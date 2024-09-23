package com.ex_dock.ex_dock.database.text_pages

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCPool
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class TextPagesJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize the eventbus connections with the text pages table
    getAllTextPages()
    getTextPageById()
    createTextPage()
    updateTextPage()
    deleteTextPage()

    // Initialize the eventbus connections with SEO text pages table
    getAllSeoTextPages()
    getSeoTextPageById()
    createSeoTextPage()
    updateSeoTextPage()
    deleteSeoTextPage()

    // Initialize the eventbus connections with Full text pages table
    getAllFullTextPages()
    getFullTextPageById()
  }

  /**
   * Get all text pages from the database
   */
  private fun getAllTextPages() {
    val allTextPagesConsumer = eventBus.localConsumer<String>("process.textPages.getAll")
    allTextPagesConsumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM text_pages").execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          json = json {
            obj(
              "textPages" to rows.map { row ->
                obj(
                  makeTextPagesJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No text pages found!")
          message.reply(json { obj("textPages" to "{}") })
        }
      }
    }
  }

  /**
   * Get all text pages with the given id
   */
  private fun getTextPageById() {
    val getTextPageByIdConsumer = eventBus.localConsumer<Int>("process.textPages.getById")
    getTextPageByIdConsumer.handler { message ->
      val textPageId = message.body()
      val rowsFuture = client.preparedQuery("SELECT * FROM text_pages WHERE text_pages_id =?")
        .execute(Tuple.of(textPageId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          json = json {
            obj(
              makeTextPagesJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No text page found with ID")
        }
      }
    }
  }

  /**
   * Create a new text page in the database
   */
  private fun createTextPage() {
    val createTextPageConsumer = eventBus.consumer<JsonObject>("process.textPages.create")
    createTextPageConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO text_pages (name, short_text, text) VALUES (?,?,?)"
      val textPageTuple =  makeTextPagesTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(textPageTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.rowCount() > 0) {
          message.reply(res.property(JDBCPool.GENERATED_KEYS).getInteger(0))
        } else {
          message.reply("Failed to create text page")
        }
      }
    }
  }

  /**
   * Update an existing text page in the database
   */
  private fun updateTextPage() {
    val updateTextPageConsumer = eventBus.consumer<JsonObject>("process.textPages.update")
    updateTextPageConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE text_pages SET name =?, short_text =?, text =? WHERE text_pages_id =?"
      val textPageTuple =  makeTextPagesTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(textPageTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.rowCount() > 0) {
          message.reply("Text page updated successfully")
        } else {
          message.reply("Failed to update text page")
        }
      }
    }
  }

  /**
   * Delete an existing text page from the database
   */
  private fun deleteTextPage() {
    val deleteTextPageConsumer = eventBus.consumer<Int>("process.textPages.delete")
    deleteTextPageConsumer.handler { message ->
      val textPageId = message.body()
      val rowsFuture = client.preparedQuery("DELETE FROM text_pages WHERE text_pages_id =?")
       .execute(Tuple.of(textPageId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.rowCount() > 0) {
          message.reply("Text page deleted successfully")
        } else {
          message.reply("No text page found with ID")
        }
      }
    }
  }

  /**
   * Get all SEO text pages from the database
   */
  private fun getAllSeoTextPages() {
    val allSeoTextPagesConsumer = eventBus.consumer<String>("process.textPages.getAllSeoTextPages")
    allSeoTextPagesConsumer.handler { message ->
      val rowsFuture = client.preparedQuery("SELECT * FROM text_pages_seo").execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          json = json {
            obj(
              "seoTextPages" to rows.map { row ->
                obj(
                  makeSeoTextPagesJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No SEO text pages found!")
          message.reply(json { obj("seoTextPages" to "{}") })
        }
      }
    }
  }

  /**
   * Get all SEO text pages with the given id
   */
  private fun getSeoTextPageById() {
    val seoTextPageByIdConsumer = eventBus.consumer<Int>("process.textPages.getSeoTextPageBySeoTextPageId")
    seoTextPageByIdConsumer.handler { message ->
      val seoTextPageId = message.body()
      val rowsFuture = client.preparedQuery("SELECT * FROM text_pages_seo WHERE text_pages_id =?")
       .execute(Tuple.of(seoTextPageId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          json = json {
            obj(
              makeSeoTextPagesJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No SEO text page found with ID")
        }
      }
    }
  }

  /**
   * Create a new SEO text page in the database
   */
  private fun createSeoTextPage() {
    val createSeoTextPageConsumer = eventBus.consumer<JsonObject>("process.textPages.createSeoTextPage")
    createSeoTextPageConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO text_pages_seo (text_pages_id, meta_title, meta_description, meta_keywords, page_index) " +
          "VALUES (?,?,?,?,?::p_index)"
      val seoTextPageTuple = makeSeoTextPageTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(seoTextPageTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.rowCount() > 0) {
          message.reply(res.property(JDBCPool.GENERATED_KEYS).getInteger(0))
        } else {
          message.reply("Failed to create seo text page")
        }
      }
    }
  }

  /**
   * Update an existing SEO text page in the database
   */
  private fun updateSeoTextPage() {
    val updateSeoTextPageConsumer = eventBus.consumer<JsonObject>("process.textPages.updateSeoTextPage")
    updateSeoTextPageConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE text_pages_seo SET meta_title =?, meta_description =?, meta_keywords =?, page_index =?::p_index " +
          "WHERE text_pages_id =?"
      val seoTextPageTuple = makeSeoTextPageTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(seoTextPageTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.rowCount() > 0) {
          message.reply("SEO text page updated successfully")
        } else {
          message.reply("Failed to update seo text page")
        }
      }
    }
  }

  /**
   * Delete an existing SEO text page from the database
   */
  private fun deleteSeoTextPage() {
    val deleteSeoTextPageConsumer = eventBus.consumer<Int>("process.textPages.deleteSeoTextPage")
    deleteSeoTextPageConsumer.handler { message ->
      val seoTextPageId = message.body()
      val rowsFuture = client.preparedQuery("DELETE FROM text_pages_seo WHERE text_pages_id =?")
       .execute(Tuple.of(seoTextPageId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.rowCount() > 0) {
          message.reply("SEO text page deleted successfully")
        } else {
          message.reply("No SEO text page found with ID")
        }
      }
    }
  }

  /**
   * Get all full text page information from the database
   */
  private fun getAllFullTextPages() {
    val allFullTextPagesConsumer = eventBus.consumer<String>("process.textPages.getAllFullTextPages")
    allFullTextPagesConsumer.handler { message ->
      val query = "SELECT * FROM text_pages " +
        "INNER JOIN text_pages_seo ON text_pages.text_pages_id = text_pages_seo.text_pages_id"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          json = json {
            obj(
              "fullTextPages" to rows.map { row ->
                obj(
                  makeFullTextPagesJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          println("No full text pages found!")
          message.reply(json { obj("fullTextPages" to "{}") })
        }
      }
    }
  }

  /**
   * Get full text page information with the given id
   */
  private fun getFullTextPageById() {
    val fullTextPageByIdConsumer = eventBus.consumer<Int>("process.textPages.getFullTextPageByFullTextPageId")
    fullTextPageByIdConsumer.handler { message ->
      val fullTextPageId = message.body()
      val query =
        "SELECT * FROM text_pages " +
          "INNER JOIN text_pages_seo ON text_pages.text_pages_id = text_pages_seo.text_pages_id " +
          "WHERE text_pages.text_pages_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(fullTextPageId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          json = json {
            obj(
              makeFullTextPagesJsonFields(rows.first())
            )
          }

          message.reply(json)
        } else {
          message.reply("No full text page found with ID")
        }
      }
    }
  }

  /**
   * Create the JSON fields for the text pages
   *
   * @param row The row given from the database
   * @return The JSON fields from the database row
   */
  private fun makeTextPagesJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "text_pages_id" to row.getInteger("text_pages_id"),
      "name" to row.getString("name"),
      "short_text" to row.getString("short_text"),
      "text" to row.getString("text")
    )
  }

  /**
   * Create the JSON fields for the SEO text pages
   *
   * @param row The row given from the database
   * @return The JSON fields from the database row
   */
  private fun makeSeoTextPagesJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "text_pages_id" to row.getInteger("text_pages_id"),
      "meta_title" to row.getString("meta_title"),
      "meta_description" to row.getString("meta_description"),
      "meta_keywords" to row.getString("meta_keywords"),
      "page_index" to row.getString("page_index"),
    )
  }

  /**
   * Create the JSON fields for the full text page information
   *
   * @param row The row given from the database
   * @return The JSON fields from the database row
   */
  private fun makeFullTextPagesJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "text_pages_id" to row.getInteger("text_pages_id"),
      "name" to row.getString("name"),
      "short_text" to row.getString("short_text"),
      "text" to row.getString("text"),
      "meta_title" to row.getString("meta_title"),
      "meta_description" to row.getString("meta_description"),
      "meta_keywords" to row.getString("meta_keywords"),
      "page_index" to row.getString("page_index")
    )
  }

  /**
   * Create a tuple for the text pages with the given body
   *
   * @param body The JSON body of the request
   * @param putRequest if the given request is a put request
   * @return A tuple for the text pages with the given body
   */
  private fun makeTextPagesTuple(body: JsonObject, putRequest: Boolean): Tuple {

    val textPagesTuple: Tuple = if (putRequest) {
      Tuple.of(
        body.getString("name"),
        body.getString("short_text"),
        body.getString("text"),
        body.getInteger("text_pages_id")
      )
    } else {
      Tuple.of(
        body.getString("name"),
        body.getString("short_text"),
        body.getString("text")
      )
    }

    return textPagesTuple
  }

  /**
   * Create a tuple for the SEO text pages with the given body
   *
   * @param body The JSON body of the request
   * @param putRequest if the given request is a put request
   * @return A Tuple for the SEO text pages with the given body
   */
  private fun makeSeoTextPageTuple(body: JsonObject, putRequest: Boolean): Tuple {
    val metaTitle = try {
      body.getString("meta_title")
    } catch (e: NullPointerException) { null }
    val metaDescription = try {
      body.getString("meta_description")
    } catch (e: NullPointerException) { null }
    val metaKeywords = try {
      body.getString("meta_keywords")
    } catch (e: NullPointerException) { null }

    val seoTextPageTuple: Tuple = if (putRequest) {
      Tuple.of(
        metaTitle,
        metaDescription,
        metaKeywords,
        body.getString("page_index"),
        body.getInteger("text_pages_id")
      )
    } else {
      Tuple.of(
        body.getInteger("text_pages_id"),
        metaTitle,
        metaDescription,
        metaKeywords,
        body.getString("page_index")
      )
    }

    return seoTextPageTuple
  }
}
