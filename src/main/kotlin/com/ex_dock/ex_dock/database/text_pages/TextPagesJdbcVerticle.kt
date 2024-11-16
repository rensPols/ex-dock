package com.ex_dock.ex_dock.database.text_pages

import com.ex_dock.ex_dock.database.category.PageIndex
import com.ex_dock.ex_dock.database.connection.getConnection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class TextPagesJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  private val textPagesDeliveryOptions = DeliveryOptions().setCodecName("TextPagesCodec")
  private val seoTextPagesDeliveryOptions = DeliveryOptions().setCodecName("TextPagesSeoCodec")
  private val fullTextPagesDeliveryOptions = DeliveryOptions().setCodecName("FullTextPagesCodec")
  private val listDeliveryOptions = DeliveryOptions().setCodecName("ListCodec")

  override fun start() {
    client = getConnection(vertx)
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
      val textPagesList: MutableList<TextPages> = emptyList<TextPages>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          rows.forEach { row ->
            textPagesList.add(makeTextPages(row))
          }
        }

        message.reply(textPagesList, listDeliveryOptions)
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeTextPages(rows.first()), textPagesDeliveryOptions)
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
    val createTextPageConsumer = eventBus.consumer<TextPages>("process.textPages.create")
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
          body.textPagesId = res.property(JDBCPool.GENERATED_KEYS).getInteger(0)
          message.reply(body, textPagesDeliveryOptions)
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
    val updateTextPageConsumer = eventBus.consumer<TextPages>("process.textPages.update")
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
          message.reply(body, textPagesDeliveryOptions)
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
      val textPagesSeoList: MutableList<TextPagesSeo> = emptyList<TextPagesSeo>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          rows.forEach { row ->
            textPagesSeoList.add(makeSeoTextPages(row))
          }
        }

        message.reply(textPagesSeoList, listDeliveryOptions)
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeSeoTextPages(rows.first()), seoTextPagesDeliveryOptions)
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
    val createSeoTextPageConsumer = eventBus.consumer<TextPagesSeo>("process.textPages.createSeoTextPage")
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
          body.textPagesId = res.property(JDBCPool.GENERATED_KEYS).getInteger(0)
          message.reply(body, seoTextPagesDeliveryOptions)
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
    val updateSeoTextPageConsumer = eventBus.consumer<TextPagesSeo>("process.textPages.updateSeoTextPage")
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
          message.reply(body, seoTextPagesDeliveryOptions)
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
      val fullTextPagesList: MutableList<FullTextPages> = emptyList<FullTextPages>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          rows.forEach { row ->
            fullTextPagesList.add(makeFullTextPages(row))
          }
        }

        message.reply(fullTextPagesList, listDeliveryOptions)
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: ${res.message}")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeFullTextPages(rows.first()), fullTextPagesDeliveryOptions)
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
  private fun makeTextPages(row: Row): TextPages {
    return TextPages(
      textPagesId = row.getInteger("text_pages_id"),
      name = row.getString("name"),
      shortText = row.getString("short_text"),
      text = row.getString("text"),
    )
  }

  /**
   * Create the JSON fields for the SEO text pages
   *
   * @param row The row given from the database
   * @return The JSON fields from the database row
   */
  private fun makeSeoTextPages(row: Row): TextPagesSeo {
    return TextPagesSeo(
      textPagesId = row.getInteger("text_pages_id"),
      metaTitle = row.getString("meta_title"),
      metaDescription = row.getString("meta_description"),
      metaKeywords = row.getString("meta_keywords"),
      pageIndex = getEnum(row.getString("page_index"))
    )
  }

  /**
   * Create the JSON fields for the full text page information
   *
   * @param row The row given from the database
   * @return The JSON fields from the database row
   */
  private fun makeFullTextPages(row: Row): FullTextPages {
    return FullTextPages(
      makeTextPages(row),
      makeSeoTextPages(row)
    )
  }

  /**
   * Create a tuple for the text pages with the given body
   *
   * @param body The JSON body of the request
   * @param putRequest if the given request is a put request
   * @return A tuple for the text pages with the given body
   */
  private fun makeTextPagesTuple(body: TextPages, putRequest: Boolean): Tuple {

    val textPagesTuple: Tuple = if (putRequest) {
      Tuple.of(
        body.name,
        body.shortText,
        body.text,
        body.textPagesId
      )
    } else {
      Tuple.of(
        body.name,
        body.shortText,
        body.text
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
  private fun makeSeoTextPageTuple(body: TextPagesSeo, putRequest: Boolean): Tuple {
    val seoTextPageTuple: Tuple = if (putRequest) {
      Tuple.of(
        body.metaTitle,
        body.metaDescription,
        body.metaKeywords,
        convertEnum(body.pageIndex),
        body.textPagesId
      )
    } else {
      Tuple.of(
        body.textPagesId,
        body.metaTitle,
        body.metaDescription,
        body.metaKeywords,
        convertEnum(body.pageIndex)
      )
    }

    return seoTextPageTuple
  }

  private fun getEnum(name: String): PageIndex {
    when (name) {
      "index, follow" -> return PageIndex.IndexFollow
      "index, nofollow" -> return PageIndex.IndexNoFollow
      "noindex, follow" -> return PageIndex.NoIndexFollow
      "noindex, nofollow" -> return PageIndex.NoIndexNoFollow
    }

    return PageIndex.NoIndexNoFollow
  }

  private fun convertEnum(pageIndex: PageIndex): String {
    return when (pageIndex) {
      PageIndex.IndexFollow -> "index, follow"
      PageIndex.IndexNoFollow -> "index, nofollow"
      PageIndex.NoIndexFollow -> "noindex, follow"
      PageIndex.NoIndexNoFollow -> "noindex, nofollow"
    }
  }
}
