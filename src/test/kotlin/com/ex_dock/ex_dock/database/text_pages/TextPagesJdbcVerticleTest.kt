package com.ex_dock.ex_dock.database.text_pages

import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TextPagesJdbcVerticleTest {
    private lateinit var eventBus: EventBus
    private var textPageId = -1

    private var textPageJson = json {
      obj(
        "text_pages_id" to textPageId,
        "name" to "test name",
        "short_text" to "test short_text",
        "text" to "test text"
      )
    }

  private var textPageSeoJson = json {
    obj(
      "text_pages_id" to textPageId,
      "meta_title" to "test meta_title",
      "meta_description" to "test meta_description",
      "meta_keywords" to "test meta_keywords",
      "page_index" to "index, follow"
    )
  }

  private lateinit var fullTextPageJson: JsonObject

    @BeforeEach
    fun setUp(vertx: Vertx, testContext: VertxTestContext) {
      eventBus = vertx.eventBus()
      deployWorkerVerticleHelper(vertx,
        TextPagesJdbcVerticle::class.qualifiedName.toString(), 5, 5).onComplete {

      eventBus.request<Int>("process.textPages.create", textPageJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createPageMsg ->
        assert(createPageMsg.succeeded())
        textPageId = createPageMsg.result().body()

        textPageJson = json {
          obj(
            "text_pages_id" to textPageId,
            "name" to "test name",
            "short_text" to "test short_text",
            "text" to "test text"
          )
        }

        textPageSeoJson = json {
          obj(
            "text_pages_id" to textPageId,
            "meta_title" to "test meta_title",
            "meta_description" to "test meta_description",
            "meta_keywords" to "test meta_keywords",
            "page_index" to "index, follow"
          )
        }

        fullTextPageJson = json {
          obj(
            "text_pages_id" to textPageId,
            "name" to "test name",
            "short_text" to "test short_text",
            "text" to "test text",
            "meta_title" to "test meta_title",
            "meta_description" to "test meta_description",
            "meta_keywords" to "test meta_keywords",
            "page_index" to "index, follow"
          )
        }

        eventBus.request<Int>("process.textPages.createSeoTextPage", textPageSeoJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createSeoMsg ->
          assert(createSeoMsg.succeeded())

          testContext.completeNow()
        }
      }
      }
    }

    @Test
    fun testGetAllPages(vertx: Vertx, testContext: VertxTestContext) {
      eventBus.request<JsonObject>("process.textPages.getAll", "").onFailure {
        testContext.failNow(it)
      }.onComplete {getAllPagesMsg ->
        assert(getAllPagesMsg.succeeded())
        assertEquals(json {
          obj("textPages" to listOf(textPageJson))
        }, getAllPagesMsg.result().body())

        testContext.completeNow()
      }
    }

  @Test
  fun testGetPageById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.textPages.getById", textPageId).onFailure {
        testContext.failNow(it)
      }.onComplete {getPageByIdMsg ->
        assert(getPageByIdMsg.succeeded())
        assertEquals(textPageJson, getPageByIdMsg.result().body())

        testContext.completeNow()
      }
    }

  @Test
  fun testUpdatePage(vertx: Vertx, testContext: VertxTestContext) {
    val updatedTextPageJson = json {
      obj(
        "text_pages_id" to textPageId,
        "name" to "updated name",
        "short_text" to "updated short_text",
        "text" to "updated text"
      )
    }

    eventBus.request<JsonObject>("process.textPages.update", updatedTextPageJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updatePageMsg ->
      assert(updatePageMsg.succeeded())

      eventBus.request<JsonObject>("process.textPages.getById", textPageId).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedPageMsg ->
        assert(getUpdatedPageMsg.succeeded())
        assertEquals(updatedTextPageJson, getUpdatedPageMsg.result().body())

        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetAllSeoTextPages(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.textPages.getAllSeoTextPages", "").onFailure {
        testContext.failNow(it)
      }.onComplete { getAllSeoPagesMsg ->
        assert(getAllSeoPagesMsg.succeeded())
        assertEquals(json {
          obj("seoTextPages" to listOf(textPageSeoJson))
        }, getAllSeoPagesMsg.result().body())

        testContext.completeNow()
      }

  }

  @Test
  fun testGetSeoPageById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.textPages.getSeoTextPageBySeoTextPageId", textPageId).onFailure {
        testContext.failNow(it)
      }.onComplete { getSeoPageByIdMsg ->
        assert(getSeoPageByIdMsg.succeeded())
        assertEquals(textPageSeoJson, getSeoPageByIdMsg.result().body())

        testContext.completeNow()
      }

  }

  @Test
  fun testUpdateSeoPage(vertx: Vertx, testContext: VertxTestContext) {
    val updatedSeoTextPageJson = json {
      obj(
        "text_pages_id" to textPageId,
        "meta_title" to "updated meta_title",
        "meta_description" to "updated meta_description",
        "meta_keywords" to "updated meta_keywords",
        "page_index" to "index, follow"
      )
    }

    eventBus.request<JsonObject>("process.textPages.updateSeoTextPage", updatedSeoTextPageJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateSeoPageMsg ->
      assert(updateSeoPageMsg.succeeded())

      eventBus.request<JsonObject>("process.textPages.getSeoTextPageBySeoTextPageId", textPageId).onFailure {
        testContext.failNow(it)
      }.onComplete { getUpdatedSeoPageMsg ->
        assert(getUpdatedSeoPageMsg.succeeded())
        assertEquals(updatedSeoTextPageJson, getUpdatedSeoPageMsg.result().body())
        testContext.completeNow()
      }
    }
  }

  @Test
  fun testGetFullTextPages(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.textPages.getAllFullTextPages", "").onFailure {
      testContext.failNow(it)
    }.onComplete { getFullTextPagesMsg ->
      assert(getFullTextPagesMsg.succeeded())
      assertEquals(json {
        obj("fullTextPages" to listOf(fullTextPageJson))
      }, getFullTextPagesMsg.result().body())

      testContext.completeNow()
    }
  }

  @Test
  fun testGetFullTextPageById(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.textPages.getFullTextPageByFullTextPageId", textPageId).onFailure {
      testContext.failNow(it)
    }.onComplete { getFullTextPageByIdMsg ->
      assert(getFullTextPageByIdMsg.succeeded())
      assertEquals(fullTextPageJson, getFullTextPageByIdMsg.result().body())

      testContext.completeNow()
    }
  }

    @AfterEach
    fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
      eventBus.request<String>("process.textPages.deleteSeoTextPage", textPageId).onFailure {
        testContext.failNow(it)
      }.onComplete { deletePageMsg ->
        assert(deletePageMsg.succeeded())

        eventBus.request<String>("process.textPages.delete", textPageId).onFailure {
          testContext.failNow(it)
        }.onComplete { deleteSeoMsg ->
          assert(deleteSeoMsg.succeeded())

          testContext.completeNow()
        }
      }
    }
}
