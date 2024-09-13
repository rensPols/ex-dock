package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.Pool

class AccountJdbcVerticle: CoroutineVerticle() {

  private lateinit var client: Pool

  private lateinit var eventBus: EventBus

  override suspend fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAccountData()
  }

  private suspend fun getAll(ctx: RoutingContext) {
    val rows = client.preparedQuery("SELECT * FROM importance").execute().coAwait()
    if (rows.size() > 0) {
      ctx.response().end(json {
        obj("importance_levels" to rows.iterator().next().getInteger("importance_levels")).encode()
      })
    } else {
      ctx.response().setStatusCode(404).end()
    }
  }

  private fun getAccountData() {
    val consumer = eventBus.localConsumer<String>("process.account.getData")
    consumer.handler { message ->
      var body = message.body()
      val json = JsonObject("""{"username":"$body"}""")
      println("Recieved account request")
      message.reply(json)
    }
  }
}
