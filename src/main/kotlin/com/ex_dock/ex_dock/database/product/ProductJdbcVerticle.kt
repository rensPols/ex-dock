package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple

class ProductJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllProducts()
    getProductById()
    createProduct()
  }

  private fun getAllProducts() {
    val consumer = eventBus.localConsumer<JsonObject>("process.products.getAll")
    consumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM products").execute()
      var json: JsonObject;

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

  private fun getProductById() {
    val consumer = eventBus.localConsumer<JsonObject>("process.products.getProductById")
    consumer.handler { message ->
      var json: JsonObject
      val productId = message.body().getString("productId")
      val rowsFuture = client.preparedQuery("SELECT * FROM products WHERE product_id = ?")
        .execute(Tuple.of(productId))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess{ res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          val row = rows.first()
          json = json {
            obj(
              "id" to row.getInteger("product_id"),
              "name" to row.getString("name"),
              "short_name" to row.getString("short_name"),
              "description" to row.getString("description"),
              "short_description" to row.getString("short_description")
            )
          }
          message.reply(json)
        } else {
          message.reply("{}")
        }
      }
    }
  }

  private fun createProduct() {
    val consumer = eventBus.localConsumer<JsonObject>("process.products.createProduct")
    consumer.handler { message ->
      val product = message.body()
      val rowsFuture = client.preparedQuery("INSERT INTO products (name, short_name, description, short_description) VALUES (?,?,?,?)")
       .execute(Tuple.of(
         product.getString("name").orEmpty(),
           product.getString("short_name").orEmpty(),
           product.getString("description").orEmpty(),
           product.getString("short_description").orEmpty()))
      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess{ res ->
        message.reply("Success!")
      }
    }
  }
}
