package com.ex_dock.ex_dock.database.category

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

class CategoryJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize all eventbus connections for basic categories
    getAllCategories()
    getCategoryById()
    createCategory()
    editCategory()
    deleteCategory()

    // Initialize all eventbus connections for Search Engine Optimization categories
    getAllSeoCategories()
    getSeoCategoryByCategoryId()
    createSeoCategory()
    editSeoCategory()
    deleteSeoCategory()

    // Initialize all eventbus connections for getting Full Category Information
    getAllFullCategoryInfo()
    getFullCategoryInfoByCategoryId()
  }

    /**
   * Retrieves all categories from the database and sends them as a JSON object to the specified EventBus address.
   *
   * @return None
   */
  private fun getAllCategories() {
    val getAllCategoriesConsumer = eventBus.localConsumer<Unit>("process.categories.getAll")
    getAllCategoriesConsumer.handler { message ->
      val query = "SELECT * FROM categories"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess{ res: RowSet<Row> ->
        if (res.size() > 0) {
          json = json {
            obj(
              "categories" to res.map { row ->
                obj(
                  makeCategoryJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply(json{ obj("categories" to "{}")})
        }
      }
    }
  }

  /**
   * Retrieves a category from the database based on the provided category ID.
   *
   * @return None
   */
  private fun getCategoryById() {
    val getCategoryByIdConsumer = eventBus.localConsumer<Int>("process.categories.getById")
    getCategoryByIdConsumer.handler { message ->
      val query = "SELECT * FROM categories WHERE category_id = ?"
      val id = message.body()
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(id))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess{ res: RowSet<Row> ->
        if (res.size() > 0) {
          val row = res.first()
          json = json {
            obj(
              makeCategoryJsonFields(row)
            )
          }
          message.reply(json)
        } else {
          message.reply(json{ obj("category" to "{}")})
        }
      }
    }
  }

  /**
   * Create a new category entry in the database
   */
  private fun createCategory() {
    val createCategoryConsumer = eventBus.localConsumer<JsonObject>("process.categories.create")
    createCategoryConsumer.handler { message ->
      val query = "INSERT INTO categories (upper_category, name, short_description, description) VALUES (?,?,?,?)"
      val category = message.body()

      val queryTuple: Tuple = makeCategoryTuple(category, false)

      val rowsFuture = client.preparedQuery(query).execute(queryTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { _ ->
        message.reply("Category created successfully!")
      }
    }
  }

  /**
   * Edit an existing category in the database
   */
  private fun editCategory() {
    val editCategoryConsumer = eventBus.localConsumer<JsonObject>("process.categories.edit")
    editCategoryConsumer.handler { message ->
      val query =
        "UPDATE categories SET upper_category = ?, name = ?" +
          ", short_description = ?, description = ? WHERE category_id = ?"
      val category = message.body()
      val queryTuple = makeCategoryTuple(category, true)

      val rowsFuture = client.preparedQuery(query).execute(queryTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { _ ->
        message.reply("Category updated successfully!")
      }
    }
  }

  /**
   * Delete an existing category in the database
   */
  private fun deleteCategory() {
    val deleteCategoryConsumer = eventBus.localConsumer<Int>("process.categories.delete")
    deleteCategoryConsumer.handler { message ->
      val query = "DELETE FROM categories WHERE category_id =?"
      val id = message.body()

      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(id))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { _ ->
        message.reply("Category deleted successfully!")
      }
    }
  }

  /**
   * Get all data from the SEO Categories table
   */
  private fun getAllSeoCategories() {
    val getAllSeoCategoriesConsumer = eventBus.localConsumer<Unit>("process.categories.getAllSeo")
    getAllSeoCategoriesConsumer.handler { message ->
      val query = "SELECT * FROM categories_seo"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { res: RowSet<Row> ->
        if (res.size() > 0) {
          json = json {
            obj(
              "categories" to res.map { row ->
                obj(
                  makeSeoCategoryJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        }
      }
    }
  }

  /**
   * Get the data from the SEO categories table by category ID
   */
  private fun getSeoCategoryByCategoryId() {
    val getSeoCategoryByCategoryIdConsumer = eventBus.localConsumer<Int>("process.categories.getSeoById")
    getSeoCategoryByCategoryIdConsumer.handler { message ->
      val query = "SELECT * FROM categories_seo WHERE category_id =?"
      val id = message.body()
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(id))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { res: RowSet<Row> ->
        if (res.size() > 0) {
          val row = res.first()
          json = json {
            obj(
              makeSeoCategoryJsonFields(row)
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("category" to "{}") })
        }
      }
    }
  }

  /**
   * Create a new entry in the SEO categories table
   */
  private fun createSeoCategory() {
    val createSeoCategoryConsumer = eventBus.localConsumer<JsonObject>("process.categories.createSeoCategory")
    createSeoCategoryConsumer.handler { message ->
      val query =
        "INSERT INTO categories_seo (category_id, meta_title, meta_description, meta_keywords, page_index) " +
          "VALUES (?,?,?,?,?::p_index)"
      val categorySeo = message.body()
      val queryTuple = makeSeoCategoryTuple(categorySeo, false)

      val rowsFuture = client.preparedQuery(query).execute(queryTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess{ _ ->
        message.reply("SEO category created successfully!")
      }
    }
  }

  /**
   * Edit an existing entry in the SEO categories table
   */
  private fun editSeoCategory() {
    val editSeoCategoryConsumer = eventBus.localConsumer<JsonObject>("process.categories.editSeoCategory")
    editSeoCategoryConsumer.handler { message ->
      val query =
        "UPDATE categories_seo SET meta_title =?, meta_description =?, meta_keywords =?, page_index =?::p_index " +
          "WHERE category_id =?"
      val categorySeo = message.body()

      val queryTuple = makeSeoCategoryTuple(categorySeo, true)

      val rowsFuture = client.preparedQuery(query).execute(queryTuple)

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { _ ->
        message.reply("SEO category updated successfully!")
      }
    }
  }

  /**
   * Delete an existing entry in the SEO categories table
   */
  private fun deleteSeoCategory() {
    val deleteSeoCategoryConsumer = eventBus.localConsumer<Int>("process.categories.deleteSeoCategory")
    deleteSeoCategoryConsumer.handler { message ->
      val query = "DELETE FROM categories_seo WHERE category_id =?"
      val id = message.body()

      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(id))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { _ ->
        message.reply("SEO category deleted successfully!")
      }
    }
  }

  /**
   * Get all data from the SEO Categories and Categories tables with full category info
   */
  private fun getAllFullCategoryInfo() {
    val getAllFullCategoryInfoConsumer = eventBus.localConsumer<Unit>("process.categories.getAllFullInfo")
    getAllFullCategoryInfoConsumer.handler { message ->
      val query =
        "SELECT c.category_id, c.upper_category, c.name, c.short_description, c.description, cs.meta_title" +
          ", cs.meta_description, cs.meta_keywords, cs.page_index FROM categories c " +
          "LEFT JOIN categories_seo cs ON c.category_id = cs.category_id"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { res: RowSet<Row> ->
        if (res.size() > 0) {
          json = json {
            obj(
              "categories" to res.map { row ->
                obj(
                  makeFullCategoryInfoJsonFields(row)
                )
              })
          }
          message.reply(json)
        } else {
          message.reply(json { obj("categories" to "[]") })
        }
      }
    }
  }

  /**
   * Get the data from the SEO Categories and Categories tables with full category info by category ID
   */
  private fun getFullCategoryInfoByCategoryId() {
    val getFullCategoryInfoByCategoryIdConsumer = eventBus.localConsumer<Int>("process.categories.getFullInfoById")
    getFullCategoryInfoByCategoryIdConsumer.handler { message ->
      val query =
        "SELECT c.category_id, c.upper_category, c.name, c.short_description, c.description, cs.meta_title" +
          ", cs.meta_description, cs.meta_keywords, cs.page_index FROM categories c " +
          "LEFT JOIN categories_seo cs ON c.category_id = cs.category_id WHERE c.category_id =?"
      val id = message.body()
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(id))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }.onSuccess { res ->
        if (res.size() > 0) {
          val row = res.first()
          json = json {
            obj(
              makeFullCategoryInfoJsonFields(row)
            )
          }
          message.reply(json)
        } else {
          message.reply(json { obj("category" to "{}") })
        }
      }
    }
  }

  /**
   * Helper function to make JSON fields from a given database row in the categories table
   *
   * @param row The row from the database to be converted into JSON
   */
  private fun makeCategoryJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "id" to row.getInteger("category_id"),
      "upper_category" to row.getInteger("upper_category"),
      "name" to row.getString("name"),
      "short_description" to row.getString("short_description"),
      "description" to row.getString("description")
    )
  }

  /**
   * Helper function to make JSON fields from a given database row in the categories_seo table
   *
   * @param row The row from the database to be converted into JSON
   */
  private fun makeSeoCategoryJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "id" to row.getInteger("category_id"),
      "meta_title" to row.getString("meta_title"),
      "meta_description" to row.getString("meta_description"),
      "meta_keywords" to row.getString("meta_keywords"),
      "page_index" to row.getString("page_index")
    )
  }

  /**
   * Helper function to make JSON fields from a given database row in the full category info table
   *
   * @param row The row from the database to be converted into JSON
   */
  private fun makeFullCategoryInfoJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "id" to row.getInteger("category_id"),
      "upper_category" to row.getInteger("upper_category"),
      "name" to row.getString("name"),
      "short_description" to row.getString("short_description"),
      "description" to row.getString("description"),
      "meta_title" to row.getString("meta_title"),
      "meta_description" to row.getString("meta_description"),
      "meta_keywords" to row.getString("meta_keywords"),
      "page_index" to row.getString("page_index")
    )
  }

  /**
   * Helper function to create a tuple from a given JSON object in the categories table
   *
   * @param body The JSON object to be converted into a tuple
   * @return A tuple from the given JSON object
   */
  private fun makeCategoryTuple(body: JsonObject, putRequest: Boolean): Tuple {
    val upperCategory = try {
        body.getInteger("upper_category")
    } catch (e: NullPointerException) { null }

    var categoryTuple: Tuple

    if (putRequest) {
      categoryTuple = Tuple.of(
        upperCategory,
        body.getString("name"),
        body.getString("short_description"),
        body.getString("description"),
        body.getInteger("category_id"),
      )
    } else {
      categoryTuple = Tuple.of(
        upperCategory,
        body.getString("name"),
        body.getString("short_description"),
        body.getString("description")
      )
    }

    return categoryTuple
  }

  /**
   * Helper function to create a tuple from a given JSON object in the categories_seo table
   *
   * @param body The JSON object to be converted into a tuple
   * @return A tuple from the given JSON object
   */
  private fun makeSeoCategoryTuple(body: JsonObject, putRequest: Boolean): Tuple {
    val metaTitle = try {
      body.getString("meta_title")
    } catch (e: NullPointerException) { null }
    val metaDescription = try {
      body.getString("meta_description")
    } catch (e: NullPointerException) { null }
    val metaKeywords = try {
      body.getString("meta_keywords")
    } catch (e: NullPointerException) { null }

    var categorySeoTuple: Tuple

    if (putRequest) {
      categorySeoTuple = Tuple.of(
        metaTitle,
        metaDescription,
        metaKeywords,
        body.getString("page_index"),
        body.getInteger("category_id"),
      )
    } else {
      categorySeoTuple = Tuple.of(
        body.getInteger("category_id"),
        metaTitle,
        metaDescription,
        metaKeywords,
        body.getString("page_index")
      )
    }

    return categorySeoTuple
  }
}
