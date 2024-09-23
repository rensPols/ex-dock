package com.ex_dock.ex_dock.database.server

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
class ServerJDBCVerticleTest {
  private lateinit var eventBus: EventBus
  private val verticleDeployHelper = VerticleDeployHelper()

  private var serverDataJson = json {
    obj(
      "key" to "server_data_key",
      "value" to "server_data_value"
    )
  }

  private var serverVersionJson = json {
   obj(
      "major" to 1,
      "minor" to 0,
      "patch" to 0,
      "version_name" to "1.0.0",
      "version_description" to "Initial release of the server"
    )
  }
  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
    verticleDeployHelper.deployWorkerHelper(vertx,
      ServerJDBCVerticle::class.qualifiedName.toString(), 5, 5).onComplete {
        eventBus.request<String>("process.server.createServerData", serverDataJson).onFailure {
          testContext.failNow(it)
        }.onComplete {createServerDataMsg ->
          assert(createServerDataMsg.succeeded())
          assertEquals(createServerDataMsg.result().body(), "Server data created successfully!")

          eventBus.request<String>("process.server.createServerVersion", serverVersionJson).onFailure {
            testContext.failNow(it)
          }.onComplete { createServerVersionMsg ->
            assert(createServerVersionMsg.succeeded())
            assertEquals(createServerVersionMsg.result().body(), "Server version created successfully!")

            testContext.completeNow()
          }
        }
    }
  }

  @Test
  fun getAllServerData(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.server.getAllServerData", "").onFailure {
      testContext.failNow(it)
    }.onComplete {
      assert(it.succeeded())
      assertEquals(it.result().body(), json { obj( "serverData" to listOf(serverDataJson)) })
      testContext.completeNow()
    }
  }

  @Test
  fun getServerDataByKey(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.server.getServerByKey", serverDataJson.getString("key")).onFailure {
      testContext.failNow(it)
    }.onComplete {
      assert(it.succeeded())
      assertEquals(it.result().body(), json { obj( "serverData" to listOf(serverDataJson)) })
      testContext.completeNow()
    }
  }

  @Test
  fun updateServerData(vertx: Vertx, testContext: VertxTestContext) {
    val updatedServerDataJson = json {
      obj(
        "key" to serverDataJson.getString("key"),
        "value" to "updated_server_data_value"
      )
    }

    eventBus.request<String>("process.server.updateServerData", updatedServerDataJson).onFailure {
      testContext.failNow(it)
    }.onComplete { updateServerMsg ->
      assert(updateServerMsg.succeeded())
      assertEquals(updateServerMsg.result().body(), "Server data updated successfully!")

      eventBus.request<JsonObject>("process.server.getServerByKey", updatedServerDataJson.getString("key")).onFailure {
        testContext.failNow(it)
      }.onComplete {
        assert(it.succeeded())
        assertEquals(it.result().body(), json { obj( "serverData" to listOf(updatedServerDataJson)) })
        testContext.completeNow()
      }
    }
  }

  @Test
  fun getAllServerVersions(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<JsonObject>("process.server.getAllServerVersions", "").onFailure {
      testContext.failNow(it)
    }.onComplete {getAllServerVersionsMsg ->
      assert(getAllServerVersionsMsg.succeeded())
      assertEquals(getAllServerVersionsMsg.result().body(), json { obj( "serverVersions" to listOf(serverVersionJson)) })
      testContext.completeNow()
    }
  }

  @AfterEach
  fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
    eventBus.request<String>("process.server.deleteServerData", serverDataJson.getString("key")).onFailure {
      testContext.failNow(it)
    }.onComplete {
      assert(it.succeeded())
      assertEquals(it.result().body(), "Server data deleted successfully!")
      eventBus.request<String>("process.server.deleteServerVersion", serverVersionJson).onFailure {
        testContext.failNow(it)
      }.onComplete { deleteServerVersionMsg ->
        assert(deleteServerVersionMsg.succeeded())
        assertEquals(deleteServerVersionMsg.result().body(), "Server version deleted successfully!")

        testContext.completeNow()
      }
    }
  }
}
