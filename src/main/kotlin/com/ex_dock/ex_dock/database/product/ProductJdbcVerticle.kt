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
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize all eventbus connections to the product table
    getAllProducts()
    getProductById()
    createProduct()
    updateProduct()
    deleteProduct()

    // Initialize all eventbus connections to the products_seo table
    getAllProductsSeo()
    getProductSeoById()
    createProductSeo()
    updateProductSeo()
    deleteProductSeo()

    // Initialize all eventbus connections to the products_pricing table
    getAllProductsPricing()
    getProductPricingById()
    createProductPricing()
    updateProductPricing()
    deleteProductPricing()

    // Initialize all eventbus connections to the full products info tables
    getAllFullProducts()
    getFullProductById()
  }

  private fun getAllProducts() {
    val allProductsConsumer = eventBus.localConsumer<JsonObject>("process.products.getAllProducts")
    allProductsConsumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM products").execute()
      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
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
    val getByIdConsumer = eventBus.localConsumer<Int>("process.products.getProductById")
    getByIdConsumer.handler { message ->
      var json: JsonObject
      val productId = message.body()
      val rowsFuture = client.preparedQuery("SELECT * FROM products WHERE product_id = ?")
        .execute(Tuple.of(productId))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
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
        message.reply(failedMessage)
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
        message.reply(failedMessage)
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
        message.reply(failedMessage)
      }.onSuccess{ res ->
        if (res.rowCount() > 0) {
          message.reply("Product deleted successfully")
        } else {
          message.reply("Failed to delete product")
        }
      }
    }
  }

  private fun getAllProductsSeo() {
    val allProductSeoConsumer = eventBus.localConsumer<JsonObject>("process.products.getAllProductsSeo")
    allProductSeoConsumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM products_seo").execute()
      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          json = json {
            obj(
              "productSeo" to rows.map { row ->
                obj(
                  makeProductSeoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("productSeo" to "{}") })
        }
      }
    }
  }

  private fun getProductSeoById() {
    val getProductSeoByIdConsumer = eventBus.localConsumer<Int>("process.products.getProductSeoById")
    getProductSeoByIdConsumer.handler { message ->
      val productId = message.body()
      val rowsFuture = client.preparedQuery("SELECT * FROM products_seo WHERE product_id =?")
        .execute(Tuple.of(productId))

      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          val row = rows.first()
          json = json {
            obj(
              makeProductSeoJsonFields(row)
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("productSeo" to "{}") })
        }
      }
    }
  }

  private fun createProductSeo() {
    val createProductSeoConsumer = eventBus.localConsumer<JsonObject>("process.products.createProductSeo")
    createProductSeoConsumer.handler { message ->
      val productSeo = message.body()
      val rowsFuture = client.preparedQuery("INSERT INTO products_seo (product_id, meta_title, meta_description, meta_keywords, page_index) VALUES (?,?,?,?,?::p_index)")
       .execute(makeProductSeoTuple(productSeo, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { _ ->
        message.reply("Product SEO created successfully")
      }
    }
  }

  private fun updateProductSeo() {
    val updateProductSeoConsumer = eventBus.localConsumer<JsonObject>("process.products.updateProductSeo")
    updateProductSeoConsumer.handler { message ->
      val productSeo = message.body()
      val rowsFuture = client.preparedQuery("UPDATE products_seo SET meta_title =?, meta_description =?, meta_keywords =?, page_index =?::p_index WHERE product_id =?")
       .execute(makeProductSeoTuple(productSeo, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { _ ->
        message.reply("Product SEO updated successfully")
      }
    }
  }

  private fun deleteProductSeo() {
    val deleteProductSeoConsumer = eventBus.localConsumer<Int>("process.products.deleteProductSeo")
    deleteProductSeoConsumer.handler { message ->
      val productId = message.body()
      val rowsFuture = client.preparedQuery("DELETE FROM products_seo WHERE product_id =?")
       .execute(Tuple.of(productId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { _ ->
        message.reply("Product SEO deleted successfully")
      }
    }
  }

  private fun getAllProductsPricing() {
    val allProductsPricingConsumer = eventBus.localConsumer<JsonObject>("process.products.getAllProductsPricing")
    allProductsPricingConsumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM products_pricing").execute()
      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          json = json {
            obj(
              "productsPricing" to rows.map { row ->
                obj(
                  makeProductsPricingJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("productsPricing" to "{}") })
        }
      }
    }
  }

  private fun getProductPricingById() {
    val getProductPricingByIdConsumer = eventBus.localConsumer<Int>("process.products.getProductPricingById")
    getProductPricingByIdConsumer.handler { message ->
      val productId = message.body()
      val rowsFuture = client.preparedQuery("SELECT * FROM products_pricing WHERE product_id =?")
        .execute(Tuple.of(productId))

      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          val row = rows.first()
          json = json {
            obj(
              makeProductsPricingJsonFields(row)
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("productsPricing" to "{}") })
        }
      }
    }
  }

  private fun createProductPricing() {
    val createProductPricingConsumer = eventBus.localConsumer<JsonObject>("process.products.createProductPricing")
    createProductPricingConsumer.handler { message ->
      val productPricing = message.body()
      val rowsFuture = client.preparedQuery("INSERT INTO products_pricing (product_id, price, sale_price, cost_price) VALUES (?,?,?,?)")
       .execute(makeProductsPricingTuple(productPricing, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { _ ->
        message.reply("Product pricing created successfully")
      }
    }
  }

  private fun updateProductPricing() {
    val updateProductPricingConsumer = eventBus.localConsumer<JsonObject>("process.products.updateProductPricing")
    updateProductPricingConsumer.handler { message ->
      val productPricing = message.body()
      val rowsFuture = client.preparedQuery("UPDATE products_pricing SET price =?, sale_price =?, cost_price =? WHERE product_id =?")
       .execute(makeProductsPricingTuple(productPricing, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { _ ->
        message.reply("Product pricing updated successfully")
      }
    }
  }

  private fun deleteProductPricing() {
    val deleteProductPricingConsumer = eventBus.localConsumer<Int>("process.products.deleteProductPricing")
    deleteProductPricingConsumer.handler { message ->
      val productId = message.body()
      val rowsFuture = client.preparedQuery("DELETE FROM products_pricing WHERE product_id =?")
       .execute(Tuple.of(productId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { _ ->
        message.reply("Product pricing deleted successfully")
      }
    }
  }

  private fun getAllFullProducts() {
    val allProductInfoConsumer = eventBus.localConsumer<JsonObject>("process.products.getAllFullProducts")
    allProductInfoConsumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM products " +
        "JOIN public.products_pricing pp on products.product_id = pp.product_id " +
        "JOIN public.products_seo ps on products.product_id = ps.product_id").execute()
      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          json = json {
            obj(
              "fullProducts" to rows.map { row ->
                obj(
                  makeFullProductsJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("products" to "{}") })
        }
      }
    }
  }

  private fun getFullProductById() {
    val allProductInfoByIdConsumer = eventBus.consumer<Int>("process.products.getFullProductsById")
    allProductInfoByIdConsumer.handler { message ->
      val productId = message.body()
      val rowsFuture = client.preparedQuery("SELECT * FROM products " +
        "JOIN public.products_pricing pp on products.product_id = pp.product_id " +
        "JOIN public.products_seo ps on products.product_id = ps.product_id " +
        "WHERE products.product_id =?")
       .execute(Tuple.of(productId))

      var json: JsonObject;

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onSuccess { res ->
        val rows: RowSet<Row> = res
        if (rows.size() > 0) {
          val row = rows.first()
          json = json {
            obj(
              makeFullProductsJsonFields(row)
            )
          }
          message.reply(json)
        } else {
          json = json { obj("products" to "{}") }
        }
      }
    }
  }

  private fun makeProductJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "name" to row.getString("name"),
      "short_name" to row.getString("short_name"),
      "description" to row.getString("description"),
      "short_description" to row.getString("short_description")
    )
  }

  private fun makeProductSeoJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "meta_title" to row.getString("meta_title"),
      "meta_description" to row.getString("meta_description"),
      "meta_keywords" to row.getString("meta_keywords"),
      "page_index" to row.getString("page_index")
    )
  }

  private fun makeProductsPricingJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "price" to row.getDouble("price"),
      "sale_price" to row.getDouble("sale_price"),
      "cost_price" to row.getDouble("cost_price")
    )
  }

  private fun makeFullProductsJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "name" to row.getString("name"),
      "short_name" to row.getString("short_name"),
      "description" to row.getString("description"),
      "short_description" to row.getString("short_description"),
      "meta_title" to row.getString("meta_title"),
      "meta_description" to row.getString("meta_description"),
      "meta_keywords" to row.getString("meta_keywords"),
      "page_index" to row.getString("page_index"),
      "price" to row.getDouble("price"),
      "sale_price" to row.getDouble("sale_price"),
      "cost_price" to row.getDouble("cost_price"),
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

  private fun makeProductSeoTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val productSeoTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("meta_title"),
        body.getString("meta_description"),
        body.getString("meta_keywords"),
        body.getString("page_index"),
        body.getInteger("product_id")
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getString("meta_title"),
        body.getString("meta_description"),
        body.getString("meta_keywords"),
        body.getString("page_index")
      )
    }

    return productSeoTuple
  }

  private fun makeProductsPricingTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val productsPricingTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getDouble("price"),
        body.getDouble("sale_price"),
        body.getDouble("cost_price"),
        body.getInteger("product_id")
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getDouble("price"),
        body.getDouble("sale_price"),
        body.getDouble("cost_price")
      )
    }

    return productsPricingTuple
  }
}
