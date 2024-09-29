package com.ex_dock.ex_dock.database.scope

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

class ScopeJdbcVerticle:  AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize all eventbus connections for the website table
    getAllWebsites()
    getWebsiteById()
    createWebsite()
    editWebsite()
    deleteWebsite()

    // Initialize all eventbus connections for the Store View table
    getAllStoreViews()
    getStoreViewById()
    createStoreView()
    editStoreView()
    deleteStoreView()

    // Initialize all eventbus connections for the Full Scope tables
    getAllScopes()
    getScopeById()
  }

  /**
   * Get all websites from the database
   */
  private fun getAllWebsites() {
    val getAllWebsitesConsumer = eventBus.consumer<Unit>("process.scope.getAllWebsites")
    getAllWebsitesConsumer.handler { message ->
      val query = "SELECT * FROM websites"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          val rows = res.result()
          if (rows.size() > 0) {
            json = json {
              obj(
                "websites" to rows.map { row ->
                  obj(
                    makeWebsiteJsonFields(row)
                  )
                }
              )
            }
            message.reply(json)
          }
        } else {
          message.reply(json{ obj("websites" to "{}")})
        }
      }
    }
  }

  /**
   * Get a website by its ID from the database
   */
  private fun getWebsiteById() {
    val getWebsiteByIdConsumer = eventBus.consumer<Int>("process.scope.getWebsiteById")
    getWebsiteByIdConsumer.handler { message ->
      val websiteId = message.body()
      val query = "SELECT * FROM websites WHERE website_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(websiteId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          val rows = res.result()
          if (rows.size() > 0) {
            json = json {
              obj(
                makeWebsiteJsonFields(rows.first())
              )
            }
            message.reply(json)
          } else {
            message.reply(json { obj("website" to "{}") })
          }
        }
      }
    }
  }

  /**
   * Create a new website in the database
   */
  private fun createWebsite() {
    val createWebsiteConsumer = eventBus.consumer<JsonObject>("process.scope.createWebsite")
    createWebsiteConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO websites (website_name) VALUES (?)"
      val websiteTuple = makeWebsiteTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(websiteTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply(res.result().property(JDBCPool.GENERATED_KEYS).getInteger(0))
        } else {
          message.reply("Failed to create website")
        }
      }
    }
  }

  /**
   * Edit an existing website in the database
   */
  private fun editWebsite() {
    val editWebsiteConsumer = eventBus.consumer<JsonObject>("process.scope.editWebsite")
    editWebsiteConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE websites SET website_name =? WHERE website_id =?"
      val websiteTuple = makeWebsiteTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(websiteTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Website updated successfully")
        } else {
          message.reply("Failed to update website")
        }
      }
    }
  }

  /**
   * Delete a website from the database
   */
  private fun deleteWebsite() {
    val deleteWebsiteConsumer = eventBus.consumer<Int>("process.scope.deleteWebsite")
    deleteWebsiteConsumer.handler { message ->
      val websiteId = message.body()
      val query = "DELETE FROM websites WHERE website_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(websiteId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Website deleted successfully")
        } else {
          message.reply("Failed to delete website")
        }
      }
    }
  }

  /**
   * Get all store views from the database
   */
  private fun getAllStoreViews() {
    val getAllStoreViewsConsumer = eventBus.consumer<Unit>("process.scope.getAllStoreViews")
    getAllStoreViewsConsumer.handler { message ->
      val query = "SELECT * FROM store_view"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          val rows = res.result()
          if (rows.size() > 0) {
            json = json {
              obj(
                "store_views" to rows.map { row ->
                  obj(
                    makeStoreViewJsonFields(row)
                  )
                }
              )
            }
            message.reply(json)
          }
        } else {
          message.reply(json { obj("store_views" to "{}") })
        }
      }
    }
  }

  /**
   * Get a store view by its ID from the database
   */
  private fun getStoreViewById() {
    val getStoreViewByIdConsumer = eventBus.consumer<Int>("process.scope.getStoreViewById")
    getStoreViewByIdConsumer.handler { message ->
      val storeViewId = message.body()
      val query = "SELECT * FROM store_view WHERE store_view_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(storeViewId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          val rows = res.result()
          if (rows.size() > 0) {
            json = json {
              obj(
                makeStoreViewJsonFields(rows.first())
              )
            }
            message.reply(json)
          } else {
            message.reply(json { obj("store_view" to "{}") })
          }
        }
      }
    }
  }

  /**
   * Create a new store view in the database
   */
  private fun createStoreView() {
    val createStoreViewConsumer = eventBus.consumer<JsonObject>("process.scope.createStoreView")
    createStoreViewConsumer.handler { message ->
      val body = message.body()
      val query = "INSERT INTO store_view (website_id, store_view_name) VALUES (?,?)"
      val storeViewTuple = makeStoreViewTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(storeViewTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply(res.result().property(JDBCPool.GENERATED_KEYS).getInteger(0))
        } else {
          message.reply("Failed to create store view")
        }
      }
    }
  }

  /**
   * Edit an existing store view in the database
   */
  private fun editStoreView() {
    val editStoreViewConsumer = eventBus.consumer<JsonObject>("process.scope.editStoreView")
    editStoreViewConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE store_view SET website_id =?, store_view_name =? WHERE store_view_id =?"
      val storeViewTuple = makeStoreViewTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(storeViewTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Store view updated successfully")
        } else {
          message.reply("Failed to update store view")
        }
      }
    }
  }

  /**
   * Delete a store view from the database
   */
  private fun deleteStoreView() {
    val deleteStoreViewConsumer = eventBus.consumer<Int>("process.scope.deleteStoreView")
    deleteStoreViewConsumer.handler { message ->
      val storeViewId = message.body()
      val query = "DELETE FROM store_view WHERE store_view_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(storeViewId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          message.reply("Store view deleted successfully")
        } else {
          message.reply("Failed to delete store view")
        }
      }
    }
  }

  /**
   * Get all scopes (website and store view) from the database
   */
  private fun getAllScopes() {
    val getAllScopesConsumer = eventBus.consumer<Unit>("process.scope.getAllScopes")
    getAllScopesConsumer.handler { message ->
      val query = "SELECT * FROM store_view INNER JOIN websites ON store_view.website_id = websites.website_id"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          val rows = res.result()
          if (rows.size() > 0) {
            json = json {
              obj(
                "scopes" to rows.map { row ->
                  obj(
                    makeFullScopeJsonFields(row)
                  )
                }
              )
            }
            message.reply(json)
          } else {
            message.reply(json { obj("scopes" to "{}") })
          }
        }
      }
    }
  }

  /**
   * Get a scope by its ID from the database
   */
  private fun getScopeById() {
    val getScopeByIdConsumer = eventBus.consumer<Int>("process.scope.getScopeById")
    getScopeByIdConsumer.handler { message ->
      val scopeId = message.body()
      val query = "SELECT * FROM store_view" +
        " INNER JOIN websites ON store_view.website_id = websites.website_id" +
        " WHERE store_view_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(scopeId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply("Failed to execute query: $res")
      }

      rowsFuture.onComplete { res ->
        if (res.succeeded()) {
          val rows = res.result()
          if (rows.size() > 0) {
            json = json {
              obj(
                makeFullScopeJsonFields(rows.first())
              )
            }
            message.reply(json)
          } else {
            message.reply(json { obj("scope" to "{}") })
          }
        }
      }
    }
  }

  /**
   * Make the JSON fields for the return of the website queries
   *
   * @param row The row from the database
   * @return A list with the JSON fields in the format of a Pair object
   */
  private fun makeWebsiteJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "website_id" to row.getInteger("website_id"),
      "website_name" to row.getString("website_name")
    )
  }

  /**
   * Make the JSON fields for the return of the store view queries
   *
   * @param row The row from the database
   * @return A list with the JSON fields in the format of a Pair object
   **/
  private fun makeStoreViewJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "store_view_id" to row.getInteger("store_view_id"),
      "website_id" to row.getInteger("website_id"),
      "store_view_name" to row.getString("store_view_name")
    )
  }

  /**
   * Make the JSON fields for the return of the full scope queries
   *
   * @param row The row from the database
   * @return A list with the JSON fields in the format of a Pair object
   **/
  private fun makeFullScopeJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "store_view_id" to row.getInteger("store_view_id"),
      "store_view_name" to row.getString("store_view_name"),
      "website_id" to row.getInteger("website_id"),
      "website_name" to row.getString("website_name")
    )
  }

  /**
   * Make the tuple for the website queries
   *
   * @param body The JSON object containing the request body
   * @param putRequest Whether it's a PUT request or not
   *
   * @return A Tuple with the query parameters
   **/
  private fun makeWebsiteTuple(body: JsonObject, putRequest: Boolean): Tuple {

    val websiteTuple: Tuple = if (putRequest) {
      Tuple.of(
        body.getString("website_name"),
        body.getInteger("website_id")
      )
    } else {
      Tuple.of(
        body.getString("website_name")
      )
    }

    return websiteTuple
  }

  /**
   * Make the tuple for the store view queries
   *
   * @param body The JSON object containing the request body
   * @param putRequest Whether it's a PUT request or not
   *
   * @return A Tuple with the query parameters
   **/
  private fun makeStoreViewTuple(body: JsonObject, putRequest: Boolean): Tuple {
    val storeViewTuple: Tuple = if (putRequest) {
      Tuple.of(
        body.getInteger("website_id"),
        body.getString("store_view_name"),
        body.getInteger("store_view_id")
      )
    } else {
      Tuple.of(
        body.getInteger("website_id"),
        body.getString("store_view_name"),
      )
    }

    return storeViewTuple
  }
}
