package com.ex_dock.ex_dock.frontend.cache

import com.ex_dock.ex_dock.database.JDBCStarter
import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class CacheVerticleTest {
  private val hitCount = 10000
  private val requestedData = "accounts;categories"

  // This method should not be in the GitHub test, but only be done for manual testing purposes
  // with a populated database
  @Test
  fun testCache(vertx: Vertx, testContext: VertxTestContext) {
    deployWorkerVerticleHelper(vertx, JDBCStarter::class.qualifiedName.toString(), 1, 1).onComplete {
      println("STARTING TESTS")
      val eventBus = vertx.eventBus()
      val amount: Int = 1_000_000
      val futures: Array<Future<Any>?> = arrayOfNulls(amount)

      val mark1 = timeSource.markNow()
      for (i in 0 until amount) {
        futures[i] = eventBus.request<CacheData>("process.cache.requestData", requestedData)
          .map { it.body() } // Convert Future<Message<CacheData>> to Future<CacheData>, then to Future<Any>
      }

      Future.all(futures.toMutableList()).onFailure { err ->
        testContext.failNow(err)
      }.onSuccess {
        val mark2 = timeSource.markNow()
        println(mark2 - mark1)
        testContext.completeNow()
      }
    }
  }
}
