package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class ProductCustomAttributesJdbcVerticleTest {
  private lateinit var eventBus: EventBus

  private val productCustomAttributesJson = json {
    obj(
      "attribute_key" to "test attribute key",
      "scope" to 2,
      "name" to "test name",
      "type" to "int",
      "multiselect" to "0",
      "required" to "1"
    )
  }


  private val expectedResult = json {
    obj(
      "attribute_key" to "test attribute key",
      "scope" to 2,
      "name" to "test name",
      "type" to "int",
      "multiselect" to false,
      "required" to true
    )
  }

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    deployWorkerVerticleHelper(
      vertx,
      ProductCustomAttributesJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ).onFailure {
      testContext.failNow(it)
    }.onComplete {
      eventBus.request<Int>("process.attributes.createCustomAttribute", productCustomAttributesJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createCustomAttributeMsg ->
        assert(createCustomAttributeMsg.succeeded())
        assertEquals("Custom attribute created successfully", createCustomAttributeMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun getAllCustomAttributes(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.attributes.getAllCustomAttributes", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllCustomAttributesMsg ->
      assert(getAllCustomAttributesMsg.succeeded())
      assertEquals(
        json { obj("customAttributes" to listOf(expectedResult)) }
        , getAllCustomAttributesMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun getCustomAttributeByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.attributes.getCustomAttributeByKey",
      productCustomAttributesJson.getString("attribute_key")).onFailure {
      testContext.failNow(it)
    }.onComplete { getCustomAttributeByKeyMsg ->
      assert(getCustomAttributeByKeyMsg.succeeded())
      assertEquals(expectedResult, getCustomAttributeByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun updateCustomAttribute(vertx: Vertx, testContext: VertxTestContext) {
    val updatedProductCustomAttributesJson = json {
      obj(
        "attribute_key" to productCustomAttributesJson.getString("attribute_key"),
        "scope" to 2,
        "name" to "updated test name",
        "type" to "int",
        "multiselect" to "0",
        "required" to "1"
      )
    }

    val expectedUpdatedResult = json {
      obj(
        "attribute_key" to productCustomAttributesJson.getString("attribute_key"),
        "scope" to 2,
        "name" to "updated test name",
        "type" to "int",
        "multiselect" to false,
        "required" to true
      )
    }

    eventBus.request<JsonObject>("process.attributes.updateCustomAttribute", updatedProductCustomAttributesJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateCustomAttributeMsg ->
      assert(updateCustomAttributeMsg.succeeded())
      assertEquals("Custom attribute updated successfully", updateCustomAttributeMsg.result().body())

      eventBus.request<JsonObject>("process.attributes.getCustomAttributeByKey",
        updatedProductCustomAttributesJson.getString("attribute_key")).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedCustomAttributeByKeyMsg ->
        assert(getUpdatedCustomAttributeByKeyMsg.succeeded())
        assertEquals(expectedUpdatedResult, getUpdatedCustomAttributeByKeyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.attributes.deleteCustomAttribute",
      productCustomAttributesJson.getString("attribute_key")).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteCustomAttributeMsg ->
      assert(deleteCustomAttributeMsg.succeeded())
      assertEquals("Custom attribute deleted successfully", deleteCustomAttributeMsg.result().body())

      testContext.completeNow()
    }
  }
}
