package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.database.scope.ScopeJdbcVerticle
import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
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
class ProductWebsiteEavJdbcVerticleTest {
  private lateinit var eventBus: EventBus

  private val verticleDeployHelper = VerticleDeployHelper()

  private var productId = -1

  private var websiteId = -1

  private var productJson = json {
    obj(
      "product_id" to productId,
      "name" to "test name",
      "short_name" to "test short name",
      "description" to "test description",
      "short_description" to "test short description"
    )
  }

  private var websiteJson = json {
    obj(
      "website_id" to websiteId,
      "website_name" to "test website name"
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
    Future.all(deployVerticles(vertx)).onFailure {
      testContext.failNow(it)
    }.onComplete {
      eventBus.request<Int>("process.products.createProduct", productJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createProductMsg ->
        productId = createProductMsg.result().body()
        assertEquals("Int", createProductMsg.result().body()::class.simpleName)

        eventBus.request<Int>("process.scope.createWebsite", websiteJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createWebsiteMsg ->
          websiteId = createWebsiteMsg.result().body()
          assertEquals("Int", createWebsiteMsg.result().body()::class.simpleName)

            eventBus.request<String>("process.attributes.createCustomAttribute", customProductAttributeJson).onFailure {
              testContext.failNow(it)
            }.onComplete { createAttributeMsg ->
              assert(createAttributeMsg.succeeded())
              assertEquals("Custom attribute created successfully", createAttributeMsg.result().body())
              setAllJsonFields()

              eventBus.request<String>("process.eavWebsite.createEavWebsiteBool", boolJson).onFailure {
                testContext.failNow(it)
              }.onComplete { createEavWebsiteBoolMsg ->
                assert(createEavWebsiteBoolMsg.succeeded())
                assertEquals("EAV website bool created successfully", createEavWebsiteBoolMsg.result().body())

                eventBus.request<String>("process.eavWebsite.createEavWebsiteFloat", floatJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { createEavWebsiteFloatMsg ->
                  assert(createEavWebsiteFloatMsg.succeeded())
                  assertEquals("EAV website float created successfully", createEavWebsiteFloatMsg.result().body())

                  eventBus.request<String>("process.eavWebsite.createEavWebsiteString", stringJson).onFailure {
                    testContext.failNow(it)
                  }.onComplete { createEavWebsiteStringMsg ->
                    assert(createEavWebsiteStringMsg.succeeded())
                    assertEquals("EAV website string created successfully", createEavWebsiteStringMsg.result().body())

                    eventBus.request<String>("process.eavWebsite.createEavWebsiteInt", intJson).onFailure {
                      testContext.failNow(it)
                    }.onComplete { createEavWebsiteIntMsg ->
                      assert(createEavWebsiteIntMsg.succeeded())
                      assertEquals("EAV website int created successfully", createEavWebsiteIntMsg.result().body())

                      eventBus.request<String>("process.eavWebsite.createEavWebsiteMoney", moneyJson).onFailure {
                        testContext.failNow(it)
                      }.onComplete { createEavWebsiteMoneyMsg ->
                        assert(createEavWebsiteMoneyMsg.succeeded())
                        assertEquals(
                          "EAV website money created successfully",
                          createEavWebsiteMoneyMsg.result().body()
                        )

                        eventBus.request<String>("process.eavWebsite.createEavWebsiteMultiSelect", multiSelectJson)
                          .onFailure {
                            testContext.failNow(it)
                          }.onComplete { createEavWebsiteMultiSelectMsg ->
                            assert(createEavWebsiteMultiSelectMsg.succeeded())
                            assertEquals(
                              "EAV website multi-select created successfully",
                              createEavWebsiteMultiSelectMsg.result().body()
                            )

                            eventBus.request<String>("process.eavWebsite.createEavWebsite", eavJson).onFailure {
                              testContext.failNow(it)
                            }.onComplete { createEavWebsiteMsg ->
                              assert(createEavWebsiteMsg.succeeded())
                              assertEquals("EAV website created successfully", createEavWebsiteMsg.result().body())

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
  }

  @Test
  fun testGetAllEavWebsiteBool(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteBool", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteBoolMsg ->
      assert(getAllEavWebsiteBoolMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteBool" to listOf(expectedBoolJson))
        },
        getAllEavWebsiteBoolMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteBoolByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteBoolByKey", boolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(expectedBoolJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsiteBool(vertx: Vertx, testContext: VertxTestContext) {
    val updatedBoolJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to boolJson.getString("attribute_key"),
        "value" to "0"
      )
    }

    val expectedUpdatedBoolJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to boolJson.getString("attribute_key"),
        "value" to false
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsiteBool", updatedBoolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website bool updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteBoolByKey", updatedBoolJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavWebsiteBoolMsg ->
        assert(getUpdatedEavWebsiteBoolMsg.succeeded())
        assertEquals(expectedUpdatedBoolJson, getUpdatedEavWebsiteBoolMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavWebsiteFloat(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteFloat", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteFloatMsg ->
      assert(getAllEavWebsiteFloatMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteFloat" to listOf(floatJson))
        },
        getAllEavWebsiteFloatMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteFloatByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteFloatByKey", floatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(floatJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsiteFloat(vertx: Vertx, testContext: VertxTestContext) {
    val updatedFloatJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to floatJson.getString("attribute_key"),
        "value" to 10.5
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsiteFloat", updatedFloatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website float updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteFloatByKey", updatedFloatJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavWebsiteFloatMsg ->
        assert(getUpdatedEavWebsiteFloatMsg.succeeded())
        assertEquals(updatedFloatJson, getUpdatedEavWebsiteFloatMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavWebsiteString(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteString", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteStringMsg ->
      assert(getAllEavWebsiteStringMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteString" to listOf(stringJson))
        },
        getAllEavWebsiteStringMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteStringByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteStringByKey", stringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(stringJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsiteString(vertx: Vertx, testContext: VertxTestContext) {
    val updatedStringJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to stringJson.getString("attribute_key"),
        "value" to "New Value"
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsiteString", updatedStringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website string updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteStringByKey", updatedStringJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavWebsiteStringMsg ->
        assert(getUpdatedEavWebsiteStringMsg.succeeded())
        assertEquals(updatedStringJson, getUpdatedEavWebsiteStringMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavWebsiteInt(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteInt", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteIntMsg ->
      assert(getAllEavWebsiteIntMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteInt" to listOf(intJson))
        },
        getAllEavWebsiteIntMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteIntByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteIntByKey", intJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(intJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsiteInt(vertx: Vertx, testContext: VertxTestContext) {
    val updatedIntJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to intJson.getString("attribute_key"),
        "value" to 20
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsiteInt", updatedIntJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website int updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteIntByKey", updatedIntJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavWebsiteIntMsg ->
        assert(getUpdatedEavWebsiteIntMsg.succeeded())
        assertEquals(updatedIntJson, getUpdatedEavWebsiteIntMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavWebsiteMoney(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteMoney", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteMoneyMsg ->
      assert(getAllEavWebsiteMoneyMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteMoney" to listOf(moneyJson))
        },
        getAllEavWebsiteMoneyMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteMoneyByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteMoneyByKey", moneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(moneyJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsiteMoney(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMoneyJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to moneyJson.getString("attribute_key"),
        "value" to 100.50
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsiteMoney", updatedMoneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website money updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteMoneyByKey", updatedMoneyJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavWebsiteMoneyMsg ->
        assert(getUpdatedEavWebsiteMoneyMsg.succeeded())
        assertEquals(updatedMoneyJson, getUpdatedEavWebsiteMoneyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavWebsiteMultiSelect(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteMultiSelect", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteMultiSelectMsg ->
      assert(getAllEavWebsiteMultiSelectMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteMultiSelect" to listOf(multiSelectJson))
        },
        getAllEavWebsiteMultiSelectMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteMultiSelectByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteMultiSelectByKey", multiSelectJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(multiSelectJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsiteMultiSelect(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMultiSelectJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to multiSelectJson.getString("attribute_key"),
        "value" to 0
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsiteMultiSelect", updatedMultiSelectJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website multi-select updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteMultiSelectByKey", updatedMultiSelectJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getUpdatedEavWebsiteMultiSelectMsg ->
          assert(getUpdatedEavWebsiteMultiSelectMsg.succeeded())
          assertEquals(updatedMultiSelectJson, getUpdatedEavWebsiteMultiSelectMsg.result().body())

          testContext.completeNow()
        }
    }
  }

  @Test
  fun testGetAllEavWebsite(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsite", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteMsg ->
      assert(getAllEavWebsiteMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsite" to listOf(eavJson))
        },
        getAllEavWebsiteMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteByKey", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteByKeyMsg ->
      assert(getEavWebsiteByKeyMsg.succeeded())
      assertEquals(eavJson, getEavWebsiteByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavWebsite(vertx: Vertx, testContext: VertxTestContext) {
    val updatedEavJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to eavJson.getString("attribute_key"),
      )
    }

    eventBus.request<String>("process.eavWebsite.updateEavWebsite", updatedEavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavWebsiteByKeyMsg ->
      assert(updateEavWebsiteByKeyMsg.succeeded())
      assertEquals("EAV website updated successfully", updateEavWebsiteByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteByKey", updatedEavJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavWebsiteMsg ->
        assert(getUpdatedEavWebsiteMsg.succeeded())
        assertEquals(updatedEavJson, getUpdatedEavWebsiteMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavWebsiteInfo(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getAllEavWebsiteInfo", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavWebsiteInfoMsg ->
      assert(getAllEavWebsiteInfoMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteInfo" to listOf(expectedFullEavJson))
        }, getAllEavWebsiteInfoMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavWebsiteInfoByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavWebsite.getEavWebsiteInfoByKey", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavWebsiteInfoByKeyMsg ->
      assert(getEavWebsiteInfoByKeyMsg.succeeded())
      assertEquals(
        json {
          obj("eavWebsiteInfo" to listOf(expectedFullEavJson))
        }, getEavWebsiteInfoByKeyMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.eavWebsite.deleteEavWebsite", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteEavWebsiteMsg ->
      assert(deleteEavWebsiteMsg.succeeded())
      assertEquals("EAV website deleted successfully", deleteEavWebsiteMsg.result().body())

      eventBus.request<String>("process.eavWebsite.deleteEavWebsiteMultiSelect", multiSelectJson).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteEavWebsiteMultiSelectMsg ->
        assert(deleteEavWebsiteMultiSelectMsg.succeeded())
        assertEquals(
          "EAV website multi-select deleted successfully",
          deleteEavWebsiteMultiSelectMsg.result().body()
        )

        eventBus.request<String>("process.eavWebsite.deleteEavWebsiteMoney", moneyJson).onFailure {
          testContext.failNow(it)
        }.onComplete { deleteEavWebsiteMoneyMsg ->
          assert(deleteEavWebsiteMoneyMsg.succeeded())
          assertEquals("EAV website money deleted successfully", deleteEavWebsiteMoneyMsg.result().body())

          eventBus.request<String>("process.eavWebsite.deleteEavWebsiteInt", intJson).onFailure {
            testContext.failNow(it)
          }.onComplete { deleteEavWebsiteIntMsg ->
            assert(deleteEavWebsiteIntMsg.succeeded())
            assertEquals("EAV website int deleted successfully", deleteEavWebsiteIntMsg.result().body())

            eventBus.request<String>("process.eavWebsite.deleteEavWebsiteString", stringJson).onFailure {
              testContext.failNow(it)
            }.onComplete { deleteEavWebsiteStringMsg ->
              assert(deleteEavWebsiteStringMsg.succeeded())
              assertEquals("EAV website string deleted successfully", deleteEavWebsiteStringMsg.result().body())

              eventBus.request<String>("process.eavWebsite.deleteEavWebsiteFloat", floatJson).onFailure {
                testContext.failNow(it)
              }.onComplete { deleteEavWebsiteFloatMsg ->
                assert(deleteEavWebsiteFloatMsg.succeeded())
                assertEquals("EAV website float deleted successfully", deleteEavWebsiteFloatMsg.result().body())

                eventBus.request<String>("process.eavWebsite.deleteEavWebsiteBool", boolJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { deleteEavWebsiteBoolMsg ->
                  assert(deleteEavWebsiteBoolMsg.succeeded())
                  assertEquals("EAV website bool deleted successfully", deleteEavWebsiteBoolMsg.result().body())

                  eventBus.request<String>(
                    "process.attributes.deleteCustomAttribute",
                    customProductAttributeJson.getString("attribute_key")
                  ).onFailure {
                    testContext.failNow(it)
                  }.onComplete { deleteAttributeMsg ->
                    assert(deleteAttributeMsg.succeeded())
                    assertEquals("Custom attribute deleted successfully", deleteAttributeMsg.result().body())

                      eventBus.request<String>("process.scope.deleteWebsite", websiteId).onFailure {
                        testContext.failNow(it)
                      }.onComplete { deleteWebsiteMsg ->
                        assert(deleteWebsiteMsg.succeeded())
                        assertEquals("Website deleted successfully", deleteWebsiteMsg.result().body())

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
  }

  private fun setAllJsonFields() {
    productJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "name" to "test name",
        "short_name" to "test short name",
        "description" to "test description",
        "short_description" to "test short description"
      )
    }

    websiteJson = json {
      obj(
        "website_id" to websiteId,
        "website_name" to "test website name"
      )
    }

    boolJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "value" to "1"
      )
    }

    expectedBoolJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "value" to true
      )
    }

    floatJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "value" to 1.0
      )
    }

    stringJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "value" to "test value"
      )
    }

    intJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "value" to 1
      )
    }

    moneyJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "value" to 10.0
      )
    }

    multiSelectJson = json {
      obj(
        "product_id" to productId,
        "website_id" to websiteId,
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
        "website_id" to websiteId,
        "attribute_key" to "test attribute key",
        "websiteBool" to true,
        "websiteFloat" to 1.0,
        "websiteString" to "test value",
        "websiteInt" to 1,
        "websiteMoney" to 10.0,
        "websiteMultiSelect" to 1
      )
    }
  }

  private fun deployVerticles(vertx: Vertx): MutableList<Future<Void>> {
    val verticleList: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

    verticleList.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductWebsiteEavJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )
    verticleList.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductCustomAttributesJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )
    verticleList.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )
    verticleList.add(
      deployWorkerVerticleHelper(
        vertx,
        ScopeJdbcVerticle::class.qualifiedName.toString(), 5, 5
      )
    )

    return verticleList
  }
}
