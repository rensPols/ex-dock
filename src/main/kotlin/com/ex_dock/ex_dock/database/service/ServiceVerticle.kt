package com.ex_dock.ex_dock.database.service

import com.ex_dock.ex_dock.database.connection.getConnection
import com.ex_dock.ex_dock.helper.convertImage
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Tuple

class ServiceVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus

  override fun start() {
    client = getConnection(vertx)
    eventBus = vertx.eventBus()

    populateTemplateTable()
    imageConverter()
  }

  private fun populateTemplateTable() {
    eventBus.consumer<Any?>("process.service.populateTemplates").handler { message ->
      val templateList = getAllStandardTemplates()
      val query = "INSERT INTO templates (template_key, template_data) SELECT ?, ? " +
        "WHERE NOT EXISTS(SELECT * FROM templates WHERE template_key = ?)"
      for (template in templateList) {
        val rowsFuture = client.preparedQuery(query).execute(Tuple.of(
          template.templateKey,
          template.templateData,
          template.templateKey
        ))

        rowsFuture.onFailure { res ->
          println("Failed to execute query: $res")
          message.fail(500, "Failed to execute query")
        }
      }

      message.reply("Completed populating templates")
    }
  }

  private fun imageConverter() {
    eventBus.consumer("process.service.convertImage") { message ->
      val path = message.body()
      println("Got request")
      convertImage(path)
      message.reply("Image conversion completed")
    }
  }
}
