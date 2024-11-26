package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.connection.getConnection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class ProductCustomAttributesJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"
  private val customProductAttributesDataDeliveryOptions = DeliveryOptions().setCodecName("CustomProductAttributesCodec")
  private val listDeliveryOptions = DeliveryOptions().setCodecName("ListCodec")

  override fun start() {
    client = getConnection(vertx)
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
      val customProductAttributesList: MutableList<CustomProductAttributes> = emptyList<CustomProductAttributes>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          rows.forEach { row ->
            customProductAttributesList.add(makeCustomAttribute(row))
          }
        }

        message.reply(customProductAttributesList, listDeliveryOptions)
      }
    }
  }

  private fun getCustomAttributeByKey() {
    val getCustomAttributeByKeyConsumer = eventBus.consumer<String>("process.attributes.getCustomAttributeByKey")
    getCustomAttributeByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM custom_product_attributes WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          message.reply(makeCustomAttribute(rows.first()), customProductAttributesDataDeliveryOptions)
        } else {
          message.reply("No custom attributes found")
        }
      }
    }
  }

  private fun createCustomAttribute() {
    val createCustomAttributeConsumer = eventBus.consumer<CustomProductAttributes>("process.attributes.createCustomAttribute")
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
          message.reply(body, customProductAttributesDataDeliveryOptions)
        } else {
          message.reply("Failed to create custom attribute")
        }
      }
    }
  }

  private fun updateCustomAttribute() {
    val updateCustomAttributeConsumer = eventBus.consumer<CustomProductAttributes>("process.attributes.updateCustomAttribute")
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
          message.reply(body, customProductAttributesDataDeliveryOptions)
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

  private fun makeCustomAttribute(row: Row): CustomProductAttributes {
    return CustomProductAttributes(
      attributeKey = row.getString("attribute_key"),
      scope = row.getInteger("scope"),
      name = row.getString("name"),
      type = convertStringToType(row.getString("type")),
      multiselect = row.getBoolean("multiselect"),
      required = row.getBoolean("required")
    )
  }

  private fun makeCustomAttributeTuple(body: CustomProductAttributes, isPutRequest: Boolean): Tuple {
    val ctaTuple = if (isPutRequest) {
      Tuple.of(
        body.scope,
        body.name,
        convertTypeToString(body.type),
        body.multiselect.toInt(),
        body.required.toInt(),
        body.attributeKey,
      )
    } else {
      Tuple.of(
        body.attributeKey,
        body.scope,
        body.name,
        convertTypeToString(body.type),
        body.multiselect.toInt(),
        body.required.toInt(),
      )
    }

    return ctaTuple
  }

  private fun convertTypeToString(type: Type): String {
    return when (type) {
      Type.STRING -> "string"
      Type.BOOL -> "bool"
      Type.FLOAT -> "float"
      Type.INT -> "int"
      Type.MONEY -> "money"
    }
  }

  private fun convertStringToType(name: String): Type {
    return when (name) {
      "string" -> Type.STRING
      "bool" -> Type.BOOL
      "float" -> Type.FLOAT
      "int" -> Type.INT
      "money" -> Type.MONEY
      else -> throw IllegalArgumentException("Invalid type: $name")
    }
  }

  private fun Boolean.toInt() = if (this) 1 else 0
}
