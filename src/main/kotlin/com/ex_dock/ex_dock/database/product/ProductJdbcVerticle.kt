package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet

class ProductJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllProducts()
  }

  private fun getAllProducts() {
    val consumer = eventBus.localConsumer<Any>("process.products.getAll")
    consumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM products").execute()
      var json: Unit;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          json = json {
            obj(
              "products" to rows.map { row ->
                obj(
                  "id" to row.getInteger("product_id"),
                  "name" to row.getString("name"),
                  "short_name" to row.getString("short_name"),
                  "description" to row.getString("description"),
                  "short_description" to row.getString("short_description")
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("{}")
        }
      }
    }
  }
}
