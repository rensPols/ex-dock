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

class ProductWebsiteEavJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllEavWebsiteBool()
    getEavWebsiteBoolByKey()
    createEavWebsiteBool()
    updateEavWebsiteBool()
    deleteEavWebsiteBool()

    getAllEavWebsiteFloat()
    getEavWebsiteFloatByKey()
    createEavWebsiteFloat()
    updateEavWebsiteFloat()
    deleteEavWebsiteFloat()

    getAllEavWebsiteString()
    getEavWebsiteStringByKey()
    createEavWebsiteString()
    updateEavWebsiteString()
    deleteEavWebsiteString()

    getAllEavWebsiteInt()
    getEavWebsiteIntByKey()
    createEavWebsiteInt()
    updateEavWebsiteInt()
    deleteEavWebsiteInt()

    getAllEavWebsiteMoney()
    getEavWebsiteMoneyByKey()
    createEavWebsiteMoney()
    updateEavWebsiteMoney()
    deleteEavWebsiteMoney()

    getAllEavWebsiteMultiSelect()
    getEavWebsiteMultiSelectByKey()
    createEavWebsiteMultiSelect()
    updateEavWebsiteMultiSelect()
    deleteEavWebsiteMultiSelect()

    getAllEavWebsite()
    getEavWebsiteByKey()
    createEavWebsite()
    updateEavWebsite()
    deleteEavWebsite()

    getALlEavWebsiteInfo()
    getEavWebsiteInfoByKey()
  }

  private fun getAllEavWebsiteBool() {
    val getAllEavWebsiteBoolConsumer = eventBus.consumer<String>("process.eavWebsite.getAllEavWebsiteBool")
    getAllEavWebsiteBoolConsumer.handler { message ->
      val query = "SELECT * FROM eav_website_bool"
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
              "eavWebsiteBool" to rows.map { row ->
                obj(
                  makeEavWebsiteBoolJsonFields(row)
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

  private fun getEavWebsiteBoolByKey() {
    val getEavWebsiteBoolByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteBoolByKey")
    getEavWebsiteBoolByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_website_bool WHERE product_id =? AND website_id =? AND attribute_key =?"
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
              makeEavWebsiteBoolJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No website bool found")
        }
      }
    }
  }

  private fun createEavWebsiteBool() {
    val createEavWebsiteBoolConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsiteBool")
    createEavWebsiteBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_website_bool (product_id, website_id, attribute_key, value) VALUES (?, ?, ?, ?::bit(1))"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteBoolTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website bool created successfully")
      }
    }
  }

  private fun updateEavWebsiteBool() {
    val updateEavWebsiteBoolConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsiteBool")
    updateEavWebsiteBoolConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_website_bool SET value =?::bit(1) WHERE product_id =? AND website_id =? AND attribute_key =? "
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteBoolTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website bool updated successfully")
      }
    }
  }

  private fun deleteEavWebsiteBool() {
    val deleteEavWebsiteBoolConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsiteBool")
    deleteEavWebsiteBoolConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_website_bool WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website bool deleted successfully")
      }
    }
  }

  private fun getAllEavWebsiteFloat() {
    val allEavWebsiteFloatConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getAllEavWebsiteFloat")
    allEavWebsiteFloatConsumer.handler { message ->
      val query = "SELECT * FROM eav_website_float"
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
              "eavWebsiteFloat" to rows.map { row ->
                obj(
                  makeEavWebsiteFloatJsonFields(row)
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

  private fun getEavWebsiteFloatByKey() {
    val getEavWebsiteFloatByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteFloatByKey")
    getEavWebsiteFloatByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_website_float WHERE product_id =? AND website_id =? AND attribute_key =?"
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
              makeEavWebsiteFloatJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No website float found")
        }
      }
    }
  }

  private fun createEavWebsiteFloat() {
    val createEavWebsiteFloatConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsiteFloat")
    createEavWebsiteFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_website_float (product_id, website_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteFloatTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website float created successfully")
      }
    }
  }

  private fun updateEavWebsiteFloat() {
    val updateEavWebsiteFloatConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsiteFloat")
    updateEavWebsiteFloatConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_website_float SET value =? WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteFloatTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website float updated successfully")
      }
    }
  }

  private fun deleteEavWebsiteFloat() {
    val deleteEavWebsiteFloatConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsiteFloat")
    deleteEavWebsiteFloatConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_website_float WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website float deleted successfully")
      }
    }
  }

  private fun getAllEavWebsiteString() {
    val allEavWebsiteStringConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getAllEavWebsiteString")
    allEavWebsiteStringConsumer.handler { message ->
      val query = "SELECT * FROM eav_website_string"
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
              "eavWebsiteString" to rows.map { row ->
                obj(
                  makeEavWebsiteStringJsonFields(row)
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

  private fun getEavWebsiteStringByKey() {
    val getEavWebsiteStringByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteStringByKey")
    getEavWebsiteStringByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_website_string WHERE product_id =? AND website_id =? AND attribute_key =?"
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
              makeEavWebsiteStringJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No website string found")
        }
      }
    }
  }

  private fun createEavWebsiteString() {
    val createEavWebsiteStringConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsiteString")
    createEavWebsiteStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_website_string (product_id, website_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteStringTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website string created successfully")
      }
    }
  }

  private fun updateEavWebsiteString() {
    val updateEavWebsiteStringConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsiteString")
    updateEavWebsiteStringConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_website_string SET value =? WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteStringTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website string updated successfully")
      }
    }
  }

  private fun deleteEavWebsiteString() {
    val deleteEavWebsiteStringConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsiteString")
    deleteEavWebsiteStringConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_website_string WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website string deleted successfully")
      }
    }
  }

  private fun getAllEavWebsiteInt() {
    val allEavWebsiteIntConsumer = eventBus.consumer<String>("process.eavWebsite.getAllEavWebsiteInt")
    allEavWebsiteIntConsumer.handler { message ->
      val query = "SELECT * FROM eav_website_int"
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
              "eavWebsiteInt" to rows.map { row ->
                obj(
                  makeEavWebsiteIntJsonFields(row)
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

  private fun getEavWebsiteIntByKey() {
    val getEavWebsiteIntByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteIntByKey")
    getEavWebsiteIntByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_website_int WHERE product_id =? AND website_id =? AND attribute_key =?"
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
              makeEavWebsiteIntJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No rows returned")
        }
      }
    }
  }

  private fun createEavWebsiteInt() {
    val createEavWebsiteIntConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsiteInt")
    createEavWebsiteIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_website_int (product_id, website_id ,attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteIntTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website int created successfully")
      }
    }
  }

  private fun updateEavWebsiteInt() {
    val updateEavWebsiteIntConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsiteInt")
    updateEavWebsiteIntConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_website_int SET value =? WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteIntTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website int updated successfully")
      }
    }
  }

  private fun deleteEavWebsiteInt() {
    val deleteEavWebsiteIntConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsiteInt")
    deleteEavWebsiteIntConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_website_int WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website int deleted successfully")
      }
    }
  }

  private fun getAllEavWebsiteMoney() {
    val allEavWebsiteMoneyConsumer = eventBus.consumer<String>("process.eavWebsite.getAllEavWebsiteMoney")
    allEavWebsiteMoneyConsumer.handler { message ->
      val query = "SELECT * FROM eav_website_money"
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
              "eavWebsiteMoney" to rows.map { row ->
                obj(
                  makeEavWebsiteMoneyJsonFields(row)
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

  private fun getEavWebsiteMoneyByKey() {
    val getEavWebsiteMoneyByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteMoneyByKey")
    getEavWebsiteMoneyByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_website_money WHERE product_id =? AND website_id =? AND attribute_key =?"
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
              makeEavWebsiteMoneyJsonFields(rows.first())
            )
          }
          message.reply(json)
        } else {
          message.reply("No Eav Global Money found")
        }
      }
    }
  }

  private fun createEavWebsiteMoney() {
    val createEavWebsiteMoneyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsiteMoney")
    createEavWebsiteMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_website_money (product_id, website_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteMoneyTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website money created successfully")
      }
    }
  }

  private fun updateEavWebsiteMoney() {
    val updateEavWebsiteMoneyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsiteMoney")
    updateEavWebsiteMoneyConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_website_money SET value =? WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteMoneyTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website money updated successfully")
      }
    }
  }

  private fun deleteEavWebsiteMoney() {
    val deleteEavWebsiteMoneyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsiteMoney")
    deleteEavWebsiteMoneyConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_website_money WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website money deleted successfully")
      }
    }
  }

  private fun getAllEavWebsiteMultiSelect() {
    val allEavWebsiteMultiSelectConsumer = eventBus.consumer<String>("process.eavWebsite.getAllEavWebsiteMultiSelect")
    allEavWebsiteMultiSelectConsumer.handler { message ->
      val query = "SELECT * FROM eav_website_multi_select"
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
              "eavWebsiteMultiSelect" to rows.map { row ->
                obj(
                  makeEavWebsiteMultiSelectJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website multi-select found")
        }
      }
    }
  }

  private fun getEavWebsiteMultiSelectByKey() {
    val getEavWebsiteMultiSelectByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteMultiSelectByKey")
    getEavWebsiteMultiSelectByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT * FROM eav_website_multi_select WHERE product_id =? AND website_id =? AND attribute_key =?"
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
              makeEavWebsiteMultiSelectJsonFields(rows.first())
            )
          }
          message.reply(json)
        }
      }
    }
  }

  private fun createEavWebsiteMultiSelect() {
    val createEavWebsiteMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsiteMultiSelect")
    createEavWebsiteMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav_website_multi_select (product_id, website_id, attribute_key, value) VALUES (?, ?, ?, ?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteMultiSelectTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website multi-select created successfully")
      }
    }
  }

  private fun updateEavWebsiteMultiSelect() {
    val updateEavWebsiteMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsiteMultiSelect")
    updateEavWebsiteMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav_website_multi_select SET value =? WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteMultiSelectTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website multi-select updated successfully")
      }
    }
  }

  private fun deleteEavWebsiteMultiSelect() {
    val deleteEavWebsiteMultiSelectConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsiteMultiSelect")
    deleteEavWebsiteMultiSelectConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav_website_multi_select WHERE product_id =? AND website_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicStoreViewGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website multi-select deleted successfully")
      }
    }
  }

  private fun getAllEavWebsite() {
    val allEavWebsiteConsumer = eventBus.consumer<String>("process.eavWebsite.getAllEavWebsite")
    allEavWebsiteConsumer.handler { message ->
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
              "eavWebsite" to rows.map { row ->
                obj(
                  makeEavWebsiteJsonFields(row)
                )
              }
            )
          }
          message.reply(json)
        } else {
          message.reply("No website found")
        }
      }
    }
  }

  private fun getEavWebsiteByKey() {
    val getEavWebsiteByKeyConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.getEavWebsiteByKey")
    getEavWebsiteByKeyConsumer.handler { message ->
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
              makeEavWebsiteJsonFields(rows.first())
            )
          }
          message.reply(json)
        }
      }
    }
  }

  private fun createEavWebsite() {
    val createEavWebsiteConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.createEavWebsite")
    createEavWebsiteConsumer.handler { message ->
      val body = message.body()
      val query =
        "INSERT INTO eav (product_id, attribute_key) VALUES (?,?)"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteTuple(body, false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website created successfully")
      }
    }
  }

  private fun updateEavWebsite() {
    val updateEavWebsiteConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.updateEavWebsite")
    updateEavWebsiteConsumer.handler { message ->
      val body = message.body()
      val query =
        "UPDATE eav SET product_id =?, attribute_key=? WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeEavWebsiteTuple(body, true))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        message.reply("EAV website updated successfully")
      }
    }
  }

  private fun deleteEavWebsite() {
    val deleteEavWebsiteConsumer = eventBus.consumer<JsonObject>("process.eavWebsite.deleteEavWebsite")
    deleteEavWebsiteConsumer.handler { message ->
      val body = message.body()
      val query = "DELETE FROM eav WHERE product_id =? AND attribute_key =?"
      val rowsFuture = client.preparedQuery(query).execute(makeBasicGetKeyTuple(body))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete {
        message.reply("EAV website deleted successfully")
      }
    }
  }

  private fun getALlEavWebsiteInfo() {
    val allEavWebsiteInfoConsumer = eventBus.consumer<String>("process.eavWebsite.getAllEavWebsiteInfo")
    allEavWebsiteInfoConsumer.handler { message ->
      val query = "SELECT products.product_id, products.name, products.short_name, " +
      "products.description, products.short_name, egb.value AS bool_value, " +
        "egf.value AS float_value, egs.value AS string_value, " +
        "egi.value AS int_value, egm.value AS money_value, " +
        "egms.value AS multi_select_value, cpa.attribute_key, w.website_id AS website_id FROM products " +
        "LEFT JOIN public.eav_website_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_website_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_website_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_website_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_website_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_website_string egs on products.product_id = egs.product_id " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "Left JOIN public.websites w ON egb.website_id = w.website_id "
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
              "eavWebsiteInfo" to rows.map { row ->
                obj(
                  makeEavWebsiteInfoJsonFields(row)
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

  private fun getEavWebsiteInfoByKey() {
    val getEavWebsiteInfoByKeyConsumer = eventBus.consumer<Int>("process.eavWebsite.getEavWebsiteInfoByKey")
    getEavWebsiteInfoByKeyConsumer.handler { message ->
      val body = message.body()
      val query = "SELECT products.product_id, products.name, products.short_name, " +
        "products.description, products.short_name, egb.value AS bool_value, " +
        "egf.value AS float_value, egs.value AS string_value, " +
        "egi.value AS int_value, egm.value AS money_value, " +
        "egms.value AS multi_select_value, cpa.attribute_key, w.website_id AS website_id FROM products " +
        "LEFT JOIN public.eav_website_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_website_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_website_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_website_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_website_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_website_string egs on products.product_id = egs.product_id " +
        "Left Join public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "Left JOIN public.websites w ON egb.website_id = w.website_id " +
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
              "eavWebsiteInfo" to rows.map { row ->
                obj(
                  makeEavWebsiteInfoJsonFields(row)
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

  private fun makeEavWebsiteBoolJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getBoolean("value")
    )
  }

  private fun makeEavWebsiteFloatJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeEavWebsiteStringJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getString("value")
    )
  }

  private fun makeEavWebsiteIntJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeEavWebsiteMoneyJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getDouble("value")
    )
  }

  private fun makeEavWebsiteMultiSelectJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "value" to row.getInteger("value")
    )
  }

  private fun makeEavWebsiteJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
    )
  }

  private fun makeEavWebsiteInfoJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "website_id" to row.getInteger("website_id"),
      "attribute_key" to row.getString("attribute_key"),
      "websiteBool" to row.getBoolean("bool_value"),
      "websiteFloat" to row.getDouble("float_value"),
      "websiteString" to row.getString("string_value"),
      "websiteInt" to row.getInteger("int_value"),
      "websiteMoney" to row.getDouble("money_value"),
      "websiteMultiSelect" to row.getInteger("multi_select_value"),
    )
  }

  private fun makeEavWebsiteBoolTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavWebsiteBoolTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("value"),
        body.getInteger("product_id"),
        body.getInteger("website_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getInteger("website_id"),
        body.getString("attribute_key"),
        body.getString("value"),
      )
    }

    return eavWebsiteBoolTuple
  }

  private fun makeEavWebsiteFloatTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavWebsiteFloatTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getDouble("value"),
        body.getInteger("product_id"),
        body.getInteger("website_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getInteger("website_id"),
        body.getString("attribute_key"),
        body.getDouble("value"),
      )
    }

    return eavWebsiteFloatTuple
  }

  private fun makeEavWebsiteStringTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavWebsiteBoolTuple(body, isPutRequest)
  }

  private fun makeEavWebsiteIntTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavWebsiteIntTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getInteger("value"),
        body.getInteger("product_id"),
        body.getInteger("website_id"),
        body.getString("attribute_key"),
      )
    } else {
      Tuple.of(
        body.getInteger("product_id"),
        body.getInteger("website_id"),
        body.getString("attribute_key"),
        body.getInteger("value"),
      )
    }

    return eavWebsiteIntTuple
  }

  private fun makeEavWebsiteMoneyTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavWebsiteFloatTuple(body, isPutRequest)
  }

  private fun makeEavWebsiteMultiSelectTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    return makeEavWebsiteIntTuple(body, isPutRequest)
  }

  private fun makeEavWebsiteTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val eavWebsiteTuple = if (isPutRequest) {
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

    return eavWebsiteTuple
  }

  private fun makeBasicStoreViewGetKeyTuple(body: JsonObject): Tuple {
    return Tuple.of(body.getInteger("product_id"),
      body.getInteger("website_id"),
      body.getString("attribute_key"))
  }

  private fun makeBasicGetKeyTuple(body: JsonObject): Tuple {
    return Tuple.of(body.getInteger("product_id"),
      body.getString("attribute_key"))
  }
}
