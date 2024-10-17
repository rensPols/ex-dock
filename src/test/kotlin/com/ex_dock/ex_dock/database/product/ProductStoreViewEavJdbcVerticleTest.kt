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
class ProductStoreViewEavJdbcVerticleTest {
  private lateinit var eventBus: EventBus

  private val verticleDeployHelper = VerticleDeployHelper()

  private var productId = -1

  private var storeViewId = -1

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

  private var storeViewJson = json {
    obj(
      "store_view_id" to storeViewId,
      "website_id" to websiteId,
      "store_view_name" to "test store view name"
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

          storeViewJson = json {
            obj(
              "store_view_id" to storeViewId,
              "website_id" to websiteId,
              "store_view_name" to "test store view name"
            )
          }

          eventBus.request<Int>("process.scope.createStoreView", storeViewJson).onFailure {
            testContext.failNow(it)
          }.onComplete { createStoreViewMsg ->
            storeViewId = createStoreViewMsg.result().body()
            assertEquals("Int", createStoreViewMsg.result().body()::class.simpleName)

            eventBus.request<String>("process.attributes.createCustomAttribute", customProductAttributeJson).onFailure {
              testContext.failNow(it)
            }.onComplete { createAttributeMsg ->
              assert(createAttributeMsg.succeeded())
              assertEquals("Custom attribute created successfully", createAttributeMsg.result().body())
              setAllJsonFields()

              eventBus.request<String>("process.eavStoreView.createEavStoreViewBool", boolJson).onFailure {
                testContext.failNow(it)
              }.onComplete { createEavStoreViewBoolMsg ->
                assert(createEavStoreViewBoolMsg.succeeded())
                assertEquals("EAV storeView bool created successfully", createEavStoreViewBoolMsg.result().body())

                eventBus.request<String>("process.eavStoreView.createEavStoreViewFloat", floatJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { createEavStoreViewFloatMsg ->
                  assert(createEavStoreViewFloatMsg.succeeded())
                  assertEquals("EAV storeView float created successfully", createEavStoreViewFloatMsg.result().body())

                  eventBus.request<String>("process.eavStoreView.createEavStoreViewString", stringJson).onFailure {
                    testContext.failNow(it)
                  }.onComplete { createEavStoreViewStringMsg ->
                    assert(createEavStoreViewStringMsg.succeeded())
                    assertEquals("EAV storeView string created successfully", createEavStoreViewStringMsg.result().body())

                    eventBus.request<String>("process.eavStoreView.createEavStoreViewInt", intJson).onFailure {
                      testContext.failNow(it)
                    }.onComplete { createEavStoreViewIntMsg ->
                      assert(createEavStoreViewIntMsg.succeeded())
                      assertEquals("EAV storeView int created successfully", createEavStoreViewIntMsg.result().body())

                      eventBus.request<String>("process.eavStoreView.createEavStoreViewMoney", moneyJson).onFailure {
                        testContext.failNow(it)
                      }.onComplete { createEavStoreViewMoneyMsg ->
                        assert(createEavStoreViewMoneyMsg.succeeded())
                        assertEquals(
                          "EAV storeView money created successfully",
                          createEavStoreViewMoneyMsg.result().body()
                        )

                        eventBus.request<String>("process.eavStoreView.createEavStoreViewMultiSelect", multiSelectJson)
                          .onFailure {
                            testContext.failNow(it)
                          }.onComplete { createEavStoreViewMultiSelectMsg ->
                            assert(createEavStoreViewMultiSelectMsg.succeeded())
                            assertEquals(
                              "EAV storeView multi-select created successfully",
                              createEavStoreViewMultiSelectMsg.result().body()
                            )

                            eventBus.request<String>("process.eavStoreView.createEavStoreView", eavJson).onFailure {
                              testContext.failNow(it)
                            }.onComplete { createEavStoreViewMsg ->
                              assert(createEavStoreViewMsg.succeeded())
                              assertEquals("EAV storeView created successfully", createEavStoreViewMsg.result().body())

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
  }

  @Test
  fun testGetAllEavStoreViewBool(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewBool", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewBoolMsg ->
      assert(getAllEavStoreViewBoolMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewBool" to listOf(expectedBoolJson))
        },
        getAllEavStoreViewBoolMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewBoolByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewBoolByKey", boolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(expectedBoolJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreViewBool(vertx: Vertx, testContext: VertxTestContext) {
    val updatedBoolJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to boolJson.getString("attribute_key"),
        "value" to "0"
      )
    }

    val expectedUpdatedBoolJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to boolJson.getString("attribute_key"),
        "value" to false
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreViewBool", updatedBoolJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView bool updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewBoolByKey", updatedBoolJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavStoreViewBoolMsg ->
        assert(getUpdatedEavStoreViewBoolMsg.succeeded())
        assertEquals(expectedUpdatedBoolJson, getUpdatedEavStoreViewBoolMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreViewFloat(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewFloat", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewFloatMsg ->
      assert(getAllEavStoreViewFloatMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewFloat" to listOf(floatJson))
        },
        getAllEavStoreViewFloatMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewFloatByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewFloatByKey", floatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(floatJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreViewFloat(vertx: Vertx, testContext: VertxTestContext) {
    val updatedFloatJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to floatJson.getString("attribute_key"),
        "value" to 10.5
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreViewFloat", updatedFloatJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView float updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewFloatByKey", updatedFloatJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavStoreViewFloatMsg ->
        assert(getUpdatedEavStoreViewFloatMsg.succeeded())
        assertEquals(updatedFloatJson, getUpdatedEavStoreViewFloatMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreViewString(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewString", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewStringMsg ->
      assert(getAllEavStoreViewStringMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewString" to listOf(stringJson))
        },
        getAllEavStoreViewStringMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewStringByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewStringByKey", stringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(stringJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreViewString(vertx: Vertx, testContext: VertxTestContext) {
    val updatedStringJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to stringJson.getString("attribute_key"),
        "value" to "New Value"
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreViewString", updatedStringJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView string updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewStringByKey", updatedStringJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavStoreViewStringMsg ->
        assert(getUpdatedEavStoreViewStringMsg.succeeded())
        assertEquals(updatedStringJson, getUpdatedEavStoreViewStringMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreViewInt(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewInt", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewIntMsg ->
      assert(getAllEavStoreViewIntMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewInt" to listOf(intJson))
        },
        getAllEavStoreViewIntMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewIntByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewIntByKey", intJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(intJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreViewInt(vertx: Vertx, testContext: VertxTestContext) {
    val updatedIntJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to intJson.getString("attribute_key"),
        "value" to 20
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreViewInt", updatedIntJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView int updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewIntByKey", updatedIntJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavStoreViewIntMsg ->
        assert(getUpdatedEavStoreViewIntMsg.succeeded())
        assertEquals(updatedIntJson, getUpdatedEavStoreViewIntMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreViewMoney(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewMoney", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewMoneyMsg ->
      assert(getAllEavStoreViewMoneyMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewMoney" to listOf(moneyJson))
        },
        getAllEavStoreViewMoneyMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewMoneyByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewMoneyByKey", moneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(moneyJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreViewMoney(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMoneyJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to moneyJson.getString("attribute_key"),
        "value" to 100.50
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreViewMoney", updatedMoneyJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView money updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewMoneyByKey", updatedMoneyJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavStoreViewMoneyMsg ->
        assert(getUpdatedEavStoreViewMoneyMsg.succeeded())
        assertEquals(updatedMoneyJson, getUpdatedEavStoreViewMoneyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreViewMultiSelect(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewMultiSelect", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewMultiSelectMsg ->
      assert(getAllEavStoreViewMultiSelectMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewMultiSelect" to listOf(multiSelectJson))
        },
        getAllEavStoreViewMultiSelectMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewMultiSelectByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewMultiSelectByKey", multiSelectJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(multiSelectJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreViewMultiSelect(vertx: Vertx, testContext: VertxTestContext) {
    val updatedMultiSelectJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to multiSelectJson.getString("attribute_key"),
        "value" to 0
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreViewMultiSelect", updatedMultiSelectJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView multi-select updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewMultiSelectByKey", updatedMultiSelectJson)
        .onFailure {
          testContext.failNow(it)
        }.onComplete { getUpdatedEavStoreViewMultiSelectMsg ->
        assert(getUpdatedEavStoreViewMultiSelectMsg.succeeded())
        assertEquals(updatedMultiSelectJson, getUpdatedEavStoreViewMultiSelectMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreView(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreView", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewMsg ->
      assert(getAllEavStoreViewMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreView" to listOf(eavJson))
        },
        getAllEavStoreViewMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewByKey", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewByKeyMsg ->
      assert(getEavStoreViewByKeyMsg.succeeded())
      assertEquals(eavJson, getEavStoreViewByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateEavStoreView(vertx: Vertx, testContext: VertxTestContext) {
    val updatedEavJson = json {
      obj(
        "product_id" to productId,
        "attribute_key" to eavJson.getString("attribute_key"),
      )
    }

    eventBus.request<String>("process.eavStoreView.updateEavStoreView", updatedEavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateEavStoreViewByKeyMsg ->
      assert(updateEavStoreViewByKeyMsg.succeeded())
      assertEquals("EAV storeView updated successfully", updateEavStoreViewByKeyMsg.result().body())

      eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewByKey", updatedEavJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedEavStoreViewMsg ->
        assert(getUpdatedEavStoreViewMsg.succeeded())
        assertEquals(updatedEavJson, getUpdatedEavStoreViewMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllEavStoreViewInfo(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getAllEavStoreViewInfo", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllEavStoreViewInfoMsg ->
      assert(getAllEavStoreViewInfoMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewInfo" to listOf(expectedFullEavJson))
        }, getAllEavStoreViewInfoMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @Test
  fun testGetEavStoreViewInfoByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.eavStoreView.getEavStoreViewInfoByKey", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getEavStoreViewInfoByKeyMsg ->
      assert(getEavStoreViewInfoByKeyMsg.succeeded())
      assertEquals(
        json {
          obj("eavStoreViewInfo" to listOf(expectedFullEavJson))
        }, getEavStoreViewInfoByKeyMsg.result().body()
      )

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.eavStoreView.deleteEavStoreView", eavJson).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteEavStoreViewMsg ->
      assert(deleteEavStoreViewMsg.succeeded())
      assertEquals("EAV storeView deleted successfully", deleteEavStoreViewMsg.result().body())

      eventBus.request<String>("process.eavStoreView.deleteEavStoreViewMultiSelect", multiSelectJson).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteEavStoreViewMultiSelectMsg ->
        assert(deleteEavStoreViewMultiSelectMsg.succeeded())
        assertEquals(
          "EAV storeView multi-select deleted successfully",
          deleteEavStoreViewMultiSelectMsg.result().body()
        )

        eventBus.request<String>("process.eavStoreView.deleteEavStoreViewMoney", moneyJson).onFailure {
          testContext.failNow(it)
        }.onComplete { deleteEavStoreViewMoneyMsg ->
          assert(deleteEavStoreViewMoneyMsg.succeeded())
          assertEquals("EAV storeView money deleted successfully", deleteEavStoreViewMoneyMsg.result().body())

          eventBus.request<String>("process.eavStoreView.deleteEavStoreViewInt", intJson).onFailure {
            testContext.failNow(it)
          }.onComplete { deleteEavStoreViewIntMsg ->
            assert(deleteEavStoreViewIntMsg.succeeded())
            assertEquals("EAV storeView int deleted successfully", deleteEavStoreViewIntMsg.result().body())

            eventBus.request<String>("process.eavStoreView.deleteEavStoreViewString", stringJson).onFailure {
              testContext.failNow(it)
            }.onComplete { deleteEavStoreViewStringMsg ->
              assert(deleteEavStoreViewStringMsg.succeeded())
              assertEquals("EAV storeView string deleted successfully", deleteEavStoreViewStringMsg.result().body())

              eventBus.request<String>("process.eavStoreView.deleteEavStoreViewFloat", floatJson).onFailure {
                testContext.failNow(it)
              }.onComplete { deleteEavStoreViewFloatMsg ->
                assert(deleteEavStoreViewFloatMsg.succeeded())
                assertEquals("EAV storeView float deleted successfully", deleteEavStoreViewFloatMsg.result().body())

                eventBus.request<String>("process.eavStoreView.deleteEavStoreViewBool", boolJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { deleteEavStoreViewBoolMsg ->
                  assert(deleteEavStoreViewBoolMsg.succeeded())
                  assertEquals("EAV storeView bool deleted successfully", deleteEavStoreViewBoolMsg.result().body())

                      eventBus.request<String>(
                        "process.attributes.deleteCustomAttribute",
                        customProductAttributeJson.getString("attribute_key")
                      ).onFailure {
                        testContext.failNow(it)
                      }.onComplete { deleteAttributeMsg ->
                        assert(deleteAttributeMsg.succeeded())
                        assertEquals("Custom attribute deleted successfully", deleteAttributeMsg.result().body())

                        eventBus.request<String>("process.scope.deleteStoreView", storeViewId).onFailure {
                          testContext.failNow(it)
                        }.onComplete { deleteStoreViewMsg ->
                          assert(deleteStoreViewMsg.succeeded())
                          assertEquals("Store view deleted successfully", deleteStoreViewMsg.result().body())

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
  }

  private fun setAllJsonFields() {
    productJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
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

    storeViewJson = json {
      obj(
        "store_view_id" to storeViewId,
        "website_id" to websiteId,
        "store_view_name" to "test store view name"
      )
    }

    boolJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "value" to "1"
      )
    }

    expectedBoolJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "value" to true
      )
    }

    floatJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "value" to 1.0
      )
    }

    stringJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "value" to "test value"
      )
    }

    intJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "value" to 1
      )
    }

    moneyJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "value" to 10.0
      )
    }

    multiSelectJson = json {
      obj(
        "product_id" to productId,
        "store_view_id" to storeViewId,
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
        "store_view_id" to storeViewId,
        "attribute_key" to "test attribute key",
        "storeViewBool" to true,
        "storeViewFloat" to 1.0,
        "storeViewString" to "test value",
        "storeViewInt" to 1,
        "storeViewMoney" to 10.0,
        "storeViewMultiSelect" to 1
      )
    }
  }

  private fun deployVerticles(vertx: Vertx): MutableList<Future<Void>> {
    val verticleList: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

    verticleList.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductStoreViewEavJdbcVerticle::class.qualifiedName.toString(), 5, 5
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
