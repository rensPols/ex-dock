package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet

class AccountJdbcVerticle: AbstractVerticle() {

  private lateinit var client: Pool

  private lateinit var eventBus: EventBus

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAccountData()
  }

  private fun getAccountData() {
    val consumer = eventBus.localConsumer<Any>("process.account.getData")
    consumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM users").execute()
      var json: String

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          json = json {
            obj(
              "users" to rows.map { row ->
                obj(
                  "id" to row.getInteger("user_id"),
                  "email" to row.getString("email")
                )
              }
            ).encode()
          }
          message.reply(json)
        } else {
          println("No users found!")
          message.reply("{}")
        }
      }
    }
  }
}
