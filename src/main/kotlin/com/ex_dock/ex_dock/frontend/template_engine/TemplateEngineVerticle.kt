package com.ex_dock.ex_dock.frontend.template_engine

import com.ex_dock.ex_dock.database.connection.getConnection
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateDataCodec
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.StringLoader
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Tuple
import java.io.StringWriter
import java.util.concurrent.TimeUnit

class TemplateEngineVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private lateinit var templateCache: LoadingCache<String, TemplateCacheData>
  private val engine = PebbleEngine.Builder().loader(StringLoader()).build()

  private val expireDuration = 10L
  private val refreshDuration = 1L
  private val maxHitCount = 100

  override fun start() {
    client = getConnection(vertx)
    eventBus = vertx.eventBus()

    templateCache = Caffeine.newBuilder()
      .expireAfterWrite(expireDuration, TimeUnit.MINUTES)
      .refreshAfterWrite(refreshDuration, TimeUnit.MINUTES)
      .build { k -> loadTemplateCacheData(k)}

    singleUseTemplate()
    getCompiledTemplate()
  }

  private fun singleUseTemplate() {
    eventBus.registerCodec(SingleUseTemplateDataCodec())
    eventBus.consumer("template.generate.singleUse") { message ->
      val singleUseTemplateData: SingleUseTemplateData = message.body()

      val compiledTemplate = engine.getTemplate(singleUseTemplateData.template)

      val writer = StringWriter()
      compiledTemplate.evaluate(writer, singleUseTemplateData.templateData)

      message.reply(writer.toString())
    }
  }

  private fun getCompiledTemplate() {
    eventBus.consumer("template.generate.compiled") { message ->
      val key = message.body()
      var templateString = ""

      val future: Future<Unit> = Future.future{ promise ->
        val templateCacheData = templateCache[key]

        // Wait until the fetching of the template is done
        templateCacheData.templateData.onFailure { err ->
          promise.fail(err.message)
        }.onSuccess { res ->
          incrementTemplateHitCount(key)
          templateString = res
          promise.complete()
        }
      }

      future.onComplete { ar ->
        if (ar.succeeded()) {
          message.reply(templateString)
        } else {
          message.fail(500, ar.cause().message)
        }
      }
    }
  }

  private fun incrementTemplateHitCount(key: String) {
    val templateCacheData = templateCache.getIfPresent(key)

    if (templateCacheData != null) {
      if (templateCacheData.hits >= maxHitCount || templateCacheData.flag) {
        templateCache.invalidate(key)
        println("CACHE DATA EXPIRED")
        return
      }

      templateCacheData.hits++
      templateCache.put(key, templateCacheData)
    }
  }

  private fun loadTemplateCacheData(key: String): TemplateCacheData {
    // Initialize a new TemplateData object to avoid null values
    val templateCacheData = TemplateCacheData(
      templateData = Future.future {},
      hits = 0,
      flag = false
    )

    // Fetch the template data asynchronously
    templateCacheData.templateData  = Future.future { promise ->
      val query = "SELECT template_key, template_data, data_string FROM templates WHERE template_key = ?"
      client.preparedQuery(query).execute(Tuple.of(key)).onFailure { err ->
        println("[FAILURE] query: \"$query\" failed")
        println("err.message: ${err.message}")
        promise.fail(err)
      }.onSuccess { res ->
        // Fetch the data from the cache
        eventBus.request<Map<String, Any>>("process.cache.requestData", res.first().getString("data_string"))
          .onFailure { dataError ->
            println("Failed to load data from cache: $dataError")
            promise.fail(dataError)
          }.onSuccess { templateData ->
            val writer = StringWriter()
            val compiledTemplate = engine.getTemplate(res.first().getString("template_data"))
            compiledTemplate.evaluate(writer, templateData.body())
            promise.complete(writer.toString())
          }
      }
    }

    return templateCacheData
  }
}

private data class TemplateCacheData(
  var templateData: Future<String>,
  var hits: Int,
  var flag: Boolean
)
