package com.ex_dock.ex_dock.frontend.template_engine

import com.ex_dock.ex_dock.database.connection.getConnection
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateDataCodec
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.StringLoader
import io.pebbletemplates.pebble.template.PebbleTemplate
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple
import java.io.StringWriter

class TemplateEngineVerticle: CoroutineVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val engine = PebbleEngine.Builder().loader(StringLoader()).build()
  private var compiledTemplates: MutableMap<String, PebbleTemplate> = mutableMapOf()

  override suspend fun start() {
    client = getConnection(vertx)
    eventBus = vertx.eventBus()

    singleUseTemplate()
    compiledTemplate()
  }

  private fun singleUseTemplate() {
    eventBus.registerCodec(SingleUseTemplateDataCodec())
    eventBus.consumer<SingleUseTemplateData>("template.generate.singleUse") { message ->
      val singleUseTemplateData: SingleUseTemplateData = message.body()

      val compiledTemplate = engine.getTemplate(singleUseTemplateData.template)

      val writer = StringWriter()
      compiledTemplate.evaluate(writer, singleUseTemplateData.templateData)

      message.reply(writer.toString())
    }
  }

  // TODO: test
  private suspend fun compiledTemplate() {
    compileAllTemplates()

    // TODO: make a global data variable for this verticle
    // TEMP:
    var data: Map<String, Any?> = mapOf(
      "name" to "testName",
      "secondName" to "testSecondName"
    )

    eventBus.consumer<String>("template.generate.compiled") { message ->
      var compiledTemplate: PebbleTemplate? = compiledTemplates[message.body()]

      if (compiledTemplate != null) {
        val writer = StringWriter()
        compiledTemplate.evaluate(writer, data)
        message.reply(writer.toString())
        return@consumer
      }

      client.preparedQuery(
        "SELECT template_key, template_data FROM templates WHERE template_key=?"
      ).execute(Tuple.of(message.body())).onFailure { e ->
        println("Failed to execute query: $e")
        message.reply("A failure occurred during the generation of the template, attempted template: ${message.body()}")
      }.onSuccess { rowSet: RowSet<Row> ->
        if (rowSet.rowCount() > 0) {
          compiledTemplate = engine.getTemplate(rowSet.first().getString("template_data"))
          compiledTemplates.put(
            rowSet.first().getString("template_key"),
            compiledTemplate
          )

          val writer = StringWriter()
          compiledTemplate.evaluate(writer, data)
          message.reply(writer.toString())
          return@onSuccess
        }
        message.reply("The '${message.body()}' template does not exist in the database")
      }
    }
  }

  private suspend fun compileAllTemplates() {
    var templates: MutableMap<String, String> = mutableMapOf()
    val templatesRowSet: RowSet<Row> = client.preparedQuery(
      "SELECT template_key, template_data FROM templates"
    ).execute().coAwait()

    templatesRowSet.forEach { templateRow ->
      templates.put(
        templateRow.getString("template_key"),
        templateRow.getString("template_data")
      )
    }

    // Compile all the templates
    for (template in templates) {
      compiledTemplates.put(template.key, engine.getTemplate(template.value))
    }
  }
}