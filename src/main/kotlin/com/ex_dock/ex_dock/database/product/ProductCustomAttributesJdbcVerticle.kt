package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class ProductCustomAttributesJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllCustomAttributes()
    getCustomAttributeByKey()
    createCustomAttribute()
    updateCustomAttribute()
    deleteCustomAttribute()
  }

  private fun getAllCustomAttributes() {
    val getAllCustomAttributesConsumer = eventBus.consumer<String>("process.attributes.getAllCustomAttributes")
    getAllCustomAttributesConsumer.handler { message ->
      val query = "SELECT * FROM custom_product_attributes"
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
            obj("customAttributes" to rows.map { row ->
              obj(
                makeCustomAttributesJsonFields(row)
              )
            })
          }
          message.reply(json)
        } else {
          message.reply(JsonObject().put("message", "No custom attributes found"))
        }
      }
    }
  }

  private fun getCustomAttributeByKey() {
    val getCustomAttributeByKeyConsumer = eventBus.consumer<String>("process.attributes.getCustomAttributeByKey")
    getCustomAttributeByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM custom_product_attributes WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body))
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
              makeCustomAttributesJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No custom attributes found")
        }
      }
    }
  }

  private fun createCustomAttribute() {
    val createCustomAttributeConsumer = eventBus.consumer<JsonObject>("process.attributes.createCustomAttribute")
    createCustomAttributeConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO custom_product_attributes (attribute_key, scope, name, type, multiselect, required) " +
          "VALUES (?,?,?,?::cpa_type,?::bit(1),?::bit(1))"

      val ctaTuple = makeCustomAttributeTuple(body, false)
      val rowsFuture = client.preparedQuery(query).execute(ctaTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.result().rowCount() > 0) {
          message.reply("Custom attribute created successfully")
        } else {
          message.reply("Failed to create custom attribute")
        }
      }
    }
  }

  private fun updateCustomAttribute() {
    val updateCustomAttributeConsumer = eventBus.consumer<JsonObject>("process.attributes.updateCustomAttribute")
    updateCustomAttributeConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE custom_product_attributes SET scope=?, name=?, type=?::cpa_type, " +
          "multiselect=?::bit(1), required=?::bit(1) WHERE attribute_key=?"

      val ctaTuple = makeCustomAttributeTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(ctaTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.result().rowCount() > 0) {
          message.reply("Custom attribute updated successfully")
        } else {
          message.reply("No custom attribute found to update")
        }
      }
    }
  }

  private fun deleteCustomAttribute() {
    val deleteCustomAttributeConsumer = eventBus.consumer<String>("process.attributes.deleteCustomAttribute")
    deleteCustomAttributeConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM custom_product_attributes WHERE attribute_key=?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        if (res.result().rowCount() > 0) {
          message.reply("Custom attribute deleted successfully")
        } else {
          message.reply("No custom attribute found to delete")
        }
      }
    }
  }

  private fun makeCustomAttributesJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "attribute_key" to row.getString("attribute_key"),
      "scope" to row.getInteger("scope"),
      "name" to row.getString("name"),
      "type" to row.getString("type"),
      "multiselect" to row.getBoolean("multiselect"),
      "required" to row.getBoolean("required")
    )
  }

  private fun makeCustomAttributeTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val ctaTuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("scope"),
        body.getString("name"),
        body.getString("type"),
        body.getString("multiselect"),
        body.getString("required"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getString("attribute_key"),
        body.getInteger("scope"),
        body.getString("name"),
        body.getString("type"),
        body.getString("multiselect"),
        body.getString("required")
      )
    }

    return ctaTuple
  }
}
