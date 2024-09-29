package com.ex_dock.ex_dock.database.url

import com.ex_dock.ex_dock.database.category.CategoryJdbcVerticle
import com.ex_dock.ex_dock.database.product.ProductJdbcVerticle
import com.ex_dock.ex_dock.database.text_pages.TextPagesJdbcVerticle
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
class UrlJdbcVerticleTest {
  private lateinit var eventBus: EventBus

  private val verticleDeployHelper = VerticleDeployHelper()

  private var productId = -1

  private var textPageId = -1

  private var categoryId = -1

  private var urlJson = json {
    obj(
      "url_key" to "/",
      "upper_key" to "/",
      "page_type" to "product"
    )
  }

  private var textPageJson = json {
    obj(
      "text_pages_id" to textPageId,
      "name" to "test name",
      "short_text" to "test short_text",
      "text" to "test text"
    )
  }

  private var categoryJson = json {
    obj(
      "category_id" to categoryId,
      "upper_category" to null,
      "name" to "test name",
      "short_description" to "test description",
      "description" to "test description"
    )
  }

  private var productJson = json {
    obj(
      "product_id" to productId,
      "name" to "test name",
      "short_name" to "test name",
      "description" to "test description",
      "short_description" to "test description"
    )
  }

  private var textPageUrlJson = json {
    obj(
      "url_key" to "/",
      "upper_key" to "/",
      "text_pages_id" to textPageId
    )
  }

  private var productUrlJson = json {
    obj(
      "url_key" to "/",
      "upper_key" to "/",
      "product_id" to productId
    )
  }

  private var categoryUrlJson = json {
    obj(
      "url_key" to "/",
      "upper_key" to "/",
      "category_id" to categoryId
    )
  }

  private var fullUrlJson = json {
    obj(
      "url_key" to "/",
      "upper_key" to "/",
      "page_type" to "product",
      "text_pages_id" to textPageId,
      "text_page_name" to "test name",
      "text_page_short_text" to "test short_text",
      "text_page_text" to "test text",
      "category_id" to categoryId,
      "upper_category" to null,
      "category_name" to "test name",
      "category_short_description" to "test description",
      "category_description" to "test description",
      "product_id" to productId,
      "product_name" to "test name",
      "product_short_name" to "test name",
      "product_description" to "test description",
      "product_short_description" to "test description"
    )
  }

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    Future.all(deployNeededVerticles(vertx)).onComplete {
      eventBus.request<Int>("process.url.createUrlKey", urlJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createUrlMsg ->
        assertEquals("Url key successfully created", createUrlMsg.result().body())

        eventBus.request<Int>("process.textPages.create", textPageJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createTextPageMsg ->
          textPageId = createTextPageMsg.result().body()
          assertEquals("Int", createTextPageMsg.result().body()::class.simpleName)

          eventBus.request<Int>("process.categories.create", categoryJson).onFailure {
            testContext.failNow(it)
          }.onComplete { createCategoryMsg ->
            categoryId = createCategoryMsg.result().body()
            assertEquals("Int", createCategoryMsg.result().body()::class.simpleName)

            eventBus.request<Int>("process.products.createProduct", productJson).onFailure {
              testContext.failNow(it)
            }.onComplete {
              productId = createCategoryMsg.result().body()
              assertEquals("Int", createCategoryMsg.result().body()::class.simpleName)

              setAllJsonS()

              eventBus.request<String>("process.url.createTextPageUrl", textPageUrlJson).onFailure {
                testContext.failNow(it)
              }.onComplete { createTextPageUrlMsg ->
                assertEquals("Text page url created successfully", createTextPageUrlMsg.result().body())

                eventBus.request<String>("process.url.createProductUrl", productUrlJson).onFailure {
                  testContext.failNow(it)
                }.onComplete {  createProductUrlMsg ->
                  assertEquals("Product url created successfully", createProductUrlMsg.result().body())

                  eventBus.request<String>("process.url.createCategoryUrl", categoryUrlJson).onFailure {
                    testContext.failNow(it)
                  }.onComplete { createCategoryUrlMsg ->
                    assertEquals("Category url created successfully", createCategoryUrlMsg.result().body())

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
  @Test
  fun testGetAllUrlKeys(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getAllUrlKeys", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllUrlKeysMsg ->
      assert(getAllUrlKeysMsg.succeeded())
      assertEquals(json { obj("urlKeys" to listOf(urlJson)) }, getAllUrlKeysMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetUrlKeyByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getUrlByKey", urlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getUrlKeyByIdMsg ->
      assert(getUrlKeyByIdMsg.succeeded())
      assertEquals(urlJson, getUrlKeyByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun updateUrlKey(vertx: Vertx, testContext: VertxTestContext) {
    val updateUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "page_type" to "text_page"
      )
    }

    eventBus.request<String>("process.url.updateUrlKey", updateUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateUrlKeyMsg ->
      assert(updateUrlKeyMsg.succeeded())
      assertEquals("Url key updated successfully", updateUrlKeyMsg.result().body())

      eventBus.request<JsonObject>("process.url.getUrlByKey", updateUrlJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedUrlByKeyMsg ->
        assert(getUpdatedUrlByKeyMsg.succeeded())
        assertEquals(updateUrlJson, getUpdatedUrlByKeyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllTextPageUrls(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getAllTextPageUrls", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllTextPageUrlsMsg ->
      assert(getAllTextPageUrlsMsg.succeeded())
      assertEquals(json { obj("textPageUrls" to listOf(textPageUrlJson)) }, getAllTextPageUrlsMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun getTextPageUrlByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getTextPageUrlByKey", textPageUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getTextPageUrlByKeyMsg ->
      assert(getTextPageUrlByKeyMsg.succeeded())
      assertEquals(textPageUrlJson, getTextPageUrlByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun updateTextPageUrl(vertx: Vertx, testContext: VertxTestContext) {
    val updateTextPageUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "text_pages_id" to textPageId
      )
    }

    eventBus.request<String>("process.url.updateTextPageUrl", updateTextPageUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateTextPageUrlMsg ->
      assert(updateTextPageUrlMsg.succeeded())
      assertEquals("Text page url updated successfully", updateTextPageUrlMsg.result().body())

      eventBus.request<JsonObject>("process.url.getTextPageUrlByKey", updateTextPageUrlJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedTextPageUrlByKeyMsg ->
        assert(getUpdatedTextPageUrlByKeyMsg.succeeded())
        assertEquals(updateTextPageUrlJson, getUpdatedTextPageUrlByKeyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllProductUrls(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getAllProductUrls", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllProductUrlsMsg ->
      assert(getAllProductUrlsMsg.succeeded())
      assertEquals(json { obj("productUrls" to listOf(productUrlJson)) }, getAllProductUrlsMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun getProductUrlByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getProductUrlByKey", productUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getProductUrlByKeyMsg ->
      assert(getProductUrlByKeyMsg.succeeded())
      assertEquals(productUrlJson, getProductUrlByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun updateProductUrl(vertx: Vertx, testContext: VertxTestContext) {
    val updateProductUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "product_id" to productId
      )
    }

    eventBus.request<String>("process.url.updateProductUrl", updateProductUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateProductUrlMsg ->
      assert(updateProductUrlMsg.succeeded())
      assertEquals("Product url updated successfully", updateProductUrlMsg.result().body())

      eventBus.request<JsonObject>("process.url.getProductUrlByKey", updateProductUrlJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedProductUrlByKeyMsg ->
        assert(getUpdatedProductUrlByKeyMsg.succeeded())
        assertEquals(updateProductUrlJson, getUpdatedProductUrlByKeyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllCategoryUrls(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getAllCategoryUrls", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllCategoryUrlsMsg ->
      assert(getAllCategoryUrlsMsg.succeeded())
      assertEquals(json { obj("categoryUrls" to listOf(categoryUrlJson)) }, getAllCategoryUrlsMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun getCategoryUrlByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.url.getCategoryUrlByKey", categoryUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { getCategoryUrlByKeyMsg ->
      assert(getCategoryUrlByKeyMsg.succeeded())
      assertEquals(categoryUrlJson, getCategoryUrlByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun updateCategoryUrl(vertx: Vertx, testContext: VertxTestContext) {
    val updateCategoryUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "category_id" to categoryId
      )
    }

    eventBus.request<String>("process.url.updateCategoryUrl", updateCategoryUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateCategoryUrlMsg ->
      assert(updateCategoryUrlMsg.succeeded())
      assertEquals("Category url updated successfully", updateCategoryUrlMsg.result().body())

      eventBus.request<JsonObject>("process.url.getCategoryUrlByKey", updateCategoryUrlJson).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedCategoryUrlByKeyMsg ->
        assert(getUpdatedCategoryUrlByKeyMsg.succeeded())
        assertEquals(updateCategoryUrlJson, getUpdatedCategoryUrlByKeyMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllFullUrlsFullJoin(vertx: Vertx, testContext: VertxTestContext) {
    val joinList = json {
      obj(
        "joinTextPage" to true,
        "joinCategory" to true,
        "joinProduct" to true
      )
    }

    eventBus.request<JsonObject>("process.url.getAllFullUrls", joinList).onFailure {
      testContext.failNow(it)
    }.onComplete {  getAllFullJoinMsg ->
      assert(getAllFullJoinMsg.succeeded())
      assertEquals(json { obj("fullUrls" to listOf(fullUrlJson)) }, getAllFullJoinMsg.result().body())

      testContext.completeNow()

    }
  }

  @Test
  fun testGetFullUrlByKey(vertx: Vertx, testContext: VertxTestContext) {
    val joinList = json {
      obj(
        "joinTextPage" to true,
        "joinCategory" to true,
        "joinProduct" to true,
        "url_key" to "/",
        "upper_key" to "/"
      )
    }

    eventBus.request<JsonObject>("process.url.getFullUrlByKey", joinList).onFailure {
      testContext.failNow(it)
    }.onComplete { getFullUrlByKeyMsg ->
      assert(getFullUrlByKeyMsg.succeeded())
      assertEquals(fullUrlJson, getFullUrlByKeyMsg.result().body())

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.url.deleteCategoryUrl", categoryUrlJson).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteCategoryUrlMsg ->
      assert(deleteCategoryUrlMsg.succeeded())
      assertEquals("Category url deleted successfully", deleteCategoryUrlMsg.result().body())

      eventBus.request<String>("process.url.deleteProductUrl", productUrlJson).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteProductUrlMsg ->
        assert(deleteProductUrlMsg.succeeded())
        assertEquals("Product url deleted successfully", deleteProductUrlMsg.result().body())

        eventBus.request<String>("process.url.deleteTextPageUrl", textPageUrlJson).onFailure {
          testContext.failNow(it)
        }.onComplete { deleteTextPageUrlMsg ->
          assert(deleteTextPageUrlMsg.succeeded())
          assertEquals("Text page url deleted successfully", deleteTextPageUrlMsg.result().body())

          eventBus.request<String>("process.products.deleteProduct", productId).onFailure {
            testContext.failNow(it)
          }.onComplete { deleteProductMsg ->
            assert(deleteProductMsg.succeeded())
            assertEquals("Product deleted successfully", deleteProductMsg.result().body())

            eventBus.request<String>("process.categories.delete", categoryId).onFailure {
              testContext.failNow(it)
            }.onComplete { deleteCategoryMsg ->
              assert(deleteCategoryMsg.succeeded())
              assertEquals("Category deleted successfully!", deleteCategoryMsg.result().body())

              eventBus.request<String>("process.textPages.delete", textPageId).onFailure {
                testContext.failNow(it)
              }.onComplete { deleteTextPageMsg ->
                assert(deleteTextPageMsg.succeeded())
                assertEquals("Text page deleted successfully", deleteTextPageMsg.result().body())

                eventBus.request<String>("process.url.deleteUrlKey", urlJson).onFailure {
                  testContext.failNow(it)
                }.onComplete { deleteUrlKeyMsg ->
                  assert(deleteUrlKeyMsg.succeeded())
                  assertEquals("Url key deleted successfully", deleteUrlKeyMsg.result().body())

                  testContext.completeNow()
                }
              }
            }
          }
        }
      }
    }
  }

  private fun setAllJsonS() {
    textPageJson = json {
      obj(
        "text_pages_id" to textPageId,
        "name" to "test name",
        "short_text" to "test short_text",
        "text" to "test text"
      )
    }

    categoryJson =  json {
      obj(
        "category_id" to categoryId,
        "upper_category" to null,
        "name" to "test name",
        "short_description" to "test description",
        "description" to "test description"
      )
    }

    productJson = json {
      obj(
        "product_id" to productId,
        "name" to "test name",
        "short_name" to "test name",
        "description" to "test description",
        "short_description" to "test description"
      )
    }

    textPageUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "text_pages_id" to textPageId
      )
    }

    productUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "product_id" to productId
      )
    }

    categoryUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "category_id" to categoryId
      )
    }

    fullUrlJson = json {
      obj(
        "url_key" to "/",
        "upper_key" to "/",
        "page_type" to "product",
        "text_pages_id" to textPageId,
        "text_page_name" to "test name",
        "text_page_short_text" to "test short_text",
        "text_page_text" to "test text",
        "category_id" to categoryId,
        "upper_category" to null,
        "category_name" to "test name",
        "category_short_description" to "test description",
        "category_description" to "test description",
        "product_id" to productId,
        "product_name" to "test name",
        "product_short_name" to "test name",
        "product_description" to "test description",
        "product_short_description" to "test description"
      )
    }
  }

  private fun deployNeededVerticles(vertx: Vertx): MutableList<Future<Void>> {
    val verticleList: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      UrlJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))
    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      TextPagesJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))
    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      CategoryJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))
    verticleList.add(verticleDeployHelper.deployWorkerHelper(
      vertx,
      ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ))

    return verticleList
  }
}
