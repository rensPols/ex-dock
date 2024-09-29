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
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class ProductGlobalEavJdbcVerticleTest {
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

  private lateinit var boolJson: JsonObject

  private lateinit var expectedBoolJson: JsonObject

  private lateinit var floatJson: JsonObject

  private lateinit var stringJson: JsonObject

  private lateinit var intJson: JsonObject

  private lateinit var moneyJson: JsonObject

  private lateinit var multiSelectJson: JsonObject

  private lateinit var eavJson: JsonObject

  private lateinit var expectedFullEavJson: JsonObject


  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    Future.all(deployVerticles(vertx)).onFailure{
      testContext.failNow(it)
    }.onComplete {
      eventBus.request<Int>("process.products.createProduct", productJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createProductMsg ->
        productId = createProductMsg.result().body()
        assertEquals("Int", createProductMsg.result().body()::class.simpleName)

        eventBus.request<String>("process.attributes.createCustomAttribute", customProductAttributeJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createAttributeMsg ->
          assert(createAttributeMsg.succeeded())
          assertEquals("Custom attribute created successfully", createAttributeMsg.result().body())
          setAllJsonFields()

          eventBus.request<String>("process.eavGlobal.createEavGlobalBool", boolJson).onFailure {
            testContext.failNow(it)
          }.onComplete { createEavGlobalBoolMsg ->
            assert(createEavGlobalBoolMsg.succeeded())
            assertEquals("EAV global bool created successfully", createEavGlobalBoolMsg.result().body())

            eventBus.request<String>("process.eavGlobal.createEavGlobalFloat", floatJson).onFailure {
              testContext.failNow(it)
            }.onComplete { createEavGlobalFloatMsg ->
              assert(createEavGlobalFloatMsg.succeeded())
              assertEquals("EAV global float created successfully", createEavGlobalFloatMsg.result().body())

              eventBus.request<String>("process.eavGlobal.createEavGlobalString", stringJson).onFailure {
                testContext.failNow(it)
              }.onComplete { createEavGlobalStringMsg ->
                assert(createEavGlobalStringMsg.succeeded())
                assertEquals("EAV global string created successfully", createEavGlobalStringMsg.result().body())

                eventBus.request<String>("process.eavGlobal.createEavGlobalInt", intJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { createEavGlobalIntMsg ->
                  assert(createEavGlobalIntMsg.succeeded())
                  assertEquals("EAV global int created successfully", createEavGlobalIntMsg.result().body())

                  eventBus.request<String>("process.eavGlobal.createEavGlobalMoney", moneyJson).onFailure {
                    testContext.failNow(it)
                  }.onComplete { createEavGlobalMoneyMsg ->
                    assert(createEavGlobalMoneyMsg.succeeded())
                    assertEquals("EAV global money created successfully", createEavGlobalMoneyMsg.result().body())

                    eventBus.request<String>("process.eavGlobal.createEavGlobalMultiSelect", multiSelectJson)
                      .onFailure {
                        testContext.failNow(it)
                      }.onComplete { createEavGlobalMultiSelectMsg ->
                        assert(createEavGlobalMultiSelectMsg.succeeded())
                        assertEquals(
                          "EAV global multi-select created successfully",
                          createEavGlobalMultiSelectMsg.result().body()
                        )

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
  }

  @Test
  fun testGetAllEavGlobalBool(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalBool", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalBoolMsg ->
      assert(getAllEavGlobalBoolMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalBool" to listOf(expectedBoolJson))
        },
        getAllEavGlobalBoolMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalBoolByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalBoolByKey", boolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(expectedBoolJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobalBool(vertx: Vertx, testContext: VertxTestContext) {
    val updatedBoolJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to boolJson.getString("attribute_key"),
        "value" to "0"
      )
    }

    val expectedUpdatedBoolJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to boolJson.getString("attribute_key"),
        "value" to false
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobalBool", updatedBoolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global bool updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalBoolByKey", updatedBoolJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalBoolMsg ->
        assert(getUpdatedEavGlobalBoolMsg.succeeded())
        assertEquals(expectedUpdatedBoolJson, getUpdatedEavGlobalBoolMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testgetAllEavGlobalFloat(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalFloat", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalFloatMsg ->
      assert(getAllEavGlobalFloatMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalFloat" to listOf(floatJson))
        },
        getAllEavGlobalFloatMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalFloatByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalFloatByKey", floatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(floatJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobalFloat(vertx: Vertx, testContext: VertxTestContext) {
    val updatedFloatJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to floatJson.getString("attribute_key"),
        "value" to 10.5
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobalFloat", updatedFloatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global float updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalFloatByKey", updatedFloatJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalFloatMsg ->
        assert(getUpdatedEavGlobalFloatMsg.succeeded())
        assertEquals(updatedFloatJson, getUpdatedEavGlobalFloatMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavGlobalString(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalString", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalStringMsg ->
      assert(getAllEavGlobalStringMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalString" to listOf(stringJson))
        },
        getAllEavGlobalStringMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalStringByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalStringByKey", stringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(stringJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobalString(vertx: Vertx, testContext: VertxTestContext) {
    val updatedStringJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to stringJson.getString("attribute_key"),
        "value" to "New Value"
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobalString", updatedStringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global string updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalStringByKey", updatedStringJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalStringMsg ->
        assert(getUpdatedEavGlobalStringMsg.succeeded())
        assertEquals(updatedStringJson, getUpdatedEavGlobalStringMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavGlobalInt(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalInt", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalIntMsg ->
      assert(getAllEavGlobalIntMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalInt" to listOf(intJson))
        },
        getAllEavGlobalIntMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalIntByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalIntByKey", intJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(intJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobalInt(vertx: Vertx, testContext: VertxTestContext) {
    val updatedIntJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to intJson.getString("attribute_key"),
        "value" to 20
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobalInt", updatedIntJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global int updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalIntByKey", updatedIntJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalIntMsg ->
        assert(getUpdatedEavGlobalIntMsg.succeeded())
        assertEquals(updatedIntJson, getUpdatedEavGlobalIntMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavGlobalMoney(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalMoney", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalMoneyMsg ->
      assert(getAllEavGlobalMoneyMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalMoney" to listOf(moneyJson))
        },
        getAllEavGlobalMoneyMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalMoneyByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalMoneyByKey", moneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(moneyJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobalMoney(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMoneyJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to moneyJson.getString("attribute_key"),
        "value" to 100.50
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobalMoney", updatedMoneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global money updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalMoneyByKey", updatedMoneyJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalMoneyMsg ->
        assert(getUpdatedEavGlobalMoneyMsg.succeeded())
        assertEquals(updatedMoneyJson, getUpdatedEavGlobalMoneyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavGlobalMultiSelect(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalMultiSelect", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalMultiSelectMsg ->
      assert(getAllEavGlobalMultiSelectMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalMultiSelect" to listOf(multiSelectJson))
        },
        getAllEavGlobalMultiSelectMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalMultiSelectByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalMultiSelectByKey", multiSelectJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(multiSelectJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobalMultiSelect(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMultiSelectJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to multiSelectJson.getString("attribute_key"),
        "value" to 0
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobalMultiSelect", updatedMultiSelectJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global multi-select updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalMultiSelectByKey", updatedMultiSelectJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalMultiSelectMsg ->
        assert(getUpdatedEavGlobalMultiSelectMsg.succeeded())
        assertEquals(updatedMultiSelectJson, getUpdatedEavGlobalMultiSelectMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavGlobal(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobal", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalMsg ->
      assert(getAllEavGlobalMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobal" to listOf(eavJson))
        },
        getAllEavGlobalMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalByKey", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalByKeyMsg ->
      assert(getEavGlobalByKeyMsg.succeeded())
      assertEquals(eavJson, getEavGlobalByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavGlobal(vertx: Vertx, testContext: VertxTestContext) {
    val updatedEavJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to eavJson.getString("attribute_key"),
      )
    }

    eventBus.request<String>("process.eavGlobal.updateEavGlobal", updatedEavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavGlobalByKeyMsg ->
      assert(updateEavGlobalByKeyMsg.succeeded())
      assertEquals("EAV global updated successfully", updateEavGlobalByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalByKey", updatedEavJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavGlobalMsg ->
        assert(getUpdatedEavGlobalMsg.succeeded())
        assertEquals(updatedEavJson, getUpdatedEavGlobalMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavGlobalInfo(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getAllEavGlobalInfo", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavGlobalInfoMsg ->
      assert(getAllEavGlobalInfoMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalInfo" to listOf(expectedFullEavJson))
        }, getAllEavGlobalInfoMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavGlobalInfoByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavGlobal.getEavGlobalInfoByKey", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavGlobalInfoByKeyMsg ->
      assert(getEavGlobalInfoByKeyMsg.succeeded())
      assertEquals(
        json {
          obj("eavGlobalInfo" to listOf(expectedFullEavJson))
        }, getEavGlobalInfoByKeyMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.eavGlobal.deleteEavGlobal", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete {deleteEavGlobalMsg ->
      assert(deleteEavGlobalMsg.succeeded())
      assertEquals("EAV global deleted successfully", deleteEavGlobalMsg.result().body())

      eventBus.request<String>("process.eavGlobal.deleteEavGlobalMultiSelect", multiSelectJson).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteEavGlobalMultiSelectMsg ->
        assert(deleteEavGlobalMultiSelectMsg.succeeded())
        assertEquals(
          "EAV global multi-select deleted successfully",
          deleteEavGlobalMultiSelectMsg.result().body()
        )

        eventBus.request<String>("process.eavGlobal.deleteEavGlobalMoney", moneyJson).onFailure {
          testContext.failNow(it)
        }.onComplete { deleteEavGlobalMoneyMsg ->
          assert(deleteEavGlobalMoneyMsg.succeeded())
          assertEquals("EAV global money deleted successfully", deleteEavGlobalMoneyMsg.result().body())

          eventBus.request<String>("process.eavGlobal.deleteEavGlobalInt", intJson).onFailure {
            testContext.failNow(it)
          }.onComplete { deleteEavGlobalIntMsg ->
            assert(deleteEavGlobalIntMsg.succeeded())
            assertEquals("EAV global int deleted successfully", deleteEavGlobalIntMsg.result().body())

            eventBus.request<String>("process.eavGlobal.deleteEavGlobalString", stringJson).onFailure {
              testContext.failNow(it)
            }.onComplete { deleteEavGlobalStringMsg ->
              assert(deleteEavGlobalStringMsg.succeeded())
              assertEquals("EAV global string deleted successfully", deleteEavGlobalStringMsg.result().body())

              eventBus.request<String>("process.eavGlobal.deleteEavGlobalFloat", floatJson).onFailure {
                testContext.failNow(it)
              }.onComplete { deleteEavGlobalFloatMsg ->
                assert(deleteEavGlobalFloatMsg.succeeded())
                assertEquals("EAV global float deleted successfully", deleteEavGlobalFloatMsg.result().body())

                eventBus.request<String>("process.eavGlobal.deleteEavGlobalBool", boolJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { deleteEavGlobalBoolMsg ->
                  assert(deleteEavGlobalBoolMsg.succeeded())
                  assertEquals("EAV global bool deleted successfully", deleteEavGlobalBoolMsg.result().body())

                  eventBus.request<String>(
                    "process.attributes.deleteCustomAttribute",
                    customProductAttributeJson.getString("attribute_key")
                  ).onFailure {
                    testContext.failNow(it)
                  }.onComplete { deleteAttributeMsg ->
                    assert(deleteAttributeMsg.succeeded())
                    assertEquals("Custom attribute deleted successfully", deleteAttributeMsg.result().body())

                    eventBus.request<String>("process.products.deleteProduct", productId).onFailure {
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
  }

  private fun setAllJsonFields() {
    productJson = json {
      obj(
        "product_id" to productId,
        "name" to "test name",
        "short_name" to "test short name",
        "description" to "test description",
        "short_description" to "test short description"
      )
    }

    boolJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to "1"
      )
    }

    expectedBoolJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to true
      )
    }

    floatJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to 1.0
      )
    }

    stringJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to "test value"
      )
    }

    intJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to 1
      )
    }

    moneyJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to 10.0
      )
    }

    multiSelectJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "value" to 1
      )
    }

    eavJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
      )
    }

    expectedFullEavJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to "test attribute key",
        "globalBool" to true,
        "globalFloat" to 1.0,
        "globalString" to "test value",
        "globalInt" to 1,
        "globalMoney" to 10.0,
        "globalMultiSelect" to 1
      )
    }
  }

  private fun deployVerticles(vertx: Vertx): MutableList<Future<Void>> {
    val verticleList: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      ProductGlobalEavJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))
    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      ProductCustomAttributesJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))
    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))

    return verticleList
  }
}
