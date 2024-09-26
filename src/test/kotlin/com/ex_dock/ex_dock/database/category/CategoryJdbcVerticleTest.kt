package com.ex_dock.ex_dock.database.category

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
class CategoryJdbcVerticleTest {
  private lateinit var eventBus: EventBus

  private val verticleDeployHelper = VerticleDeployHelper()

  private var categoryId = -1

  private var categoryJson = json {
    obj(
      "category_id" to categoryId,
      "upper_category" to null,
      "name" to "test name",
      "short_description" to "test description",
      "description" to "test description"
    )
  }

  private lateinit var fullCategoryJson: JsonObject

  private lateinit var categorySeo: JsonObject
  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    verticleDeployHelper.deployWorkerHelper(
      vertx,
      CategoryJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ).onFailure {
      testContext.failNow(it)
    }.onComplete {
      eventBus.request<Int>("process.categories.create", categoryJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createCategoryMsg ->
        assert(createCategoryMsg.succeeded())
        categoryId = createCategoryMsg.result().body()
        assertEquals(createCategoryMsg.result().body()::class.simpleName, "Int")

        categoryJson = json {
          obj(
            "category_id" to categoryId,
            "upper_category" to null,
            "name" to "test name",
            "short_description" to "test description",
            "description" to "test description"
          )
        }

        categorySeo = json {
          obj(
            "category_id" to categoryId,
            "meta_title" to "test meta_title",
            "meta_description" to "test meta_description",
            "meta_keywords" to "test meta_keywords",
            "page_index" to "index, follow"
          )
        }

        fullCategoryJson = json {
          obj(
            "category_id" to categoryId,
            "upper_category" to null,
            "name" to "test name",
            "short_description" to "test description",
            "description" to "test description",
            "meta_title" to "test meta_title",
            "meta_description" to "test meta_description",
            "meta_keywords" to "test meta_keywords",
            "page_index" to "index, follow"
          )
        }

        eventBus.request<String>("process.categories.createSeoCategory", categorySeo).onFailure {
          testContext.failNow(it)
        }.onComplete { createSeoCategoryMsg ->
          assert(createSeoCategoryMsg.succeeded())
          assertEquals("SEO category created successfully!", createSeoCategoryMsg.result().body())

          testContext.completeNow()
        }
      }
    }
  }

  @Test
  fun testGetAllCategories(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.categories.getAll", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllCategoriesMsg ->
      assert(getAllCategoriesMsg.succeeded())
      assertEquals(json {
        obj("categories" to listOf(categoryJson))
      }, getAllCategoriesMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetCategoryById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.categories.getById", categoryId).onFailure {
      testContext.failNow(it)
    }.onComplete { getCategoryByIdMsg ->
      assert(getCategoryByIdMsg.succeeded())
      assertEquals(categoryJson, getCategoryByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateCategory(vertx: Vertx, testContext: VertxTestContext) {
    val updatedCategoryJson = json {
      obj(
        "category_id" to categoryId,
        "upper_category" to null,
        "name" to "updated test name",
        "short_description" to "updated test description",
        "description" to "updated test description"
      )
    }

    eventBus.request<String>("process.categories.edit", updatedCategoryJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateCategoryMsg ->
      assert(updateCategoryMsg.succeeded())
      assertEquals("Category updated successfully!", updateCategoryMsg.result().body())

      eventBus.request<JsonObject>("process.categories.getById", categoryId).onFailure {
        testContext.failNow(it)
      }.onComplete { getCategoryMsg ->
        assert(getCategoryMsg.succeeded())
        assertEquals(updatedCategoryJson, getCategoryMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun getAllSeoCategories(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.categories.getAllSeo", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllSeoCategoriesMsg ->
      assert(getAllSeoCategoriesMsg.succeeded())
      assertEquals(json {
        obj("seoCategories" to listOf(categorySeo))
      }, getAllSeoCategoriesMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetSeoCategoryById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.categories.getSeoById", categoryId).onFailure {
      testContext.failNow(it)
    }.onComplete { getSeoCategoryByIdMsg ->
      assert(getSeoCategoryByIdMsg.succeeded())
      assertEquals(categorySeo, getSeoCategoryByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateSeoCategory(vertx: Vertx, testContext: VertxTestContext) {
    val updatedCategorySeo = json {
      obj(
        "category_id" to categoryId,
        "meta_title" to "updated test meta_title",
        "meta_description" to "updated test meta_description",
        "meta_keywords" to "updated test meta_keywords",
        "page_index" to "index, nofollow"
      )
    }

    eventBus.request<String>("process.categories.editSeoCategory", updatedCategorySeo).onFailure {
      testContext.failNow(it)
    }.onComplete { updateSeoCategoryMsg ->
      assert(updateSeoCategoryMsg.succeeded())
      assertEquals("SEO category updated successfully!", updateSeoCategoryMsg.result().body())

      eventBus.request<JsonObject>("process.categories.getSeoById", categoryId).onFailure {
        testContext.failNow(it)
      }.onComplete { getSeoCategoryMsg ->
        assert(getSeoCategoryMsg.succeeded())
        assertEquals(updatedCategorySeo, getSeoCategoryMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetFullCategoryInfo(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.categories.getAllFullInfo", categoryId).onFailure {
      testContext.failNow(it)
    }.onComplete { getFullCategoryInfoMsg ->
      assert(getFullCategoryInfoMsg.succeeded())
      assertEquals(json {
        obj( "categories" to listOf(fullCategoryJson))
      }, getFullCategoryInfoMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetFullCategoryInfoById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.categories.getFullInfoById", categoryId).onFailure {
      testContext.failNow(it)
    }.onComplete { getFullCategoryByIdMsg ->
      assert(getFullCategoryByIdMsg.succeeded())
      assertEquals(fullCategoryJson, getFullCategoryByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.categories.deleteSeoCategory", categoryId).onFailure {
      testContext.failNow(it)
    }.onComplete { deleteSeoCategoryMsg ->
      assert(deleteSeoCategoryMsg.succeeded())
      assertEquals("SEO category deleted successfully!", deleteSeoCategoryMsg.result().body())

      eventBus.request<String>("process.categories.delete", categoryId).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteCategoryMsg ->
        assert(deleteCategoryMsg.succeeded())
        assertEquals("Category deleted successfully!", deleteCategoryMsg.result().body())
        testContext.completeNow()
      }
    }
  }
}
