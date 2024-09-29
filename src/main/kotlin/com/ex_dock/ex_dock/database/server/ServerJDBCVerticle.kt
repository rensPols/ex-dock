package com.ex_dock.ex_dock.database.server

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple

class ServerJDBCVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize all eventbus connections with the Server Data table
    getAllServerData()
    getServerDataByKey()
    createServerData()
    updateServerData()
    deleteServerData()

    // Initialize all eventbus connections with the Server Version table
    getAllServerVersions()
    getServerVersionByKey()
    createServerVersion()
    updateServerVersion()
    deleteServerVersion()
  }

  /**
   * Get all server data from the database
   */
  private fun getAllServerData() {
    val getAllServerDataConsumer = eventBus.consumer<String>("process.server.getAllServerData")
    getAllServerDataConsumer.handler { message ->
      val query = "SELECT * FROM server_data"
      val rowsFuture = client.preparedQuery(query).execute()

      answerServerDataMessage(rowsFuture, message)
    }
  }

  /**
   * Get all server data from the database by key
   */
  private fun getServerDataByKey() {
    val getServerDataByKeyConsumer = eventBus.localConsumer<String>("process.server.getServerByKey")
    getServerDataByKeyConsumer.handler { message ->
      val key = message.body().toString()
      val query = "SELECT * FROM server_data WHERE key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(key))

      answerServerDataMessage(rowsFuture, message)
    }
  }

  /**
   * Create a new server data entry in the database
   */
  private fun createServerData() {
    val createServerDataConsumer = eventBus.localConsumer<JsonObject>("process.server.createServerData")
    createServerDataConsumer.handler { message ->
      val serverData = message.body()
      val query = "INSERT INTO server_data (key, value) VALUES (?,?)"
      val rowsFuture = client.preparedQuery(query).execute(makeServerDataTuple(serverData, false))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { _ ->
        message.reply("Server data created successfully!")
      }
    }
  }

  /**
   * Update an existing server data entry in the database
   */
  private fun updateServerData() {
    val updateServerDataConsumer = eventBus.localConsumer<JsonObject>("process.server.updateServerData")
    updateServerDataConsumer.handler { message ->
      val serverData = message.body()
      val query = "UPDATE server_data SET value =? WHERE key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeServerDataTuple(serverData, true))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { _ ->
        message.reply("Server data updated successfully!")
      }
    }
  }

  /**
   * Delete a server data entry from the database
   */
  private fun deleteServerData() {
    val deleteServerDataConsumer = eventBus.localConsumer<String>("process.server.deleteServerData")
    deleteServerDataConsumer.handler { message ->
      val key = message.body().toString()
      val query = "DELETE FROM server_data WHERE key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(key))

      rowsFuture.onFailure{ res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { _ ->
        message.reply("Server data deleted successfully!")
      }
    }
  }

  /**
   * Get all server versions from the database
   */
  private fun getAllServerVersions() {
    val getAllServerVersionsConsumer = eventBus.localConsumer<String>("process.server.getAllServerVersions")
    getAllServerVersionsConsumer.handler { message ->
      val query = "SELECT * FROM server_version"
      val rowsFuture = client.preparedQuery(query).execute()
      var allServerJson: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          allServerJson = json {
            obj (
              "serverVersions" to rows.map { row ->
                obj(
                  makeServerVersionJsonFields(row)
                )
              }
            )
          }
          message.reply(allServerJson)
        } else {
          message.reply("No server versions found!")
        }
      }
    }
  }

  /**
   * Get a server version from the database by major, minor, and patch
   */
  private fun getServerVersionByKey() {
    val getServerVersionByKeyConsumer = eventBus.localConsumer<JsonObject>("process.server.getServerVersionByKey")
    getServerVersionByKeyConsumer.handler { message ->
      val key = message.body()
      val query = "SELECT * FROM server_version WHERE major = ? AND minor = ? AND patch = ?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(
        key.getInteger("major"), key.getInteger("minor"), key.getInteger("patch")
      ))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = json {
            obj (
              "server_version" to rows.map { row ->
                obj(
                  makeServerVersionJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No server version found!")
        }
      }
    }
  }

  /**
   * Create a new server version entry in the database
   */
  private fun createServerVersion() {
    val createServerVersionConsumer = eventBus.localConsumer<JsonObject>("process.server.createServerVersion")
    createServerVersionConsumer.handler { message ->
      val serverVersion = message.body()
      val query = "INSERT INTO server_version (major, minor, patch, version_name, version_description) VALUES (?,?,?,?,?)"
      val rowsFuture = client.preparedQuery(query).execute(makeServerVersionTuple(serverVersion, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { _ ->
        message.reply("Server version created successfully!")
      }
    }
  }

  /**
   * Update an existing server version entry in the database
   */
  private fun updateServerVersion() {
    val updateServerVersionConsumer = eventBus.localConsumer<JsonObject>("process.server.updateServerVersion")
    updateServerVersionConsumer.handler { message ->
      val serverVersion = message.body()
      val query = "UPDATE server_version SET major =?, minor =?, patch =?, version_name =?, version_description =? WHERE major = ? AND minor = ? AND patch = ?"
      val rowsFuture = client.preparedQuery(query).execute(makeServerVersionTuple(serverVersion, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { _ ->
        message.reply("Server version updated successfully!")
      }
    }
  }

  /**
   * Delete a server version entry from the database
   */
  private fun deleteServerVersion() {
    val deleteServerVersionConsumer = eventBus.localConsumer<JsonObject>("process.server.deleteServerVersion")
    deleteServerVersionConsumer.handler { message ->
      val serverVersion = message.body()
      val query = "DELETE FROM server_version WHERE major = ? AND minor = ? AND patch = ?"
      val rowsFuture = client.preparedQuery(query).execute(
        Tuple.of(serverVersion.getInteger("major"),
          serverVersion.getInteger("minor"),
          serverVersion.getInteger("patch")))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { _ ->
        message.reply("Server version deleted successfully!")
      }
    }
  }

  /**
   * Make JSON fields from a row out of the database for the server data
   */
  private fun makeServerDataJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "key" to row.getString("key"),
      "value" to row.getString("value")
    )
  }

  /**
   * Make JSON fields from a row out of the database for the server version
   */
  private fun makeServerVersionJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "major" to row.getInteger("major"),
      "minor" to row.getInteger("minor"),
      "patch" to row.getInteger("patch"),
      "version_name" to row.getString("version_name"),
      "version_description" to row.getString("version_description")
    )
  }

  /**
   * Make a tuple for the server data for database insertion or update
   */
  private fun makeServerDataTuple(body: JsonObject, putRequest: Boolean): Tuple {
    val serverDataTuple: Tuple = if (putRequest) {
      Tuple.of(
        body.getString("value"),
        body.getString("key")
      )
    } else {
      Tuple.of(
        body.getString("key"),
        body.getString("value")
      )
    }

    return serverDataTuple
  }

  /**
   * Make a tuple for the server version for database insertion or update
   */
  private fun makeServerVersionTuple(body: JsonObject, putRequest: Boolean): Tuple {
    val versionName = try {
        body.getString("version_name")
    } catch (e: NullPointerException) { null }
    val versionDescription = try {
        body.getString("version_description")
    } catch (e: NullPointerException) { null }

    val serverVersionTuple: Tuple
    if (putRequest) {
      serverVersionTuple = Tuple.of(
        body.getInteger("major"),
        body.getInteger("minor"),
        body.getInteger("patch"),
        versionName,
        versionDescription,
        body.getInteger("major"),
        body.getInteger("minor"),
        body.getInteger("patch")
      )
    } else {
      serverVersionTuple = Tuple.of(
        body.getInteger("major"),
        body.getInteger("minor"),
        body.getInteger("patch"),
        versionName,
        versionDescription
      )
    }

    return serverVersionTuple
  }

  /**
   * Answer the server data message with the retrieved data
   */
  private fun answerServerDataMessage(rowsFuture: Future<RowSet<Row>>, message: Message<String>) {
    var json: JsonObject

    rowsFuture.onFailure { res ->
      println("Failed to execute query: $res")
      message.reply(failedMessage)
    }
    rowsFuture.onSuccess { res ->
      if (res.size() > 0) {
        json = json {
          obj(
            "serverData" to res.map { row ->
              obj(
                makeServerDataJsonFields(row)
              )
            }
          )
        }
        message.reply(json)
      } else {
        message.reply(json { obj("serverData" to "{}") })
      }
    }
  }
}
