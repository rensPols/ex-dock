package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class ProductMultiSelectJdbcVerticleTest {
  private lateinit var eventBus: EventBus

  private val verticleDeployHelper = VerticleDeployHelper()

  private var productId = -1

  private var productJson = json {
    obj(
      "product_id" to productId,
      "name" to "test name",
      "short_name" to "test short name",
      "description" to "test description",
      "short_description" to "test short description"
    )
  }

  private var customProductAttributeJson = json {
    obj(
      "attribute_key" to "test attribute key",
      "scope" to 2,
      "name" to "test name",
      "type" to "int",
      "multiselect" to "0",
      "required" to "1"
    )
  }

  private val msaBool = json {
    obj(
      "attribute_key" to "test attribute key",
      "option" to 1,
      "value" to "1"
    )
  }

  private val expectedMsaBool = json {
    obj(
      "attribute_key" to "test attribute key",
      "option" to 1,
      "value" to true
    )
  }

  private val msaFloat = json {
    obj(
      "attribute_key" to "test attribute key",
      "option" to 1,
      "value" to 1.0
    )
  }

  private val msaString = json {
    obj(
      "attribute_key" to "test attribute key",
      "option" to 1,
      "value" to "test value"
    )
  }

  private val msaInt = json {
    obj(
      "attribute_key" to "test attribute key",
      "option" to 1,
      "value" to 1
    )
  }

  private val msaMoney = json {
    obj(
      "attribute_key" to "test attribute key",
      "option" to 1,
      "value" to 1.0
    )
  }

  private var msaInfoJson = json {
    obj(
      "product_id" to productId,
      "attribute_key" to "test attribute key",
      "multiSelectBool" to true,
      "multiSelectFloat" to 1.0,
      "multiSelectString" to "test value",
      "multiSelectInt" to 1,
      "multiSelectMoney" to 1.0
    )
  }

  private var eavJson = json {
    obj(
      "product_id" to productId,
      "attribute_key" to "test attribute key",
    )
  }

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    Future.all(deployVerticles(vertx)).onFailure {
      testContext.failNow(it)
    }.onComplete {
      eventBus.request<Int>("process.products.createProduct", productJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createProductMsg ->
        productId = createProductMsg.result().body()
        assertEquals(createProductMsg.result().body()::class.simpleName, "Int")

        eventBus.request<String>("process.attributes.createCustomAttribute", customProductAttributeJson)
          .onFailure {
            testContext.failNow(it)
          }.onComplete { createCustomAttributeMsg ->
            assert(createCustomAttributeMsg.succeeded())
            assertEquals(createCustomAttributeMsg.result().body(), "Custom attribute created successfully")

            eventBus.request<Int>("process.multiSelect.createMultiSelectAttributesBool", msaBool).onFailure {
              testContext.failNow(it)
            }.onComplete { createMsaBoolMsg ->
              assert(createMsaBoolMsg.succeeded())

              eventBus.request<Int>("process.multiSelect.createMultiSelectAttributesFloat", msaFloat).onFailure {
                testContext.failNow(it)
              }.onComplete { createMsaFloatMsg ->
                assert(createMsaFloatMsg.succeeded())

                eventBus.request<Int>("process.multiSelect.createMultiSelectAttributesString", msaString).onFailure {
                  testContext.failNow(it)
                }.onComplete { createMsaStringMsg ->
                  assert(createMsaStringMsg.succeeded())

                  eventBus.request<Int>("process.multiSelect.createMultiSelectAttributesInt", msaInt).onFailure {
                    testContext.failNow(it)
                  }.onComplete { createMsaIntMsg ->
                    assert(createMsaIntMsg.succeeded())

                    eventBus.request<Int>("process.multiSelect.createMultiSelectAttributesMoney", msaMoney).onFailure {
                      testContext.failNow(it)
                    }.onComplete { createMsaMoneyMsg ->
                      assert(createMsaMoneyMsg.succeeded())

                      productJson = json {
                        obj(
                          "product_id" to productId,
                          "name" to "test name",
                          "short_name" to "test short name",
                          "description" to "test description",
                          "short_description" to "test short description"
                        )
                      }

                      msaInfoJson  = json {
                        obj(
                          "product_id" to productId,
                          "attribute_key" to "test attribute key",
                          "multiSelectBool" to true,
                          "multiSelectFloat" to 1.0,
                          "multiSelectString" to "test value",
                          "multiSelectInt" to 1,
                          "multiSelectMoney" to 1.0
                        )
                      }

                      eavJson = json {
                        obj(
                          "product_id" to productId,
                          "attribute_key" to "test attribute key",
                        )
                      }

                      eventBus.request<String>("process.eavGlobal.createEavGlobal", eavJson).onFailure {
                        testContext.failNow(it)
                      }.onComplete { createEavGlobalMsg ->
                        assert(createEavGlobalMsg.succeeded())
                        assertEquals("EAV global created successfully", createEavGlobalMsg.result().body())

                        testContext.completeNow()
                      }
                    }
                  }
                }
              }
            }
          }
      }
    }
  }

  @Test
  fun testGetAllMsaBool(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getAllMultiSelectAttributesBool", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllMsaBoolMsg ->
      assert(getAllMsaBoolMsg.succeeded())
      assertEquals(
        json { obj("multiSelectBool" to listOf(expectedMsaBool)) }, getAllMsaBoolMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetMsaBoolByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesBoolByKey", msaBool).onFailure {
      testContext.failNow(it)
    }.onComplete { getMsaByKeyMsg ->
      assert(getMsaByKeyMsg.succeeded())
      assertEquals(expectedMsaBool, getMsaByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateMultiSelectBool(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMsaBoolJson = json {
      obj(
        "attribute_key" to "test attribute key",
        "option" to 1,
        "value" to "0"
      )
    }

    val expectedUpdatedMsaBoolJson = json {
      obj(
        "attribute_key" to "test attribute key",
        "option" to 1,
        "value" to false
      )
    }

    eventBus.request<String>("process.multiSelect.updateMultiSelectAttributesBool", updatedMsaBoolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateMsaBoolMsg ->
      assert(updateMsaBoolMsg.succeeded())
      assertEquals("Multi-select attribute bool updated successfully", updateMsaBoolMsg.result().body())

      eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesBoolByKey", updatedMsaBoolJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getMsaByKeyMsg ->
          assert(getMsaByKeyMsg.succeeded())
          assertEquals(expectedUpdatedMsaBoolJson, getMsaByKeyMsg.result().body())

          testContext.completeNow()
        }
    }
  }

  @Test
  fun testGetAllMultiSelectAttributesFloat(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getAllMultiSelectAttributesFloat", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllMsaFloatMsg ->
      assert(getAllMsaFloatMsg.succeeded())
      assertEquals(json { obj("multiSelectFloat" to listOf(msaFloat)) }, getAllMsaFloatMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetMsaFloatByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesFloatByKey", msaFloat).onFailure {
      testContext.failNow(it)
    }.onComplete { getMsaByKeyMsg ->
      assert(getMsaByKeyMsg.succeeded())
      assertEquals(msaFloat, getMsaByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateMultiSelectFloat(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMsaFloatJson = json {
      obj(
        "attribute_key" to "test attribute key",
        "option" to 1,
        "value" to 2.5
      )
    }

    eventBus.request<String>("process.multiSelect.updateMultiSelectAttributesFloat", updatedMsaFloatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateMsaFloatMsg ->
      assert(updateMsaFloatMsg.succeeded())
      assertEquals("Multi-select attribute float updated successfully", updateMsaFloatMsg.result().body())

      eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesFloatByKey", updatedMsaFloatJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getUpdatedMsaFloatMsg ->
          assert(getUpdatedMsaFloatMsg.succeeded())
          assertEquals(updatedMsaFloatJson, getUpdatedMsaFloatMsg.result().body())

          testContext.completeNow()
        }
    }
  }

  @Test
  fun testGetAllMultiSelectAttributesString(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getAllMultiSelectAttributesString", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllMsaStringMsg ->
      assert(getAllMsaStringMsg.succeeded())
      assertEquals(json { obj("multiSelectString" to listOf(msaString)) }, getAllMsaStringMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetMsaStringByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesStringByKey", msaString).onFailure {
      testContext.failNow(it)
    }.onComplete { getMsaByKeyMsg ->
      assert(getMsaByKeyMsg.succeeded())
      assertEquals(msaString, getMsaByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateMultiSelectString(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMsaStringJson = json {
      obj(
        "attribute_key" to "test attribute key",
        "option" to 1,
        "value" to "test updated value"
      )
    }

    eventBus.request<String>("process.multiSelect.updateMultiSelectAttributesString", updatedMsaStringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateMsaStringMsg ->
      assert(updateMsaStringMsg.succeeded())
      assertEquals("Multi-select attribute string updated successfully", updateMsaStringMsg.result().body())

      eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesStringByKey", updatedMsaStringJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getUpdatedMsaStringMsg ->
          assert(getUpdatedMsaStringMsg.succeeded())
          assertEquals(updatedMsaStringJson, getUpdatedMsaStringMsg.result().body())

          testContext.completeNow()
        }
    }
  }

  @Test
  fun testGetAllMultiSelectAttributesInt(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getAllMultiSelectAttributesInt", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllMsaIntMsg ->
      assert(getAllMsaIntMsg.succeeded())
      assertEquals(json { obj("multiSelectInt" to listOf(msaInt)) }, getAllMsaIntMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetMsaIntByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesIntByKey", msaInt).onFailure {
      testContext.failNow(it)
    }.onComplete { getMsaByKeyMsg ->
      assert(getMsaByKeyMsg.succeeded())
      assertEquals(msaInt, getMsaByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateMultiSelectInt(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMsaIntJson = json {
      obj(
        "attribute_key" to "test attribute key",
        "option" to 1,
        "value" to 3
      )
    }

    eventBus.request<String>("process.multiSelect.updateMultiSelectAttributesInt", updatedMsaIntJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateMsaIntMsg ->
      assert(updateMsaIntMsg.succeeded())
      assertEquals("Multi-select attribute int updated successfully", updateMsaIntMsg.result().body())

      eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesIntByKey", updatedMsaIntJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getUpdatedMsaIntMsg ->
          assert(getUpdatedMsaIntMsg.succeeded())
          assertEquals(updatedMsaIntJson, getUpdatedMsaIntMsg.result().body())

          testContext.completeNow()
        }
    }
  }

  @Test
  fun testGetAllMultiSelectMoney(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getAllMultiSelectAttributesMoney", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllMsaMoneyMsg ->
      assert(getAllMsaMoneyMsg.succeeded())
      assertEquals(json { obj("multiSelectMoney" to listOf(msaMoney)) }, getAllMsaMoneyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetMsaMoneyByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesMoneyByKey", msaMoney).onFailure {
      testContext.failNow(it)
    }.onComplete { getMsaByKeyMsg ->
      assert(getMsaByKeyMsg.succeeded())
      assertEquals(msaMoney, getMsaByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateMultiSelectMoney(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMsaMoneyJson = json {
      obj(
        "attribute_key" to "test attribute key",
        "option" to 1,
        "value" to 3.5
      )
    }

    eventBus.request<String>("process.multiSelect.updateMultiSelectAttributesMoney", updatedMsaMoneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateMsaMoneyMsg ->
      assert(updateMsaMoneyMsg.succeeded())
      assertEquals("Multi-select attribute money updated successfully", updateMsaMoneyMsg.result().body())

      eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesMoneyByKey", updatedMsaMoneyJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getUpdatedMsaMoneyMsg ->
          assert(getUpdatedMsaMoneyMsg.succeeded())
          assertEquals(updatedMsaMoneyJson, getUpdatedMsaMoneyMsg.result().body())

          testContext.completeNow()
        }
    }
  }

  @Test
  fun testGetAllMultiSelectAttributesInfo(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getAllMultiSelectAttributesInfo", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllMsaInfoMsg ->
      assert(getAllMsaInfoMsg.succeeded())
      assertEquals(json { obj("multiSelectInfo" to listOf(msaInfoJson)) }, getAllMsaInfoMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetMsaInfoByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.multiSelect.getMultiSelectAttributesInfoByKey", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getMsaByKeyMsg ->
      assert(getMsaByKeyMsg.succeeded())
      assertEquals(json { obj("multiSelectInfo" to listOf(msaInfoJson)) }, getMsaByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.eavGlobal.deleteEavGlobal", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteEavGlobalMsg ->
      assert(deleteEavGlobalMsg.succeeded())
      assertEquals("EAV global deleted successfully", deleteEavGlobalMsg.result().body())
      eventBus.request<String>(
        "process.multiSelect.deleteMultiSelectAttributesBool",
        msaBool
      ).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteMsaBoolMsg ->
        assert(deleteMsaBoolMsg.succeeded())
        assertEquals("Multi-select attribute bool deleted successfully", deleteMsaBoolMsg.result().body())

        eventBus.request<String>(
          "process.multiSelect.deleteMultiSelectAttributesFloat",
          msaFloat
        ).onFailure {
          testContext.failNow(it)
        }.onComplete { deleteMsaFloatMsg ->
          assert(deleteMsaFloatMsg.succeeded())
          assertEquals("Multi-select attribute float deleted successfully", deleteMsaFloatMsg.result().body())

          eventBus.request<String>(
            "process.multiSelect.deleteMultiSelectAttributesString",
            msaString
          ).onFailure {
            testContext.failNow(it)
          }.onComplete { deleteMsaStringMsg ->
            assert(deleteMsaStringMsg.succeeded())
            assertEquals("Multi-select attribute string deleted successfully", deleteMsaStringMsg.result().body())

            eventBus.request<String>(
              "process.multiSelect.deleteMultiSelectAttributesInt",
              msaInt
            ).onFailure {
              testContext.failNow(it)
            }.onComplete { deleteMsaIntMsg ->
              assert(deleteMsaIntMsg.succeeded())
              assertEquals("Multi-select attribute int deleted successfully", deleteMsaIntMsg.result().body())

              eventBus.request<String>(
                "process.multiSelect.deleteMultiSelectAttributesMoney",
                msaMoney
              ).onFailure {
                testContext.failNow(it)
              }.onComplete { deleteMsaMoneyMsg ->
                assert(deleteMsaMoneyMsg.succeeded())
                assertEquals("Multi-select attribute money deleted successfully", deleteMsaMoneyMsg.result().body())

                eventBus.request<String>(
                  "process.attributes.deleteCustomAttribute",
                  customProductAttributeJson.getString("attribute_key")
                ).onFailure {
                  testContext.failNow(it)
                }.onComplete { deleteCustomAttributeMsg ->
                  assert(deleteCustomAttributeMsg.succeeded())
                  assertEquals("Custom attribute deleted successfully", deleteCustomAttributeMsg.result().body())

                  eventBus.request<Int>("process.products.deleteProduct", productId).onFailure {
                    testContext.failNow(it)
                  }.onComplete { deleteProductMsg ->
                    assert(deleteProductMsg.succeeded())
                    assertEquals("Product deleted successfully", deleteProductMsg.result().body())

                    testContext.completeNow()
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun deployVerticles(vertx: Vertx): MutableList<Future<Void>> {
    val verticleList: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

    verticleList.add(
      verticleDeployHelper.deployWorkerHelper(
        vertx,
        ProductCustomAttributesJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )
    verticleList.add(
      verticleDeployHelper.deployWorkerHelper(
        vertx,
        ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )
    verticleList.add(
      verticleDeployHelper.deployWorkerHelper(
        vertx,
        ProductMultiSelectJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )
    verticleList.add(
      verticleDeployHelper.deployWorkerHelper(
        vertx,
        ProductGlobalEavJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )

    return verticleList
  }
}
