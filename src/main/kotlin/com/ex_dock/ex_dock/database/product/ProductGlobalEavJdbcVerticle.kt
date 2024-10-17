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

class ProductGlobalEavJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllEavGlobalBool()
    getEavGlobalBoolByKey()
    createEavGlobalBool()
    updateEavGlobalBool()
    deleteEavGlobalBool()

    getAllEavGlobalFloat()
    getEavGlobalFloatByKey()
    createEavGlobalFloat()
    updateEavGlobalFloat()
    deleteEavGlobalFloat()

    getAllEavGlobalString()
    getEavGlobalStringByKey()
    createEavGlobalString()
    updateEavGlobalString()
    deleteEavGlobalString()

    getAllEavGlobalInt()
    getEavGlobalIntByKey()
    createEavGlobalInt()
    updateEavGlobalInt()
    deleteEavGlobalInt()

    getAllEavGlobalMoney()
    getEavGlobalMoneyByKey()
    createEavGlobalMoney()
    updateEavGlobalMoney()
    deleteEavGlobalMoney()

    getAllEavGlobalMultiSelect()
    getEavGlobalMultiSelectByKey()
    createEavGlobalMultiSelect()
    updateEavGlobalMultiSelect()
    deleteEavGlobalMultiSelect()

    getAllEavGlobal()
    getEavGlobalByKey()
    createEavGlobal()
    updateEavGlobal()
    deleteEavGlobal()

    getALlEavGlobalInfo()
    getEavGlobalInfoByKey()
  }

  private fun getAllEavGlobalBool() {
    val getAllEavGlobalBoolConsumer = eventBus.consumer<String>("process.eavGlobal.getAllEavGlobalBool")
    getAllEavGlobalBoolConsumer.handler { message ->
      val query = "SELECT * FROM eav_global_bool"
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
              "eavGlobalBool" to rows.map { row ->
                obj(
                  makeEavGlobalBoolJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global bool found")
        }
      }
    }
  }

  private fun getEavGlobalBoolByKey() {
    val getEavGlobalBoolByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalBoolByKey")
    getEavGlobalBoolByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_global_bool WHERE product_id =? AND attribute_key =?"
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
              makeEavGlobalBoolJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No global bool found")
        }
      }
    }
  }

  private fun createEavGlobalBool() {
    val createEavGlobalBoolConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobalBool")
    createEavGlobalBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_global_bool (product_id, attribute_key, value) VALUES (?, ?, ?::bit(1))"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalBoolTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global bool created successfully")
      }
    }
  }

  private fun updateEavGlobalBool() {
    val updateEavGlobalBoolConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobalBool")
    updateEavGlobalBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_global_bool SET value =?::bit(1) WHERE product_id =? AND attribute_key =? "
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalBoolTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global bool updated successfully")
      }
    }
  }

  private fun deleteEavGlobalBool() {
    val deleteEavGlobalBoolConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobalBool")
    deleteEavGlobalBoolConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_global_bool WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global bool deleted successfully")
      }
    }
  }

  private fun getAllEavGlobalFloat() {
    val allEavGlobalFloatConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getAllEavGlobalFloat")
    allEavGlobalFloatConsumer.handler { message ->
      val query = "SELECT * FROM eav_global_float"
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
              "eavGlobalFloat" to rows.map { row ->
                obj(
                  makeEavGlobalFloatJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global float found")
        }
      }
    }
  }

  private fun getEavGlobalFloatByKey() {
    val getEavGlobalFloatByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalFloatByKey")
    getEavGlobalFloatByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_global_float WHERE product_id =? AND attribute_key =?"
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
              makeEavGlobalFloatJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No global float found")
        }
      }
    }
  }

  private fun createEavGlobalFloat() {
    val createEavGlobalFloatConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobalFloat")
    createEavGlobalFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_global_float (product_id, attribute_key, value) VALUES (?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalFloatTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global float created successfully")
      }
    }
  }

  private fun updateEavGlobalFloat() {
    val updateEavGlobalFloatConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobalFloat")
    updateEavGlobalFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_global_float SET value =? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalFloatTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global float updated successfully")
      }
    }
  }

  private fun deleteEavGlobalFloat() {
    val deleteEavGlobalFloatConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobalFloat")
    deleteEavGlobalFloatConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_global_float WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global float deleted successfully")
      }
    }
  }

  private fun getAllEavGlobalString() {
    val allEavGlobalStringConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getAllEavGlobalString")
    allEavGlobalStringConsumer.handler { message ->
      val query = "SELECT * FROM eav_global_string"
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
              "eavGlobalString" to rows.map { row ->
                obj(
                  makeEavGlobalStringJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global string found")
        }
      }
    }
  }

  private fun getEavGlobalStringByKey() {
    val getEavGlobalStringByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalStringByKey")
    getEavGlobalStringByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_global_string WHERE product_id =? AND attribute_key =?"
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
              makeEavGlobalStringJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No global string found")
        }
      }
    }
  }

  private fun createEavGlobalString() {
    val createEavGlobalStringConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobalString")
    createEavGlobalStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_global_string (product_id, attribute_key, value) VALUES (?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalStringTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global string created successfully")
      }
    }
  }

  private fun updateEavGlobalString() {
    val updateEavGlobalStringConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobalString")
    updateEavGlobalStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_global_string SET value =? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalStringTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global string updated successfully")
      }
    }
  }

  private fun deleteEavGlobalString() {
    val deleteEavGlobalStringConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobalString")
    deleteEavGlobalStringConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_global_string WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global string deleted successfully")
      }
    }
  }

  private fun getAllEavGlobalInt() {
    val allEavGlobalIntConsumer = eventBus.consumer<String>("process.eavGlobal.getAllEavGlobalInt")
    allEavGlobalIntConsumer.handler { message ->
      val query = "SELECT * FROM eav_global_int"
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
              "eavGlobalInt" to rows.map { row ->
                obj(
                  makeEavGlobalIntJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global int found")
        }
      }
    }
  }

  private fun getEavGlobalIntByKey() {
    val getEavGlobalIntByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalIntByKey")
    getEavGlobalIntByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_global_int WHERE product_id =? AND attribute_key =?"
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
              makeEavGlobalIntJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No rows returned")
        }
      }
    }
  }

  private fun createEavGlobalInt() {
    val createEavGlobalIntConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobalInt")
    createEavGlobalIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_global_int (product_id, attribute_key, value) VALUES (?,?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalIntTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global int created successfully")
      }
    }
  }

  private fun updateEavGlobalInt() {
    val updateEavGlobalIntConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobalInt")
    updateEavGlobalIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_global_int SET value =? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalIntTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global int updated successfully")
      }
    }
  }

  private fun deleteEavGlobalInt() {
    val deleteEavGlobalIntConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobalInt")
    deleteEavGlobalIntConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_global_int WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global int deleted successfully")
      }
    }
  }

  private fun getAllEavGlobalMoney() {
    val allEavGlobalMoneyConsumer = eventBus.consumer<String>("process.eavGlobal.getAllEavGlobalMoney")
    allEavGlobalMoneyConsumer.handler { message ->
      val query = "SELECT * FROM eav_global_money"
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
              "eavGlobalMoney" to rows.map { row ->
                obj(
                  makeEavGlobalMoneyJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global money found")
        }
      }
    }
  }

  private fun getEavGlobalMoneyByKey() {
    val getEavGlobalMoneyByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalMoneyByKey")
    getEavGlobalMoneyByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_global_money WHERE product_id =? AND attribute_key =?"
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
              makeEavGlobalMoneyJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No Eav Global Money found")
        }
      }
    }
  }

  private fun createEavGlobalMoney() {
    val createEavGlobalMoneyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobalMoney")
    createEavGlobalMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_global_money (product_id, attribute_key, value) VALUES (?,?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalMoneyTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global money created successfully")
      }
    }
  }

  private fun updateEavGlobalMoney() {
    val updateEavGlobalMoneyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobalMoney")
    updateEavGlobalMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_global_money SET value =? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalMoneyTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global money updated successfully")
      }
    }
  }

  private fun deleteEavGlobalMoney() {
    val deleteEavGlobalMoneyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobalMoney")
    deleteEavGlobalMoneyConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_global_money WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global money deleted successfully")
      }
    }
  }

  private fun getAllEavGlobalMultiSelect() {
    val allEavGlobalMultiSelectConsumer = eventBus.consumer<String>("process.eavGlobal.getAllEavGlobalMultiSelect")
    allEavGlobalMultiSelectConsumer.handler { message ->
      val query = "SELECT * FROM eav_global_multi_select"
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
              "eavGlobalMultiSelect" to rows.map { row ->
                obj(
                  makeEavGlobalMultiSelectJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global multi-select found")
        }
      }
    }
  }

  private fun getEavGlobalMultiSelectByKey() {
    val getEavGlobalMultiSelectByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalMultiSelectByKey")
    getEavGlobalMultiSelectByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_global_multi_select WHERE product_id =? AND attribute_key =?"
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
              makeEavGlobalMultiSelectJsonFields(rows.first())
            )
          }
          message.reply(json)
        }
      }
    }
  }

  private fun createEavGlobalMultiSelect() {
    val createEavGlobalMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobalMultiSelect")
    createEavGlobalMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_global_multi_select (product_id, attribute_key, value) VALUES (?,?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalMultiSelectTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global multi-select created successfully")
      }
    }
  }

  private fun updateEavGlobalMultiSelect() {
    val updateEavGlobalMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobalMultiSelect")
    updateEavGlobalMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_global_multi_select SET value =? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalMultiSelectTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global multi-select updated successfully")
      }
    }
  }

  private fun deleteEavGlobalMultiSelect() {
    val deleteEavGlobalMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobalMultiSelect")
    deleteEavGlobalMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_global_multi_select WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global multi-select deleted successfully")
      }
    }
  }

  private fun getAllEavGlobal() {
    val allEavGlobalConsumer = eventBus.consumer<String>("process.eavGlobal.getAllEavGlobal")
    allEavGlobalConsumer.handler { message ->
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
              "eavGlobal" to rows.map { row ->
                obj(
                  makeEavGlobalJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global found")
        }
      }
    }
  }

  private fun getEavGlobalByKey() {
    val getEavGlobalByKeyConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.getEavGlobalByKey")
    getEavGlobalByKeyConsumer.handler { message ->
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
              makeEavGlobalJsonFields(rows.first())
            )
          }
          message.reply(json)
        }
      }
    }
  }

  private fun createEavGlobal() {
    val createEavGlobalConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.createEavGlobal")
    createEavGlobalConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav (product_id, attribute_key) VALUES (?,?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global created successfully")
      }
    }
  }

  private fun updateEavGlobal() {
    val updateEavGlobalConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.updateEavGlobal")
    updateEavGlobalConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav SET product_id =?, attribute_key=? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavGlobalTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV global updated successfully")
      }
    }
  }

  private fun deleteEavGlobal() {
    val deleteEavGlobalConsumer = eventBus.consumer<JsonObject>("process.eavGlobal.deleteEavGlobal")
    deleteEavGlobalConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete {
        message.reply("EAV global deleted successfully")
      }
    }
  }

  private fun getALlEavGlobalInfo() {
    val allEavGlobalInfoConsumer = eventBus.consumer<String>("process.eavGlobal.getAllEavGlobalInfo")
    allEavGlobalInfoConsumer.handler { message ->
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, egb.value AS bool_value, " +
        "egf.value AS float_value, egs.value AS string_value, " +
        "egi.value AS int_value, egm.value AS money_value, " +
        "egms.value AS multi_select_value, cpa.attribute_key FROM products " +
        "LEFT JOIN public.eav_global_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_global_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_global_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_global_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_global_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_global_string egs on products.product_id = egs.product_id " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key "
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
              "eavGlobalInfo" to rows.map { row ->
                obj(
                  makeEavGlobalInfoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global info found")
        }
      }
    }
  }

  private fun getEavGlobalInfoByKey() {
    val getEavGlobalInfoByKeyConsumer = eventBus.consumer<Int>("process.eavGlobal.getEavGlobalInfoByKey")
    getEavGlobalInfoByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, egb.value AS bool_value, " +
        "egf.value AS float_value, egs.value AS string_value, " +
        "egi.value AS int_value, egm.value AS money_value, " +
        "egms.value AS multi_select_value, cpa.attribute_key FROM products " +
        "LEFT JOIN public.eav_global_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_global_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_global_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_global_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_global_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_global_string egs on products.product_id = egs.product_id " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
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
              "eavGlobalInfo" to rows.map { row ->
                obj(
                  makeEavGlobalInfoJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No global info found for product ID: $body")
        }
      }
    }
  }

  private fun makeEavGlobalBoolJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getBoolean("value")
    )
  }

  private fun makeEavGlobalFloatJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeEavGlobalStringJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getString("value")
    )
  }

  private fun makeEavGlobalIntJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeEavGlobalMoneyJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeEavGlobalMultiSelectJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeEavGlobalJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
    )
  }

  private fun makeEavGlobalInfoJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "globalBool" to row.getBoolean("bool_value"),
      "globalFloat" to row.getDouble("float_value"),
      "globalString" to row.getString("string_value"),
      "globalInt" to row.getInteger("int_value"),
      "globalMoney" to row.getDouble("money_value"),
      "globalMultiSelect" to row.getInteger("multi_select_value"),
    )
  }

  private fun makeEavGlobalBoolTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavGlobalBoolTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("value"),
        body.getInteger("product_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getString("attribute_key"),
        body.getString("value"),
      )
    }

    return eavGlobalBoolTuple
  }

  private fun makeEavGlobalFloatTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavGlobalFloatTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getDouble("value"),
        body.getInteger("product_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getString("attribute_key"),
        body.getDouble("value"),
      )
    }

    return eavGlobalFloatTuple
  }

  private fun makeEavGlobalStringTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavGlobalBoolTuple(body, isPutRequest)
  }

  private fun makeEavGlobalIntTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavGlobalIntTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("value"),
        body.getInteger("product_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getString("attribute_key"),
        body.getInteger("value"),
      )
    }

    return eavGlobalIntTuple
  }

  private fun makeEavGlobalMoneyTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavGlobalFloatTuple(body, isPutRequest)
  }

  private fun makeEavGlobalMultiSelectTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavGlobalIntTuple(body, isPutRequest)
  }

  private fun makeEavGlobalTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavGlobalTuple = if (isPutRequest) {
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

    return eavGlobalTuple
  }

  private fun makeBasicGetKeyTuple(body: JsonObject): Tuple {
    return Tuple.of(body.getInteger("product_id"), body.getString("attribute_key"))
  }
}
