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

class ProductStoreViewEavJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllEavStoreViewBool()
    getEavStoreViewBoolByKey()
    createEavStoreViewBool()
    updateEavStoreViewBool()
    deleteEavStoreViewBool()

    getAllEavStoreViewFloat()
    getEavStoreViewFloatByKey()
    createEavStoreViewFloat()
    updateEavStoreViewFloat()
    deleteEavStoreViewFloat()

    getAllEavStoreViewString()
    getEavStoreViewStringByKey()
    createEavStoreViewString()
    updateEavStoreViewString()
    deleteEavStoreViewString()

    getAllEavStoreViewInt()
    getEavStoreViewIntByKey()
    createEavStoreViewInt()
    updateEavStoreViewInt()
    deleteEavStoreViewInt()

    getAllEavStoreViewMoney()
    getEavStoreViewMoneyByKey()
    createEavStoreViewMoney()
    updateEavStoreViewMoney()
    deleteEavStoreViewMoney()

    getAllEavStoreViewMultiSelect()
    getEavStoreViewMultiSelectByKey()
    createEavStoreViewMultiSelect()
    updateEavStoreViewMultiSelect()
    deleteEavStoreViewMultiSelect()

    getAllEavStoreView()
    getEavStoreViewByKey()
    createEavStoreView()
    updateEavStoreView()
    deleteEavStoreView()

    getALlEavStoreViewInfo()
    getEavStoreViewInfoByKey()
  }

  private fun getAllEavStoreViewBool() {
    val getAllEavStoreViewBoolConsumer = eventBus.consumer<String>("process.eavStoreView.getAllEavStoreViewBool")
    getAllEavStoreViewBoolConsumer.handler { message ->
      val query = "SELECT * FROM eav_store_view_bool"
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
              "eavStoreViewBool" to rows.map { row ->
                obj(
                  makeEavStoreViewBoolJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView bool found")
        }
      }
    }
  }

  private fun getEavStoreViewBoolByKey() {
    val getEavStoreViewBoolByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewBoolByKey")
    getEavStoreViewBoolByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_store_view_bool WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))
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
              makeEavStoreViewBoolJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView bool found")
        }
      }
    }
  }

  private fun createEavStoreViewBool() {
    val createEavStoreViewBoolConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreViewBool")
    createEavStoreViewBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_store_view_bool (product_id, store_view_id, attribute_key, value) VALUES (?, ?, ?, ?::bit(1))"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewBoolTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView bool created successfully")
      }
    }
  }

  private fun updateEavStoreViewBool() {
    val updateEavStoreViewBoolConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreViewBool")
    updateEavStoreViewBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_store_view_bool SET value =?::bit(1) WHERE product_id =? AND store_view_id =? AND attribute_key =? "
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewBoolTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView bool updated successfully")
      }
    }
  }

  private fun deleteEavStoreViewBool() {
    val deleteEavStoreViewBoolConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreViewBool")
    deleteEavStoreViewBoolConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_store_view_bool WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView bool deleted successfully")
      }
    }
  }

  private fun getAllEavStoreViewFloat() {
    val allEavStoreViewFloatConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getAllEavStoreViewFloat")
    allEavStoreViewFloatConsumer.handler { message ->
      val query = "SELECT * FROM eav_store_view_float"
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
              "eavStoreViewFloat" to rows.map { row ->
                obj(
                  makeEavStoreViewFloatJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView float found")
        }
      }
    }
  }

  private fun getEavStoreViewFloatByKey() {
    val getEavStoreViewFloatByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewFloatByKey")
    getEavStoreViewFloatByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_store_view_float WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))
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
              makeEavStoreViewFloatJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView float found")
        }
      }
    }
  }

  private fun createEavStoreViewFloat() {
    val createEavStoreViewFloatConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreViewFloat")
    createEavStoreViewFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_store_view_float (product_id, store_view_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewFloatTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView float created successfully")
      }
    }
  }

  private fun updateEavStoreViewFloat() {
    val updateEavStoreViewFloatConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreViewFloat")
    updateEavStoreViewFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_store_view_float SET value =? WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewFloatTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView float updated successfully")
      }
    }
  }

  private fun deleteEavStoreViewFloat() {
    val deleteEavStoreViewFloatConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreViewFloat")
    deleteEavStoreViewFloatConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_store_view_float WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView float deleted successfully")
      }
    }
  }

  private fun getAllEavStoreViewString() {
    val allEavStoreViewStringConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getAllEavStoreViewString")
    allEavStoreViewStringConsumer.handler { message ->
      val query = "SELECT * FROM eav_store_view_string"
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
              "eavStoreViewString" to rows.map { row ->
                obj(
                  makeEavStoreViewStringJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView string found")
        }
      }
    }
  }

  private fun getEavStoreViewStringByKey() {
    val getEavStoreViewStringByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewStringByKey")
    getEavStoreViewStringByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_store_view_string WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))
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
              makeEavStoreViewStringJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView string found")
        }
      }
    }
  }

  private fun createEavStoreViewString() {
    val createEavStoreViewStringConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreViewString")
    createEavStoreViewStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_store_view_string (product_id, store_view_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewStringTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView string created successfully")
      }
    }
  }

  private fun updateEavStoreViewString() {
    val updateEavStoreViewStringConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreViewString")
    updateEavStoreViewStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_store_view_string SET value =? WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewStringTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView string updated successfully")
      }
    }
  }

  private fun deleteEavStoreViewString() {
    val deleteEavStoreViewStringConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreViewString")
    deleteEavStoreViewStringConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_store_view_string WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView string deleted successfully")
      }
    }
  }

  private fun getAllEavStoreViewInt() {
    val allEavStoreViewIntConsumer = eventBus.consumer<String>("process.eavStoreView.getAllEavStoreViewInt")
    allEavStoreViewIntConsumer.handler { message ->
      val query = "SELECT * FROM eav_store_view_int"
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
              "eavStoreViewInt" to rows.map { row ->
                obj(
                  makeEavStoreViewIntJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView int found")
        }
      }
    }
  }

  private fun getEavStoreViewIntByKey() {
    val getEavStoreViewIntByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewIntByKey")
    getEavStoreViewIntByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_store_view_int WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))
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
              makeEavStoreViewIntJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No rows returned")
        }
      }
    }
  }

  private fun createEavStoreViewInt() {
    val createEavStoreViewIntConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreViewInt")
    createEavStoreViewIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_store_view_int (product_id, store_view_id ,attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewIntTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView int created successfully")
      }
    }
  }

  private fun updateEavStoreViewInt() {
    val updateEavStoreViewIntConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreViewInt")
    updateEavStoreViewIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_store_view_int SET value =? WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewIntTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView int updated successfully")
      }
    }
  }

  private fun deleteEavStoreViewInt() {
    val deleteEavStoreViewIntConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreViewInt")
    deleteEavStoreViewIntConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_store_view_int WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView int deleted successfully")
      }
    }
  }

  private fun getAllEavStoreViewMoney() {
    val allEavStoreViewMoneyConsumer = eventBus.consumer<String>("process.eavStoreView.getAllEavStoreViewMoney")
    allEavStoreViewMoneyConsumer.handler { message ->
      val query = "SELECT * FROM eav_store_view_money"
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
              "eavStoreViewMoney" to rows.map { row ->
                obj(
                  makeEavStoreViewMoneyJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView money found")
        }
      }
    }
  }

  private fun getEavStoreViewMoneyByKey() {
    val getEavStoreViewMoneyByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewMoneyByKey")
    getEavStoreViewMoneyByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_store_view_money WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))
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
              makeEavStoreViewMoneyJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No Eav Global Money found")
        }
      }
    }
  }

  private fun createEavStoreViewMoney() {
    val createEavStoreViewMoneyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreViewMoney")
    createEavStoreViewMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_store_view_money (product_id, store_view_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewMoneyTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView money created successfully")
      }
    }
  }

  private fun updateEavStoreViewMoney() {
    val updateEavStoreViewMoneyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreViewMoney")
    updateEavStoreViewMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_store_view_money SET value =? WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewMoneyTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView money updated successfully")
      }
    }
  }

  private fun deleteEavStoreViewMoney() {
    val deleteEavStoreViewMoneyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreViewMoney")
    deleteEavStoreViewMoneyConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_store_view_money WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView money deleted successfully")
      }
    }
  }

  private fun getAllEavStoreViewMultiSelect() {
    val allEavStoreViewMultiSelectConsumer = eventBus.consumer<String>("process.eavStoreView.getAllEavStoreViewMultiSelect")
    allEavStoreViewMultiSelectConsumer.handler { message ->
      val query = "SELECT * FROM eav_store_view_multi_select"
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
              "eavStoreViewMultiSelect" to rows.map { row ->
                obj(
                  makeEavStoreViewMultiSelectJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView multi-select found")
        }
      }
    }
  }

  private fun getEavStoreViewMultiSelectByKey() {
    val getEavStoreViewMultiSelectByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewMultiSelectByKey")
    getEavStoreViewMultiSelectByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_store_view_multi_select WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))
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
              makeEavStoreViewMultiSelectJsonFields(rows.first())
            )
          }
          message.reply(json)
        }
      }
    }
  }

  private fun createEavStoreViewMultiSelect() {
    val createEavStoreViewMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreViewMultiSelect")
    createEavStoreViewMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_store_view_multi_select (product_id, store_view_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewMultiSelectTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView multi-select created successfully")
      }
    }
  }

  private fun updateEavStoreViewMultiSelect() {
    val updateEavStoreViewMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreViewMultiSelect")
    updateEavStoreViewMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_store_view_multi_select SET value =? WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewMultiSelectTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView multi-select updated successfully")
      }
    }
  }

  private fun deleteEavStoreViewMultiSelect() {
    val deleteEavStoreViewMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreViewMultiSelect")
    deleteEavStoreViewMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_store_view_multi_select WHERE product_id =? AND store_view_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView multi-select deleted successfully")
      }
    }
  }

  private fun getAllEavStoreView() {
    val allEavStoreViewConsumer = eventBus.consumer<String>("process.eavStoreView.getAllEavStoreView")
    allEavStoreViewConsumer.handler { message ->
      val query = "SELECT * FROM eav"
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
              "eavStoreView" to rows.map { row ->
                obj(
                  makeEavStoreViewJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView found")
        }
      }
    }
  }

  private fun getEavStoreViewByKey() {
    val getEavStoreViewByKeyConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.getEavStoreViewByKey")
    getEavStoreViewByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav WHERE product_id =? AND attribute_key =?"
      val rowsFuture =
        client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))
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
              makeEavStoreViewJsonFields(rows.first())
            )
          }
          message.reply(json)
        }
      }
    }
  }

  private fun createEavStoreView() {
    val createEavStoreViewConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.createEavStoreView")
    createEavStoreViewConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav (product_id, attribute_key) VALUES (?,?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView created successfully")
      }
    }
  }

  private fun updateEavStoreView() {
    val updateEavStoreViewConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.updateEavStoreView")
    updateEavStoreViewConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav SET product_id =?, attribute_key=? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavStoreViewTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV storeView updated successfully")
      }
    }
  }

  private fun deleteEavStoreView() {
    val deleteEavStoreViewConsumer = eventBus.consumer<JsonObject>("process.eavStoreView.deleteEavStoreView")
    deleteEavStoreViewConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete {
        message.reply("EAV storeView deleted successfully")
      }
    }
  }

  private fun getALlEavStoreViewInfo() {
    val allEavStoreViewInfoConsumer = eventBus.consumer<String>("process.eavStoreView.getAllEavStoreViewInfo")
    allEavStoreViewInfoConsumer.handler { message ->
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, egb.value AS bool_value, " +
        "egf.value AS float_value, egs.value AS string_value, " +
        "egi.value AS int_value, egm.value AS money_value, " +
        "egms.value AS multi_select_value, cpa.attribute_key, sv.store_view_id AS store_view_id FROM products " +
        "LEFT JOIN public.eav_store_view_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_store_view_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_store_view_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_store_view_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_store_view_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_store_view_string egs on products.product_id = egs.product_id " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "LEFT JOIN public.store_view sv on egb.store_view_id = sv.store_view_id "
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
              "eavStoreViewInfo" to rows.map { row ->
                obj(
                  makeEavStoreViewInfoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView info found")
        }
      }
    }
  }

  private fun getEavStoreViewInfoByKey() {
    val getEavStoreViewInfoByKeyConsumer = eventBus.consumer<Int>("process.eavStoreView.getEavStoreViewInfoByKey")
    getEavStoreViewInfoByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, egb.value AS bool_value, " +
        "egf.value AS float_value, egs.value AS string_value, " +
        "egi.value AS int_value, egm.value AS money_value, " +
        "egms.value AS multi_select_value, cpa.attribute_key, sv.store_view_id AS store_view_id FROM products " +
        "LEFT JOIN public.eav_store_view_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_store_view_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_store_view_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_store_view_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_store_view_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_store_view_string egs on products.product_id = egs.product_id " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "LEFT JOIN public.store_view sv on egb.store_view_id = sv.store_view_id " +
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
              "eavStoreViewInfo" to rows.map { row ->
                obj(
                  makeEavStoreViewInfoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No storeView info found for product ID: $body")
        }
      }
    }
  }

  private fun makeEavStoreViewBoolJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getBoolean("value")
    )
  }

  private fun makeEavStoreViewFloatJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeEavStoreViewStringJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getString("value")
    )
  }

  private fun makeEavStoreViewIntJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeEavStoreViewMoneyJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeEavStoreViewMultiSelectJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeEavStoreViewJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
    )
  }

  private fun makeEavStoreViewInfoJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "store_view_id" to row.getInteger("store_view_id"),
      "attribute_key" to row.getString("attribute_key"),
      "storeViewBool" to row.getBoolean("bool_value"),
      "storeViewFloat" to row.getDouble("float_value"),
      "storeViewString" to row.getString("string_value"),
      "storeViewInt" to row.getInteger("int_value"),
      "storeViewMoney" to row.getDouble("money_value"),
      "storeViewMultiSelect" to row.getInteger("multi_select_value"),
    )
  }

  private fun makeEavStoreViewBoolTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavStoreViewBoolTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("value"),
        body.getInteger("product_id"),
        body.getInteger("store_view_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getInteger("store_view_id"),
        body.getString("attribute_key"),
        body.getString("value"),
      )
    }

    return eavStoreViewBoolTuple
  }

  private fun makeEavStoreViewFloatTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavStoreViewFloatTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getDouble("value"),
        body.getInteger("product_id"),
        body.getInteger("store_view_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getInteger("store_view_id"),
        body.getString("attribute_key"),
        body.getDouble("value"),
      )
    }

    return eavStoreViewFloatTuple
  }

  private fun makeEavStoreViewStringTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavStoreViewBoolTuple(body, isPutRequest)
  }

  private fun makeEavStoreViewIntTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavStoreViewIntTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("value"),
        body.getInteger("product_id"),
        body.getInteger("store_view_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getInteger("store_view_id"),
        body.getString("attribute_key"),
        body.getInteger("value"),
      )
    }

    return eavStoreViewIntTuple
  }

  private fun makeEavStoreViewMoneyTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavStoreViewFloatTuple(body, isPutRequest)
  }

  private fun makeEavStoreViewMultiSelectTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavStoreViewIntTuple(body, isPutRequest)
  }

  private fun makeEavStoreViewTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavStoreViewTuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("product_id"),
        body.getString("attribute_key"),
        body.getInteger("product_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getString("attribute_key"),
      )
    }

    return eavStoreViewTuple
  }

  private fun makeBasicStoreViewGetKeyTuple(body: JsonObject): Tuple {
    return Tuple.of(body.getInteger("product_id"),
      body.getInteger("store_view_id"),
      body.getString("attribute_key"))
  }

  private fun makeBasicGetKeyTuple(body: JsonObject): Tuple {
    return Tuple.of(body.getInteger("product_id"),
      body.getString("attribute_key"))
  }
}
