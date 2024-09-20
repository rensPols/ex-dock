package com.ex_dock.ex_dock.database.url

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class UrlJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize all eventbus connections with the url_keys table
    getAllUrlKeys()
    getUrlByKey()
    createUrlKey()
    updateUrlKey()
    deleteUrlKey()

    // Initialize all eventbus connections with the text_page_urls table
    getAllTextPageUrls()
    getTextPageUrlByKey()
    createTextPageUrl()
    updateTextPageUrl()
    deleteTextPageUrl()

    // Initialize all eventbus connections with the category_urls table
    getAllCategoryUrls()
    getCategoryUrlByKey()
    createCategoryUrl()
    updateCategoryUrl()
    deleteCategoryUrl()

    // Initialize all eventbus connections with the product_urls table
    getAllProductUrls()
    getProductUrlByKey()
    createProductUrl()
    updateProductUrl()
    deleteProductUrl()

    // Initialize all eventbus connections with the full_urls table structure
    getAllFullUrls()
    getFullUrlByKey()
  }

  /**
   * Get all url keys from the database
   */
  private fun getAllUrlKeys() {
    val getAllUrlKeysConsumer = eventBus.consumer<String>("process.url.getAllUrlKeys")
    getAllUrlKeysConsumer.handler { message ->
      val query = "SELECT * FROM url_keys"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = json {
            obj(
              "urlKeys" to rows.map { row ->
                obj(
                  makeUrlKeyJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No url keys found!")
          message.reply(json { obj("urlKeys" to "{}") })
        }
      }
    }
  }

  /**
   * Get a specific url key from the database by url_key and upper_key
   */
  private fun getUrlByKey() {
    val getUrlByKeyConsumer = eventBus.consumer<JsonObject>("process.url.getUrlByKey")
    getUrlByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM url_keys WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(
        body.getString("urlKey"),
        body.getString("upperKey")
      ))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          val row = res.result()
          json = json {
            obj(
              makeUrlKeyJsonFields(row.first())
            )
          }
          message.reply(json)
        } else {
          println("No url key found!")
          message.reply(json { obj("urlKey" to "{}") })
        }
      }
    }
  }

  /**
   * Create a new url key in the database
   */
  private fun createUrlKey() {
    val createUrlKeyConsumer = eventBus.consumer<JsonObject>("process.url.createUrlKey")
    createUrlKeyConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO url_keys (url_key, upper_key, page_type) VALUES (?,?,?::p_type)"
      val urlKeyTuple = makeUrlKeyTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(urlKeyTuple)
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.result().rowCount() > 0) {
          json = json {
            obj(
              "message" to "Url key created successfully"
            )
          }
          message.reply(json)
        } else {
          println("Failed to create url key!")
          message.reply("Failed to create url key")
        }
      }
    }
  }

  /**
   * Update an existing url key in the database
   */
  private fun updateUrlKey() {
    val updateUrlKeyConsumer = eventBus.consumer<JsonObject>("process.url.updateUrlKey")
    updateUrlKeyConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE url_keys SET page_type =? WHERE url_key =? AND upper_key =?"
      val urlKeyTuple = makeUrlKeyTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(urlKeyTuple)
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.result().rowCount() > 0) {
          json = json {
            obj(
              "message" to "Url key updated successfully"
            )
          }
          message.reply(json)
        } else {
          println("No url key found to update!")
          message.reply("Failed to update url key")
        }
      }
    }
  }

  /**
   * Delete an existing url key from the database
   */
  private fun deleteUrlKey() {
    val deleteUrlKeyConsumer = eventBus.consumer<JsonObject>("process.url.deleteUrlKey")
    deleteUrlKeyConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM url_keys WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(
        body.getString("urlKey"),
        body.getString("upperKey")
      ))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.result().rowCount() > 0) {
          json = json {
            obj(
              "message" to "Url key deleted successfully"
            )
          }
          message.reply(json)
        } else {
          println("No url key found to delete!")
          message.reply("Failed to delete url key")
        }
      }
    }
  }

  /**
   * Get all text page urls from the database
   */
  private fun getAllTextPageUrls() {
    val getAllTextPageUrlsConsumer = eventBus.consumer<String>("process.url.getAllTextPageUrls")
    getAllTextPageUrlsConsumer.handler { message ->
      val query = "SELECT * FROM text_pages"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = json {
            obj(
              "textPageUrls" to rows.map { row ->
                obj(
                  makeTextPageUrlJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No text page urls found!")
          message.reply(json { obj("textPageUrls" to "{}") })
        }
      }
    }
  }

  /**
   * Get a specific text page url from the database by url_key and upper_key
   */
  private fun getTextPageUrlByKey() {
    val getTextPageUrlByKeyConsumer = eventBus.consumer<JsonObject>("process.url.getTextPageUrlByKey")
    getTextPageUrlByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM text_page_urls WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          val row = res.result()
          json = json {
            obj(
              makeTextPageUrlJsonFields(row.first())
            )
          }
          message.reply(json)
        } else {
          println("No text page url found!")
          message.reply(json { obj("textPageUrl" to "{}") })
        }
      }
    }
  }

  /**
   * Create a new text page url in the database
   */
  private fun createTextPageUrl() {
    val createTextPageUrlConsumer = eventBus.consumer<JsonObject>("process.url.createTextPageUrl")
    createTextPageUrlConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO text_page_urls (url_key, upper_key, text_pages_id) VALUES (?,?,?)"
      val textPageUrlTuple = makeTextPageUrlTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(textPageUrlTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Text page url created successfully")
        } else {
          message.reply("Failed to create text page url")
        }
      }
    }
  }

  /**
   * Update an existing text page url in the database
   */
  private fun updateTextPageUrl() {
    val updateTextPageUrlConsumer = eventBus.consumer<JsonObject>("process.url.updateTextPageUrl")
    updateTextPageUrlConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE text_page_urls SET url_key =?, upper_key =?, " +
        "text_pages_id =? WHERE url_key =? AND upper_key =?"
      val textPageUrlTuple = makeTextPageUrlTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(textPageUrlTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Text page url updated successfully")
        } else {
          message.reply("Failed to update text page url")
        }
      }
    }
  }

  /**
   * Delete an existing text page url from the database
   */
  private fun deleteTextPageUrl() {
    val deleteTextPageUrlConsumer = eventBus.consumer<JsonObject>("process.url.deleteTextPageUrl")
    deleteTextPageUrlConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM text_page_urls WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Text page url deleted successfully")
        } else {
          message.reply("Failed to delete text page url")
        }
      }
    }
  }

  /**
   * Get all category urls from the database
   */
  private fun getAllCategoryUrls() {
    val getAllCategoryUrlsConsumer = eventBus.consumer<String>("process.url.getAllCategoryUrls")
    getAllCategoryUrlsConsumer.handler { message ->
      val query = "SELECT * FROM category_urls"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = json {
            obj(
              "categoryUrls" to rows.map { row ->
                obj(
                  makeCategoryUrlJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No category urls found!")
          message.reply(json { obj("categoryUrls" to "{}") })
        }
      }
    }
  }

  /**
   * Get a specific category url from the database by url_key and upper_key
   */
  private fun getCategoryUrlByKey() {
    val getCategoryUrlByKeyConsumer = eventBus.consumer<JsonObject>("process.url.getCategoryUrlByKey")
    getCategoryUrlByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM category_urls WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          val row = res.result()
          json = json {
            obj(
              makeCategoryUrlJsonFields(row.first())
            )
          }
          message.reply(json)
        } else {
          println("No category url found!")
          message.reply(json { obj("categoryUrl" to "{}") })
        }
      }
    }
  }

  /**
   * Create a new category url in the database
   */
  private fun createCategoryUrl() {
    val createCategoryUrlConsumer = eventBus.consumer<JsonObject>("process.url.createCategoryUrl")
    createCategoryUrlConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO category_urls (url_key, upper_key, category_id) VALUES (?,?,?)"
      val categoryUrlTuple = makeCategoryUrlTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(categoryUrlTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Category url created successfully")
        } else {
          message.reply("Failed to create category url")
        }
      }
    }
  }

  /**
   * Update an existing category url in the database
   */
  private fun updateCategoryUrl() {
    val updateCategoryUrlConsumer = eventBus.consumer<JsonObject>("process.url.updateCategoryUrl")
    updateCategoryUrlConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE category_urls SET url_key =?, upper_key =?, category_id =? " +
        "WHERE url_key =? AND upper_key =?"
      val categoryUrlTuple = makeCategoryUrlTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(categoryUrlTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Category url updated successfully")
        } else {
          message.reply("Failed to update category url")
        }
      }
    }
  }

  /**
   * Delete an existing category url from the database
   */
  private fun deleteCategoryUrl() {
    val deleteCategoryUrlConsumer = eventBus.consumer<JsonObject>("process.url.deleteCategoryUrl")
    deleteCategoryUrlConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM category_urls WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Category url deleted successfully")
        } else {
          message.reply("Failed to delete category url")
        }
      }
    }
  }

  /**
   * Get all product urls from the database
   */
  private fun getAllProductUrls() {
    val getAllProductUrlsConsumer = eventBus.consumer<String>("process.url.getAllProductUrls")
    getAllProductUrlsConsumer.handler { message ->
      val query = "SELECT * FROM product_urls"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = json {
            obj(
              "productUrls" to rows.map { row ->
                obj(
                  makeProductUrlJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No product urls found!")
          message.reply(json { obj("productUrls" to "{}") })
        }
      }
    }
  }

  /**
   * Get a specific product url from the database by url_key and upper_key
   */
  private fun getProductUrlByKey() {
    val getProductUrlByKeyConsumer = eventBus.consumer<JsonObject>("process.url.getProductUrlByKey")
    getProductUrlByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM product_urls WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          val row = res.result()
          json = json {
            obj(
              makeProductUrlJsonFields(row.first())
            )
          }
          message.reply(json)
        } else {
          println("No product url found!")
          message.reply(json { obj("productUrl" to "{}") })
        }
      }
    }
  }

  /**
   * Create a new product url in the database
   */
  private fun createProductUrl() {
    val createProductUrlConsumer = eventBus.consumer<JsonObject>("process.url.createProductUrl")
    createProductUrlConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO product_urls (url_key, upper_key, product_id) VALUES (?,?,?)"
      val productUrlTuple = makeProductUrlTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(productUrlTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Product url created successfully")
        } else {
          message.reply("Failed to create product url")
        }
      }
    }
  }

  /**
   * Update an existing product url in the database
   */
  private fun updateProductUrl() {
    val updateProductUrlConsumer = eventBus.consumer<JsonObject>("process.url.updateProductUrl")
    updateProductUrlConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE product_urls SET url_key =?, upper_key =?, product_id =? " +
        "WHERE url_key =? AND upper_key =?"
      val productUrlTuple = makeProductUrlTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(productUrlTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Product url updated successfully")
        } else {
          message.reply("Failed to update product url")
        }
      }
    }
  }

  /**
   * Delete an existing product url from the database
   */
  private fun deleteProductUrl() {
    val deleteProductUrlConsumer = eventBus.consumer<JsonObject>("process.url.deleteProductUrl")
    deleteProductUrlConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM product_urls WHERE url_key =? AND upper_key =?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Product url deleted successfully")
        } else {
          message.reply("Failed to delete product url")
        }
      }
    }
  }

  /**
   * Get all full urls from the database
   */
  private fun getAllFullUrls() {
    val getAllFullUrlsConsumer = eventBus.consumer<JsonObject>("process.url.getAllFullUrls")
    getAllFullUrlsConsumer.handler { message ->
      val body = message.body()
      val joinList = checkJoinMessage(body)
      val query = makeFullUrlKeyQuery(joinList)

      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = json {
            obj(
              "fullUrls" to rows.map { row ->
                obj(
                  makeFullUrlKeyJsonFields(row, joinList)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No full urls found!")
          message.reply(json { obj("fullUrls" to "{}") })
        }
      }
    }
  }

  /**
   * Get a specific full url from the database by url_key and upper_key
   */
  private fun getFullUrlByKey() {
    val getFullUrlByKeyConsumer = eventBus.consumer<JsonObject>("process.url.getFullUrlByKey")
    getFullUrlByKeyConsumer.handler { message ->
      val body = message.body()
      val joinList = checkJoinMessage(body)
      val query = makeFullUrlKeyQuery(joinList)

      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(
          body.getString("urlKey"),
          body.getString("upperKey")
        )
      )
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          val row = res.result().first()
          json = json {
            obj(
              makeFullUrlKeyJsonFields(row, joinList)
            )
          }
          message.reply(json)
        } else {
          println("No full url found!")
          message.reply( json { obj("fullUrl" to "{}") })
        }
      }
    }
  }

  /**
   * Create JSON fields for the url keys
   *
   * @param row The row from the database
   * @return A list with the converted fields from the database
   */
  private fun makeUrlKeyJsonFields(row: Row): List<Pair<String, Any?>> {
    return  listOf(
      "url_key" to row.getString("url_key"),
      "upper_key" to row.getString("upper_key"),
      "page_type" to row.getString("page_type")
    )
  }

  /**
   * Create JSON fields for the text page urls
   *
   * @param row The row from the database
   * @return A list with the converted fields from the database
   */
  private fun makeTextPageUrlJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "url_key" to row.getString("url_key"),
      "upper_key" to row.getString("upper_key"),
      "text_pages_id" to row.getString("text_pages_id")
    )
  }

  /**
   * Create JSON fields for the category urls
   *
   * @param row The row from the database
   * @return A list with the converted fields from the database
   */
  private fun makeCategoryUrlJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "url_key" to row.getString("url_key"),
      "upper_key" to row.getString("upper_key"),
      "category_id" to row.getString("category_id")
    )
  }

  /**
   * Create JSON fields for the product urls
   *
   * @param row The row from the database
   * @return A list with the converted fields from the databas
   */
  private fun makeProductUrlJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "url_key" to row.getString("url_key"),
      "upper_key" to row.getString("upper_key"),
      "product_id" to row.getString("product_id")
    )
  }

  /**
   * Create JSON fields for the full url key query
   *
   * @param row The row from the database
   * @param joinList A list of booleans indicating whether to join with text_pages, categories, or products
   * @return The constructed query string
   */
  private fun makeFullUrlKeyJsonFields(row: Row, joinList: List<Boolean>): List<Pair<String, Any?>> {
    val fieldList: MutableList<Pair<String, Any?>> = mutableListOf(
      "url_key" to row.getString("url_key"),
      "upper_key" to row.getString("upper_key"),
      "page_type" to row.getString("page_type")
    )

    if (joinList[0]) {
      fieldList.add("text_pages_id" to row.getString("text_pages_id"))
      fieldList.add("text_page_name" to row.getString("text_page_name"))
      fieldList.add("text_page_short_text" to row.getString("text_page_short_text"))
      fieldList.add("text_page_text" to row.getString("text_page_text"))
    }

    if (joinList[1]) {
      fieldList.add("category_id" to row.getString("category_id"))
      fieldList.add("upper_category" to row.getString("upper_category"))
      fieldList.add("category_name" to row.getString("category_name"))
      fieldList.add("category_short_description" to row.getString("category_short_description"))
      fieldList.add("category_description" to row.getString("category_description"))
    }

    if (joinList[2]) {
      fieldList.add("product_id" to row.getString("product_id"))
      fieldList.add("product_name" to row.getString("product_name"))
      fieldList.add("product_short_name" to row.getString("product_short_name"))
      fieldList.add("product_description" to row.getString("product_description"))
      fieldList.add("product_short_description" to row.getString("product_short_description"))
    }

    return fieldList
  }

  /**
   * Makes a Tuple for the url key query
   *
   * @param body The body to convert into a Tuple
   * @param isPutRequest if the request is a put request
   * @return The Tuple with the data from the body
   */
  private fun makeUrlKeyTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val urlKeyTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("url_key"),
        body.getString("upper_key"),
        body.getString("page_type")
      )
    } else {
      Tuple.of(
        body.getString("page_type"),
        body.getString("url_key"),
        body.getString("upper_key")
      )
    }

    return urlKeyTuple
  }

  /**
   * Makes a Tuple for the text page url
   *
   * @param body The body to convert into a Tuple
   * @param isPutRequest if the request is a put request
   * @return The Tuple with the data from the body
   */
  private fun makeTextPageUrlTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val textPageUrlTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("url_key"),
        body.getString("upper_key"),
        body.getString("text_pages_id")
      )
    } else {
      Tuple.of(
        body.getString("text_pages_id"),
        body.getString("url_key"),
        body.getString("upper_key")
      )
    }

    return textPageUrlTuple
  }

  /**
   * Makes a Tuple for the category url
   *
   * @param body The body to convert into a Tuple
   * @param isPutRequest if the request is a put request
   * @return The Tuple with the data from the body
   */
  private fun makeCategoryUrlTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val categoryUrlTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("url_key"),
        body.getString("upper_key"),
        body.getString("category_id")
      )
    } else {
      Tuple.of(
        body.getString("category_id"),
        body.getString("url_key"),
        body.getString("upper_key")
      )
    }

    return categoryUrlTuple
  }

  /**
   * Makes a Tuple for the product url
   *
   * @param body The body to convert into a Tuple
   * @param isPutRequest if the request is a put request
   * @return The Tuple with the data from the body
   */
  private fun makeProductUrlTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val productUrlTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("url_key"),
        body.getString("upper_key"),
        body.getString("product_id")
      )
    } else {
      Tuple.of(
        body.getString("product_id"),
        body.getString("url_key"),
        body.getString("upper_key")
      )
    }

    return productUrlTuple
  }

  /**
   * Checks which tables to join from a JsonObject
   *
   * @param body The body which has the information of the joins
   * @return A list with booleans of the tables to join
   */
  private fun checkJoinMessage(body: JsonObject): MutableList<Boolean> {
    val joinList: MutableList<Boolean> = mutableListOf(false, false, false)

    joinList[0]= try {
      body.getBoolean("joinTextPage")
    } catch (e: NullPointerException) { false}
    joinList[1] = try {
      body.getBoolean("joinCategory")
    } catch (e: NullPointerException) { false }
    joinList[2] = try {
      body.getBoolean("joinProduct")
    } catch (e: NullPointerException) { false }

    return joinList
  }

  /**
   * The query constructor for full url key search
   *
   * @param joinList The list of booleans of the tables to join
   * @return A query string to run on the query
   */
  private fun makeFullUrlKeyQuery(joinList: List<Boolean>): String {
    var query = renameFullUrlKeyColumns(joinList)

    if (joinList[0]) {
      query += "INNER JOIN text_page_urls tpu ON uk.url_key = tpu.url_key AND uk.upper_key = " +
        "tpu.upper_key " +
        "INNER JOIN public.text_pages tp on tp.text_pages_id = tpu.text_pages_id"
    }
    if (joinList[1]) {
      query += "INNER JOIN category_urls cu ON uk.url_key = cu.url_key AND uk.upper_key = " +
        "cu.upper_key INNER JOIN public.categories c on c.category_id = cu.category_id"
    }
    if (joinList[2]) {
      query += "INNER JOIN public.product_urls pu ON yk.url_key = " +
        "pu.url_key AND uk.upper_key = pu.upper_key " +
        "INNER JOIN public.products p ON p.product_id = pu.product_id"
    }

    client.preparedQuery(query).execute()

    return query
  }

  /**
   * A query constructor to rename all the query parameters so
   * that they don't conflict with different tables
   *
   * @param joinList the list of booleans of which tables to join
   * @return A query with changed parameters
   */
  private fun renameFullUrlKeyColumns(joinList: List<Boolean>): String {
    var columnNamesQuery = "SELECT uk.url_key, uk.upper_key, uk.page_type"

    if (joinList[0]) {
      columnNamesQuery += ", tp.text_pages_id, tp.name AS text_page_name, " +
        "tp.short_text AS text_page_short_text, tp.text AS text_page_text"
    }

    if (joinList[1]) {
      columnNamesQuery += ", c.category_id, c.upper_category, c.name AS category_name, " +
        "c.short_description AS category_short_description, c.description AS category_description"
    }

    if (joinList[2]) {
      columnNamesQuery += ", p.product_id, p.name AS product_name, p.short_name AS product_short_name, " +
        "p.description AS product_description, p.short_description AS product_short_description"
    }

    columnNamesQuery += " FROM url_keys uk "
    return columnNamesQuery
  }
}
