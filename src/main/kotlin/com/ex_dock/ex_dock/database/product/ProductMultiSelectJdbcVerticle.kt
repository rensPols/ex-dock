package com.ex_dock.ex_dock.database.product;

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class ProductMultiSelectJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllMultiSelectAttributesBool()
    getMultiSelectAttributesBoolByKey()
    createMultiSelectAttributesBool()
    updateMultiSelectAttributesBool()
    deleteMultiSelectAttributesBool()

    getAllMultiSelectAttributesFloat()
    getMultiSelectAttributesFloatByKey()
    createMultiSelectAttributesFloat()
    updateMultiSelectAttributesFloat()
    deleteMultiSelectAttributesFloat()

    getAllMultiSelectAttributesString()
    getMultiSelectAttributesStringByKey()
    createMultiSelectAttributesString()
    updateMultiSelectAttributesString()
    deleteMultiSelectAttributesString()

    getAllMultiSelectAttributesInt()
    getMultiSelectAttributesIntByKey()
    createMultiSelectAttributesInt()
    updateMultiSelectAttributesInt()
    deleteMultiSelectAttributesInt()

    getAllMultiSelectAttributesMoney()
    getMultiSelectAttributesMoneyByKey()
    createMultiSelectAttributesMoney()
    updateMultiSelectAttributesMoney()
    deleteMultiSelectAttributesMoney()

    getALlMultiSelectAttributesInfo()
    getMultiSelectAttributesInfoByKey()
  }

  private fun getAllMultiSelectAttributesBool() {
    val getAllMultiSelectAttributesBoolConsumer = eventBus.consumer<String>("process.multiSelect.getAllMultiSelectAttributesBool")
    getAllMultiSelectAttributesBoolConsumer.handler { message ->
      val query = "SELECT * FROM multi_select_attributes_bool"
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
            obj (
              "multiSelectBool" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesBoolJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website bool found")
        }
      }
    }
  }

  private fun getMultiSelectAttributesBoolByKey() {
    val getMultiSelectAttributesBoolByKeyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getMultiSelectAttributesBoolByKey")
    getMultiSelectAttributesBoolByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM multi_select_attributes_bool WHERE attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))
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
              makeMultiSelectAttributesBoolJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No website bool found")
        }
      }
    }
  }

  private fun createMultiSelectAttributesBool() {
    val createMultiSelectAttributesBoolConsumer = eventBus.consumer<JsonObject>("process.multiSelect.createMultiSelectAttributesBool")
    createMultiSelectAttributesBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO multi_select_attributes_bool (attribute_key, option, value) VALUES (?, ?, ?::bit(1))"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesBoolTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute bool created successfully")
      }
    }
  }

  private fun updateMultiSelectAttributesBool() {
    val updateMultiSelectAttributesBoolConsumer = eventBus.consumer<JsonObject>("process.multiSelect.updateMultiSelectAttributesBool")
    updateMultiSelectAttributesBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE multi_select_attributes_bool SET option =?, value =?::bit(1) WHERE attribute_key =? "
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesBoolTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute bool updated successfully")
      }
    }
  }

  private fun deleteMultiSelectAttributesBool() {
    val deleteMultiSelectAttributesBoolConsumer = eventBus.consumer<JsonObject>("process.multiSelect.deleteMultiSelectAttributesBool")
    deleteMultiSelectAttributesBoolConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM multi_select_attributes_bool WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute bool deleted successfully")
      }
    }
  }

  private fun getAllMultiSelectAttributesFloat() {
    val allMultiSelectAttributesFloatConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getAllMultiSelectAttributesFloat")
    allMultiSelectAttributesFloatConsumer.handler { message ->
      val query = "SELECT * FROM multi_select_attributes_float"
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
            obj (
              "multiSelectFloat" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesFloatJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website float found")
        }
      }
    }
  }

  private fun getMultiSelectAttributesFloatByKey() {
    val getMultiSelectAttributesFloatByKeyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getMultiSelectAttributesFloatByKey")
    getMultiSelectAttributesFloatByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM multi_select_attributes_float WHERE attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))
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
              makeMultiSelectAttributesFloatJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No website float found")
        }
      }
    }
  }

  private fun createMultiSelectAttributesFloat() {
    val createMultiSelectAttributesFloatConsumer = eventBus.consumer<JsonObject>("process.multiSelect.createMultiSelectAttributesFloat")
    createMultiSelectAttributesFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO multi_select_attributes_float (attribute_key, option, value) VALUES (?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesFloatTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute float created successfully")
      }
    }
  }

  private fun updateMultiSelectAttributesFloat() {
    val updateMultiSelectAttributesFloatConsumer = eventBus.consumer<JsonObject>("process.multiSelect.updateMultiSelectAttributesFloat")
    updateMultiSelectAttributesFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE multi_select_attributes_float SET option =?, value =? WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesFloatTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute float updated successfully")
      }
    }
  }

  private fun deleteMultiSelectAttributesFloat() {
    val deleteMultiSelectAttributesFloatConsumer = eventBus.consumer<JsonObject>("process.multiSelect.deleteMultiSelectAttributesFloat")
    deleteMultiSelectAttributesFloatConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM multi_select_attributes_float WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute float deleted successfully")
      }
    }
  }

  private fun getAllMultiSelectAttributesString() {
    val allMultiSelectAttributesStringConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getAllMultiSelectAttributesString")
    allMultiSelectAttributesStringConsumer.handler { message ->
      val query = "SELECT * FROM multi_select_attributes_string"
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
              "multiSelectString" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesStringJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website string found")
        }
      }
    }
  }

  private fun getMultiSelectAttributesStringByKey() {
    val getMultiSelectAttributesStringByKeyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getMultiSelectAttributesStringByKey")
    getMultiSelectAttributesStringByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM multi_select_attributes_string WHERE attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))
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
              makeMultiSelectAttributesStringJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No website string found")
        }
      }
    }
  }

  private fun createMultiSelectAttributesString() {
    val createMultiSelectAttributesStringConsumer = eventBus.consumer<JsonObject>("process.multiSelect.createMultiSelectAttributesString")
    createMultiSelectAttributesStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO multi_select_attributes_string (attribute_key, option, value) VALUES (?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesStringTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute string created successfully")
      }
    }
  }

  private fun updateMultiSelectAttributesString() {
    val updateMultiSelectAttributesStringConsumer = eventBus.consumer<JsonObject>("process.multiSelect.updateMultiSelectAttributesString")
    updateMultiSelectAttributesStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE multi_select_attributes_string SET option =?, value =? WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesStringTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute string updated successfully")
      }
    }
  }

  private fun deleteMultiSelectAttributesString() {
    val deleteMultiSelectAttributesStringConsumer = eventBus.consumer<JsonObject>("process.multiSelect.deleteMultiSelectAttributesString")
    deleteMultiSelectAttributesStringConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM multi_select_attributes_string WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute string deleted successfully")
      }
    }
  }

  private fun getAllMultiSelectAttributesInt() {
    val allMultiSelectAttributesIntConsumer = eventBus.consumer<String>("process.multiSelect.getAllMultiSelectAttributesInt")
    allMultiSelectAttributesIntConsumer.handler { message ->
      val query = "SELECT * FROM multi_select_attributes_int"
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
              "multiSelectInt" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesIntJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website int found")
        }
      }
    }
  }

  private fun getMultiSelectAttributesIntByKey() {
    val getMultiSelectAttributesIntByKeyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getMultiSelectAttributesIntByKey")
    getMultiSelectAttributesIntByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM multi_select_attributes_int WHERE attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))
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
              makeMultiSelectAttributesIntJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No rows returned")
        }
      }
    }
  }

  private fun createMultiSelectAttributesInt() {
    val createMultiSelectAttributesIntConsumer = eventBus.consumer<JsonObject>("process.multiSelect.createMultiSelectAttributesInt")
    createMultiSelectAttributesIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO multi_select_attributes_int (attribute_key, option, value) VALUES (?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesIntTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute int created successfully")
      }
    }
  }

  private fun updateMultiSelectAttributesInt() {
    val updateMultiSelectAttributesIntConsumer = eventBus.consumer<JsonObject>("process.multiSelect.updateMultiSelectAttributesInt")
    updateMultiSelectAttributesIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE multi_select_attributes_int SET option =?, value =? WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesIntTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute int updated successfully")
      }
    }
  }

  private fun deleteMultiSelectAttributesInt() {
    val deleteMultiSelectAttributesIntConsumer = eventBus.consumer<JsonObject>("process.multiSelect.deleteMultiSelectAttributesInt")
    deleteMultiSelectAttributesIntConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM multi_select_attributes_int WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute int deleted successfully")
      }
    }
  }

  private fun getAllMultiSelectAttributesMoney() {
    val allMultiSelectAttributesMoneyConsumer = eventBus.consumer<String>("process.multiSelect.getAllMultiSelectAttributesMoney")
    allMultiSelectAttributesMoneyConsumer.handler { message ->
      val query = "SELECT * FROM multi_select_attributes_money"
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
              "multiSelectMoney" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesMoneyJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website money found")
        }
      }
    }
  }

  private fun getMultiSelectAttributesMoneyByKey() {
    val getMultiSelectAttributesMoneyByKeyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.getMultiSelectAttributesMoneyByKey")
    getMultiSelectAttributesMoneyByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM multi_select_attributes_money WHERE attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))
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
              makeMultiSelectAttributesMoneyJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No Multi-select attribute Money found")
        }
      }
    }
  }

  private fun createMultiSelectAttributesMoney() {
    val createMultiSelectAttributesMoneyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.createMultiSelectAttributesMoney")
    createMultiSelectAttributesMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO multi_select_attributes_money (attribute_key, option, value) VALUES (?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesMoneyTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute money created successfully")
      }
    }
  }

  private fun updateMultiSelectAttributesMoney() {
    val updateMultiSelectAttributesMoneyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.updateMultiSelectAttributesMoney")
    updateMultiSelectAttributesMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE multi_select_attributes_money SET option =?, value =? WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeMultiSelectAttributesMoneyTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute money updated successfully")
      }
    }
  }

  private fun deleteMultiSelectAttributesMoney() {
    val deleteMultiSelectAttributesMoneyConsumer = eventBus.consumer<JsonObject>("process.multiSelect.deleteMultiSelectAttributesMoney")
    deleteMultiSelectAttributesMoneyConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM multi_select_attributes_money WHERE attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(body.getString("attribute_key")))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("Multi-select attribute money deleted successfully")
      }
    }
  }

  private fun getALlMultiSelectAttributesInfo() {
    val allMultiSelectAttributesInfoConsumer = eventBus.consumer<String>("process.multiSelect.getAllMultiSelectAttributesInfo")
    allMultiSelectAttributesInfoConsumer.handler { message ->
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, msab.value AS bool_value, " +
        "msaf.value AS float_value, msas.value AS string_value, " +
        "msai.value AS int_value, msam.value AS money_value, " +
        "cpa.attribute_key FROM products " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_bool msab on msab.attribute_key = cpa.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_float msaf on cpa.attribute_key = msaf.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_int msai on cpa.attribute_key = msai.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_string msas on cpa.attribute_key = msas.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_money msam on cpa.attribute_key = msam.attribute_key "
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
              "multiSelectInfo" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesInfoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website info found")
        }
      }
    }
  }

  private fun getMultiSelectAttributesInfoByKey() {
    val getMultiSelectAttributesInfoByKeyConsumer = eventBus.consumer<Int>("process.multiSelect.getMultiSelectAttributesInfoByKey")
    getMultiSelectAttributesInfoByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, msab.value AS bool_value, " +
        "msaf.value AS float_value, msas.value AS string_value, " +
        "msai.value AS int_value, msam.value AS money_value, " +
        "cpa.attribute_key FROM products " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_bool msab on msab.attribute_key = cpa.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_float msaf on cpa.attribute_key = msaf.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_int msai on cpa.attribute_key = msai.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_string msas on cpa.attribute_key = msas.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_money msam on cpa.attribute_key = msam.attribute_key " +
        "WHERE products.product_id =?"

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
              "multiSelectInfo" to rows.map { row ->
                obj(
                  makeMultiSelectAttributesInfoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website info found for product ID: $body")
        }
      }
    }
  }

  private fun makeMultiSelectAttributesBoolJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "attribute_key" to row.getString("attribute_key"),
      "option" to row.getInteger("option"),
      "value" to row.getBoolean("value")
    )
  }

  private fun makeMultiSelectAttributesFloatJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "attribute_key" to row.getString("attribute_key"),
      "option" to row.getInteger("option"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeMultiSelectAttributesStringJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "attribute_key" to row.getString("attribute_key"),
      "option" to row.getInteger("option"),
      "value" to row.getString("value")
    )
  }

  private fun makeMultiSelectAttributesIntJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "attribute_key" to row.getString("attribute_key"),
      "option" to row.getInteger("option"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeMultiSelectAttributesMoneyJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "attribute_key" to row.getString("attribute_key"),
      "option" to row.getInteger("option"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeMultiSelectAttributesInfoJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "multiSelectBool" to row.getBoolean("bool_value"),
      "multiSelectFloat" to row.getDouble("float_value"),
      "multiSelectString" to row.getString("string_value"),
      "multiSelectInt" to row.getInteger("int_value"),
      "multiSelectMoney" to row.getDouble("money_value"),
    )
  }

  private fun makeMultiSelectAttributesBoolTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val multiSelectBoolTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("option"),
        body.getString("value"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getString("attribute_key"),
        body.getInteger("option"),
        body.getString("value"),
      )
    }

    return multiSelectBoolTuple
  }

  private fun makeMultiSelectAttributesFloatTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val multiSelectFloatTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("option"),
        body.getDouble("value"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getString("attribute_key"),
        body.getInteger("option"),
        body.getDouble("value"),
      )
    }

    return multiSelectFloatTuple
  }

  private fun makeMultiSelectAttributesStringTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeMultiSelectAttributesBoolTuple(body, isPutRequest)
  }

  private fun makeMultiSelectAttributesIntTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val multiSelectIntTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("option"),
        body.getInteger("value"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getString("attribute_key"),
        body.getInteger("option"),
        body.getInteger("value"),
      )
    }

    return multiSelectIntTuple
  }

  private fun makeMultiSelectAttributesMoneyTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeMultiSelectAttributesFloatTuple(body, isPutRequest)
  }
}
