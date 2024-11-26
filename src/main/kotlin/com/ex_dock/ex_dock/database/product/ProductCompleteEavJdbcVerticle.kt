package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.connection.getConnection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

class ProductCompleteEavJdbcVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private val failedMessage: String = "failed"

  override fun start() {
    client = getConnection(vertx)
    eventBus = vertx.eventBus()

    getAllCompleteProductEavData()
    getAllCompleteProductEavDataByProductId()
  }

  private fun getAllCompleteProductEavData() {
    val consumer = eventBus.consumer<String>("process.completeEav.getAll")
    consumer.handler { message ->
      val query = "SELECT " +
        "products.product_id AS product_id, " +
        "products.name AS product_name, " +
        "products.short_name AS product_short_name, " +
        "products.description AS product_description, " +
        "products.short_description AS product_short_description, " +
        "e.attribute_key AS attribute_key, " +
        "egb.value AS global_bool, " +
        "egf.value AS global_float, " +
        "egs.value AS global_string, " +
        "egi.value AS global_int, " +
        "egm.value AS global_money, " +
        "egms.value AS global_multi_select, " +
        "esvb.value AS store_view_bool, " +
        "esvf.value AS store_view_float, " +
        "esvs.value AS store_view_string, " +
        "esvi.value AS store_view_int, " +
        "esvm.value AS store_view_money, " +
        "esvms.value AS store_view_multi_select, " +
        "ewb.value AS website_bool, " +
        "ewf.value AS website_float, " +
        "ews.value AS website_string, " +
        "ewi.value AS website_int, " +
        "ewm.value AS website_money, " +
        "ewms.value AS website_multi_select," +
        "msab.value AS multi_select_bool, " +
        "msaf.value AS multi_select_float, " +
        "msas.value AS multi_select_string, " +
        "msai.value AS multi_select_int, " +
        "msam.value AS multi_select_money, " +
        "w.website_id AS website_id, " +
        "sv.store_view_id AS store_view_id " +
        "FROM products " +
        "LEFT JOIN public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "LEFT JOIN public.eav_global_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_global_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_global_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_global_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_global_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_global_string egs on products.product_id = egs.product_id " +
        "LEFT JOIN public.eav_website_bool ewb on products.product_id = ewb.product_id " +
        "LEFT JOIN public.eav_website_float ewf on products.product_id = ewf.product_id " +
        "LEFT JOIN public.eav_website_int ewi on products.product_id = ewi.product_id " +
        "LEFT JOIN public.eav_website_money ewm on products.product_id = ewm.product_id " +
        "LEFT JOIN public.eav_website_multi_select ewms on products.product_id = ewms.product_id " +
        "LEFT JOIN public.eav_website_string ews on products.product_id = ews.product_id " +
        "LEFT JOIN public.eav_store_view_bool esvb on products.product_id = esvb.product_id " +
        "LEFT JOIN public.eav_store_view_float esvf on products.product_id = esvf.product_id " +
        "LEFT JOIN public.eav_store_view_int esvi on products.product_id = esvi.product_id " +
        "LEFT JOIN public.eav_store_view_money esvm on products.product_id = esvm.product_id " +
        "LEFT JOIN public.eav_store_view_multi_select esvms on products.product_id = esvms.product_id " +
        "LEFT JOIN public.eav_store_view_string esvs on products.product_id = esvs.product_id " +
        "LEFT JOIN public.multi_select_attributes_bool msab on cpa.attribute_key = msab.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_float msaf on cpa.attribute_key = msaf.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_int msai on cpa.attribute_key = msai.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_string msas on cpa.attribute_key = msas.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_money msam  on cpa.attribute_key = msam.attribute_key " +
        "LEFT JOIN public.store_view sv on esvb.store_view_id = sv.store_view_id " +
        "LEFT JOIN public.websites w on ewb.website_id = w.website_id"
      var json: JsonObject

      val rowsFuture = client.preparedQuery(query).execute()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = io.vertx.kotlin.core.json.json {
            obj(
              "completeEav" to rows.map { row ->
                obj(
                  makeCompleteEavDataJsonFields(row)
                )
              }
            )
          }

          message.reply(json)
        } else {
          message.reply("No complete product EAV data found!")
        }
      }
    }
  }

  private fun getAllCompleteProductEavDataByProductId() {
    val consumer = eventBus.consumer<Int>("process.completeEav.getById")
    consumer.handler { message ->
      val query = "SELECT " +
        "products.product_id AS product_id, " +
        "products.name AS product_name, " +
        "products.short_name AS product_short_name, " +
        "products.description AS product_description, " +
        "products.short_description AS product_short_description, " +
        "e.attribute_key AS attribute_key, " +
        "egb.value AS global_bool, " +
        "egf.value AS global_float, " +
        "egs.value AS global_string, " +
        "egi.value AS global_int, " +
        "egm.value AS global_money, " +
        "egms.value AS global_multi_select, " +
        "esvb.value AS store_view_bool, " +
        "esvf.value AS store_view_float, " +
        "esvs.value AS store_view_string, " +
        "esvi.value AS store_view_int, " +
        "esvm.value AS store_view_money, " +
        "esvms.value AS store_view_multi_select, " +
        "ewb.value AS website_bool, " +
        "ewf.value AS website_float, " +
        "ews.value AS website_string, " +
        "ewi.value AS website_int, " +
        "ewm.value AS website_money, " +
        "ewms.value AS website_multi_select," +
        "msab.value AS multi_select_bool, " +
        "msaf.value AS multi_select_float, " +
        "msas.value AS multi_select_string, " +
        "msai.value AS multi_select_int, " +
        "msam.value AS multi_select_money, " +
        "w.website_id AS website_id, " +
        "sv.store_view_id AS store_view_id " +
        "FROM products " +
        "LEFT JOIN public.eav e on products.product_id = e.product_id " +
        "LEFT JOIN public.custom_product_attributes cpa on cpa.attribute_key = e.attribute_key " +
        "LEFT JOIN public.eav_global_bool egb on products.product_id = egb.product_id " +
        "LEFT JOIN public.eav_global_float egf on products.product_id = egf.product_id " +
        "LEFT JOIN public.eav_global_int egi on products.product_id = egi.product_id " +
        "LEFT JOIN public.eav_global_money egm on products.product_id = egm.product_id " +
        "LEFT JOIN public.eav_global_multi_select egms on products.product_id = egms.product_id " +
        "LEFT JOIN public.eav_global_string egs on products.product_id = egs.product_id " +
        "LEFT JOIN public.eav_website_bool ewb on products.product_id = ewb.product_id " +
        "LEFT JOIN public.eav_website_float ewf on products.product_id = ewf.product_id " +
        "LEFT JOIN public.eav_website_int ewi on products.product_id = ewi.product_id " +
        "LEFT JOIN public.eav_website_money ewm on products.product_id = ewm.product_id " +
        "LEFT JOIN public.eav_website_multi_select ewms on products.product_id = ewms.product_id " +
        "LEFT JOIN public.eav_website_string ews on products.product_id = ews.product_id " +
        "LEFT JOIN public.eav_store_view_bool esvb on products.product_id = esvb.product_id " +
        "LEFT JOIN public.eav_store_view_float esvf on products.product_id = esvf.product_id " +
        "LEFT JOIN public.eav_store_view_int esvi on products.product_id = esvi.product_id " +
        "LEFT JOIN public.eav_store_view_money esvm on products.product_id = esvm.product_id " +
        "LEFT JOIN public.eav_store_view_multi_select esvms on products.product_id = esvms.product_id " +
        "LEFT JOIN public.eav_store_view_string esvs on products.product_id = esvs.product_id " +
        "LEFT JOIN public.multi_select_attributes_bool msab on cpa.attribute_key = msab.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_float msaf on cpa.attribute_key = msaf.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_int msai on cpa.attribute_key = msai.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_string msas on cpa.attribute_key = msas.attribute_key " +
        "LEFT JOIN public.multi_select_attributes_money msam  on cpa.attribute_key = msam.attribute_key " +
        "LEFT JOIN public.store_view sv on esvb.store_view_id = sv.store_view_id " +
        "LEFT JOIN public.websites w on ewb.website_id = w.website_id " +
        "WHERE products.product_id =?"
      var json: JsonObject

      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(message.body()))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onComplete { res ->
        val rows = res.result()
        if (rows.size() > 0) {
          json = io.vertx.kotlin.core.json.json {
            obj(
              "completeEav" to rows.map { row ->
                obj(
                  makeCompleteEavDataJsonFields(row)
                )
              }
            )
          }

          message.reply(json)
        } else {
          message.reply("No complete product EAV data found!")
        }
      }
    }
  }



  private fun makeCompleteEavDataJsonFields(row: Row): List<Pair<String, Any>> {
    return listOf(
      "product_id" to row.getInteger("product_id"),
      "attribute_key" to row.getString("attribute_key"),
      "store_view_id" to row.getInteger("store_view_id"),
      "website_id" to row.getInteger("website_id"),
      "product_name" to row.getString("product_name"),
      "product_short_name" to row.getString("product_short_name"),
      "product_description" to row.getString("product_description"),
      "product_short_description" to row.getString("product_short_description"),
      "global_bool" to row.getBoolean("global_bool"),
      "global_float" to row.getDouble("global_float"),
      "global_string" to row.getString("global_string"),
      "global_int" to row.getInteger("global_int"),
      "global_money" to row.getDouble("global_money"),
      "global_multi_select" to row.getString("global_multi_select"),
      "store_view_bool" to row.getString("store_view_bool"),
      "store_view_float" to row.getDouble("store_view_float"),
      "store_view_string" to row.getString("store_view_string"),
      "store_view_int" to row.getInteger("store_view_int"),
      "store_view_money" to row.getDouble("store_view_money"),
      "store_view_multi_select" to row.getString("store_view_multi_select"),
      "website_bool" to row.getString("website_bool"),
      "website_float" to row.getDouble("website_float"),
      "website_string" to row.getString("website_string"),
      "website_int" to row.getInteger("website_int"),
      "website_money" to row.getDouble("website_money"),
      "website_multi_select" to row.getString("website_multi_select"),
      "multi_select_bool" to row.getString("multi_select_bool"),
      "multi_select_float" to row.getDouble("multi_select_float"),
      "multi_select_string" to row.getString("multi_select_string"),
      "multi_select_int" to row.getInteger("multi_select_int"),
      "multi_select_money" to row.getDouble("multi_select_money")
    )
  }
}
