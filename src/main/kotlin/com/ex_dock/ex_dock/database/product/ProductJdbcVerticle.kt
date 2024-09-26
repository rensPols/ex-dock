package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCPool
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
    updateProduct()
    deleteProduct()
  }

  private fun getAllProducts() {
    val allProductsConsumer = eventBus.localConsumer<JsonObject>("process.products.getAll")
    allProductsConsumer.handler { message ->
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
                  makeProductJsonFields(row)
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
    val getByIdConsumer = eventBus.localConsumer<JsonObject>("process.products.getProductById")
    getByIdConsumer.handler { message ->
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
              makeProductJsonFields(row)
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
    val createProductConsumer = eventBus.localConsumer<JsonObject>("process.products.createProduct")
    createProductConsumer.handler { message ->
      val product = message.body()
      val rowsFuture = client.preparedQuery("INSERT INTO products (name, short_name, description, short_description) VALUES (?,?,?,?)")
       .execute(makeProductTuple(product, false))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess{ res ->
        message.reply(res.value().property(JDBCPool.GENERATED_KEYS).getInteger(0))
      }
    }
  }

  private fun updateProduct() {
    val updateProductConsumer = eventBus.localConsumer<JsonObject>("process.products.updateProduct")
    updateProductConsumer.handler { message ->
      val product = message.body()
      val rowsFuture = client.preparedQuery("UPDATE products SET name =?, short_name =?, description =?, short_description =? WHERE product_id =?")
       .execute(makeProductTuple(product, true))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess{ res ->
        if (res.rowCount() > 0) {
          message.reply("Product updated successfully")
        } else {
          message.reply("Failed to update product")
        }
      }
    }
  }

  private fun deleteProduct() {
    val deleteProductConsumer = eventBus.localConsumer<Int>("process.products.deleteProduct")
    deleteProductConsumer.handler { message ->
      val productId = message.body()
      val rowsFuture = client.preparedQuery("DELETE FROM products WHERE product_id =?")
       .execute(Tuple.of(productId))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply("Failed!")
      }.onSuccess{ res ->
        if (res.rowCount() > 0) {
          message.reply("Product deleted successfully")
        } else {
          message.reply("Failed to delete product")
        }
      }
    }
  }

  private fun makeProductJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "id" to row.getInteger("product_id"),
      "name" to row.getString("name"),
      "short_name" to row.getString("short_name"),
      "description" to row.getString("description"),
      "short_description" to row.getString("short_description")
    )
  }

  private fun makeProductTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val productTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("name"),
        body.getString("short_name"),
        body.getString("description"),
        body.getString("short_description"),
        body.getInteger("product_id")
      )
    } else {
      Tuple.of(
        body.getString("name"),
        body.getString("short_name"),
        body.getString("description"),
        body.getString("short_description")
      )
    }

    return productTuple
  }
}
