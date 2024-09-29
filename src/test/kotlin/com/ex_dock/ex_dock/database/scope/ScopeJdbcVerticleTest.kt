package com.ex_dock.ex_dock.database.scope

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
class ScopeJdbcVerticleTest {
  private lateinit var eventBus: EventBus
  private var storeViewId = -1
  private var websiteId = -1
  private val verticleDeployHelper = VerticleDeployHelper()

  private var websiteJson = json {
    obj(
      "website_id" to websiteId,
      "website_name" to "test name"
    )
  }

  private var storeViewJson = json {
    obj(
      "store_view_id" to storeViewId,
      "website_id" to websiteId,
      "store_view_name" to "test store view name"
    )
  }

  private var fullScopeJson = json {
    obj(
      "store_view_id" to storeViewId,
      "store_view_name" to "test store view name",
      "website_id" to websiteId,
      "website_name" to "test name"
    )
  }

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    verticleDeployHelper.deployWorkerHelper(vertx,
      ScopeJdbcVerticle::class.qualifiedName.toString(), 5, 5).onComplete {
        eventBus.request<Int>("process.scope.createWebsite", websiteJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createWebsiteMsg ->
          assert(createWebsiteMsg.succeeded())
          websiteId = createWebsiteMsg.result().body()

          websiteJson = json {
            obj(
              "website_id" to websiteId,
              "website_name" to "test name"
            )
          }

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
            assert(createStoreViewMsg.succeeded())
            storeViewId = createStoreViewMsg.result().body()

            storeViewJson = json {
              obj(
                "store_view_id" to storeViewId,
                "website_id" to websiteId,
                "store_view_name" to "test store view name"
              )
            }

            fullScopeJson = json {
              obj(
                "store_view_id" to storeViewId,
                "store_view_name" to "test store view name",
                "website_id" to websiteId,
                "website_name" to "test name"
              )
            }

            testContext.completeNow()
          }
        }
    }
  }
  @Test
  fun testGetAllWebsites(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.scope.getAllWebsites", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllWebsitesMsg ->
      assert(getAllWebsitesMsg.succeeded())
      assertEquals(json { obj("websites" to listOf(websiteJson))
      }, getAllWebsitesMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetWebsiteById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.scope.getWebsiteById", websiteId).onFailure {
      testContext.failNow(it)
    }.onComplete { getWebsiteByIdMsg ->
      assert(getWebsiteByIdMsg.succeeded())
      assertEquals(websiteJson, getWebsiteByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateWebsite(vertx: Vertx, testContext: VertxTestContext) {
    val updatedWebsiteJson = json {
      obj(
        "website_id" to websiteId,
        "website_name" to "updated test name"
      )
    }

    eventBus.request<JsonObject>("process.scope.editWebsite", updatedWebsiteJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateWebsiteMsg ->
      assert(updateWebsiteMsg.succeeded())
      assertEquals("Website updated successfully", updateWebsiteMsg.result().body())

      eventBus.request<JsonObject>("process.scope.getWebsiteById", websiteId).onFailure {
        testContext.failNow(it)
      }.onComplete { getWebsiteByIdMsg ->
        assert(getWebsiteByIdMsg.succeeded())
        assertEquals(updatedWebsiteJson, getWebsiteByIdMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllStoreViews(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.scope.getAllStoreViews", websiteId).onFailure {
      testContext.failNow(it)
    }.onComplete { getAllStoreViewsMsg ->
      assert(getAllStoreViewsMsg.succeeded())
      assertEquals(json { obj("store_views" to listOf(storeViewJson))
      }, getAllStoreViewsMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetStoreViewById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.scope.getStoreViewById", storeViewId).onFailure {
      testContext.failNow(it)
    }.onComplete { getStoreViewByIdMsg ->
      assert(getStoreViewByIdMsg.succeeded())
      assertEquals(storeViewJson, getStoreViewByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testUpdateStoreView(vertx: Vertx, testContext: VertxTestContext) {
    val updatedStoreViewJson = json {
      obj(
        "store_view_id" to storeViewId,
        "website_id" to websiteId,
        "store_view_name" to "updated test store view name"
      )
    }

    eventBus.request<JsonObject>("process.scope.editStoreView", updatedStoreViewJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateStoreViewMsg ->
      assert(updateStoreViewMsg.succeeded())
      assertEquals("Store view updated successfully", updateStoreViewMsg.result().body())

      eventBus.request<JsonObject>("process.scope.getStoreViewById", storeViewId).onFailure {
        testContext.failNow(it)
      }.onComplete { getStoreViewByIdMsg ->
        assert(getStoreViewByIdMsg.succeeded())
        assertEquals(updatedStoreViewJson, getStoreViewByIdMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllFullScopes(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.scope.getAllScopes", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getAllFullScopesMsg ->
      assert(getAllFullScopesMsg.succeeded())
      assertEquals(json { obj("scopes" to listOf(fullScopeJson))
      }, getAllFullScopesMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetFullScopeById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.scope.getScopeById", storeViewId).onFailure {
      testContext.failNow(it)
    }.onComplete { getFullScopeByIdMsg ->
      assert(getFullScopeByIdMsg.succeeded())
      assertEquals(fullScopeJson, getFullScopeByIdMsg.result().body())

      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.scope.deleteStoreView", storeViewId).onFailure {
      testContext.failNow(it)
    }.onComplete { storeViewMsg ->
      assert(storeViewMsg.succeeded())
      assertEquals("Store view deleted successfully", storeViewMsg.result().body())

      eventBus.request<String>("process.scope.deleteWebsite", websiteId).onFailure {
        testContext.failNow(it)
      }.onComplete { websiteMsg ->
        assert(websiteMsg.succeeded())
        assertEquals("Website deleted successfully", websiteMsg.result().body())

        testContext.completeNow()
      }
    }
  }
}
