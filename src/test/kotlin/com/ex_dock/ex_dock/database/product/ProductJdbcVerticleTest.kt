package com.ex_dock.ex_dock.database.product

import com.ex_dock.ex_dock.helper.VerticleDeployHelper
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
class ProductJdbcVerticleTest {
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

  private var productSeoJson = json {
    obj(
      "product_id" to productId,
      "meta_title" to "test meta_title",
      "meta_description" to "test meta_description",
      "meta_keywords" to "test meta_keywords",
      "page_index" to "index, follow"
    )
  }

  private var productPricingJson = json {
    obj(
      "product_id" to productId,
      "price" to 10.0,
      "sale_price" to 10.0,
      "cost_price" to 4.0
    )
  }

  private var fullProductJson = json {
    obj(
      "product_id" to productId,
      "name" to "test name",
      "short_name" to "test short name",
      "description" to "test description",
      "short_description" to "test short description",
      "meta_title" to "test meta_title",
      "meta_description" to "test meta_description",
      "meta_keywords" to "test meta_keywords",
      "page_index" to "index, follow",
      "price" to 10.0,
      "sale_price" to 10.0,
      "cost_price" to 4.0
    )
  }

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    verticleDeployHelper.deployWorkerHelper(vertx,
      ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5).onComplete {
      eventBus.request<Int>("process.products.createProduct", productJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createProductMsg ->
        productId = createProductMsg.result().body()
        assertEquals("Int", createProductMsg.result().body()::class.simpleName)

        productJson = json {
          obj(
            "product_id" to productId,
            "name" to "test name",
            "short_name" to "test short name",
            "description" to "test description",
            "short_description" to "test short description"
          )
        }

        productSeoJson = json {
          obj(
            "product_id" to productId,
            "meta_title" to "test meta_title",
            "meta_description" to "test meta_description",
            "meta_keywords" to "test meta_keywords",
            "page_index" to "index, follow"
          )
        }

        productPricingJson = json {
          obj(
            "product_id" to productId,
            "price" to 10.0,
            "sale_price" to 10.0,
            "cost_price" to 4.0
          )
        }

        fullProductJson = json {
          obj(
            "product_id" to productId,
            "name" to "test name",
            "short_name" to "test short name",
            "description" to "test description",
            "short_description" to "test short description",
            "meta_title" to "test meta_title",
            "meta_description" to "test meta_description",
            "meta_keywords" to "test meta_keywords",
            "page_index" to "index, follow",
            "price" to 10.0,
            "sale_price" to 10.0,
            "cost_price" to 4.0
          )
        }

        eventBus.request<Int>("process.products.createProductSeo", productSeoJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createProductSeoMsg ->
          assertEquals("Product SEO created successfully", createProductSeoMsg.result().body())

          eventBus.request<Int>("process.products.createProductPricing", productPricingJson).onFailure {
            testContext.failNow(it)
          }.onComplete { createProductPricingMsg ->
            assertEquals("Product pricing created successfully", createProductPricingMsg.result().body())

            testContext.completeNow()
          }
        }
      }
    }
  }

  @Test
  fun testGetAllProducts(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getAllProducts", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllProductsMsg ->
      assert(getAllProductsMsg.succeeded())
      assertEquals(json { obj("products" to listOf(productJson)) }, getAllProductsMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetProductById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getProductById", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getProductByIdMsg ->
      assert(getProductByIdMsg.succeeded())
      assertEquals(productJson, getProductByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateProduct(vertx: Vertx, testContext: VertxTestContext) {
    val updatedProductJson = json {
      obj(
        "product_id" to productId,
        "name" to "updated test name",
        "short_name" to "updated test short name",
        "description" to "updated test description",
        "short_description" to "updated test short description"
      )
    }

    eventBus.request<JsonObject>("process.products.updateProduct", updatedProductJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateProductMsg ->
      assert(updateProductMsg.succeeded())
      assertEquals("Product updated successfully", updateProductMsg.result().body())

      eventBus.request<JsonObject>("process.products.getProductById", productId).onFailure {
        testContext.failNow(it)
      }.onComplete { updatedProductMsg ->
        assert(updatedProductMsg.succeeded())
        assertEquals(updatedProductJson, updatedProductMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllProductSeo(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getAllProductsSeo", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllProductSeoMsg ->
      assert(getAllProductSeoMsg.succeeded())
      assertEquals(json { obj("productSeo" to listOf(productSeoJson)) }, getAllProductSeoMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetProductSeoById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getProductSeoById", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getProductSeoByIdMsg ->
      assert(getProductSeoByIdMsg.succeeded())
      assertEquals(productSeoJson, getProductSeoByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateProductSeo(vertx: Vertx, testContext: VertxTestContext) {
    val updatedProductSeoJson = json {
      obj(
        "product_id" to productId,
        "meta_title" to "updated test meta_title",
        "meta_description" to "updated test meta_description",
        "meta_keywords" to "updated test meta_keywords",
        "page_index" to "index, follow"
      )
    }

    eventBus.request<JsonObject>("process.products.updateProductSeo", updatedProductSeoJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateProductSeoMsg ->
      assert(updateProductSeoMsg.succeeded())
      assertEquals("Product SEO updated successfully", updateProductSeoMsg.result().body())

      eventBus.request<JsonObject>("process.products.getProductSeoById", productId).onFailure {
        testContext.failNow(it)
      }.onComplete { updatedProductSeoMsg ->
        assert(updatedProductSeoMsg.succeeded())
        assertEquals(updatedProductSeoJson, updatedProductSeoMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllProductPricing(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getAllProductsPricing", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllProductPricingMsg ->
      assert(getAllProductPricingMsg.succeeded())
      assertEquals(json { obj("productsPricing" to listOf(productPricingJson)) }, getAllProductPricingMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetProductPricingById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getProductPricingById", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getProductPricingByIdMsg ->
      assert(getProductPricingByIdMsg.succeeded())
      assertEquals(productPricingJson, getProductPricingByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateProductPricing(vertx: Vertx, testContext: VertxTestContext) {
    val updatedProductPricingJson = json {
      obj(
        "product_id" to productId,
        "price" to 15.0,
        "sale_price" to 15.0,
        "cost_price" to 5.0
      )
    }

    eventBus.request<JsonObject>("process.products.updateProductPricing", updatedProductPricingJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateProductPricingMsg ->
      assert(updateProductPricingMsg.succeeded())
      assertEquals("Product pricing updated successfully", updateProductPricingMsg.result().body())

      eventBus.request<JsonObject>("process.products.getProductPricingById", productId).onFailure {
        testContext.failNow(it)
      }.onComplete { updatedProductPricingMsg ->
        assert(updatedProductPricingMsg.succeeded())
        assertEquals(updatedProductPricingJson, updatedProductPricingMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllFullProducts(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getAllFullProducts", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllFullProductsMsg ->
      assert(getAllFullProductsMsg.succeeded())
      assertEquals(json { obj("fullProducts" to listOf(fullProductJson)) }, getAllFullProductsMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetFullProductById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.products.getFullProductsById", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { getFullProductByIdMsg ->
      assert(getFullProductByIdMsg.succeeded())
      assertEquals(fullProductJson, getFullProductByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.products.deleteProductPricing", productId).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteProductPricingMsg ->
      assert(deleteProductPricingMsg.succeeded())
      assertEquals("Product pricing deleted successfully", deleteProductPricingMsg.result().body())

      eventBus.request<String>("process.products.deleteProductSeo", productId).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteProductSeoMsg ->
        assert(deleteProductSeoMsg.succeeded())
        assertEquals("Product SEO deleted successfully", deleteProductSeoMsg.result().body())

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
