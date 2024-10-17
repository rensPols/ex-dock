package com.ex_dock.ex_dock.frontend.template_engine

import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateDataCodec
import io.vertx.core.eventbus.EventBus
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.*

@ExtendWith(VertxExtension::class)
class TemplateEngineVerticleTest {
  @Test
  fun testSingleUseTemplate(vertx: Vertx, testContext: VertxTestContext) {
    val eventBus: EventBus = vertx.eventBus()
    val checkpoint = testContext.checkpoint()

    val singleUseTemplateData: SingleUseTemplateData = SingleUseTemplateData(
      "<test>{{ name }}</test>",
      mapOf("name" to "testName")
    )

    val expectedResult: String = "<test>testName</test>"

    vertx.deployVerticle(TemplateEngineVerticle(), testContext.succeeding {
      eventBus.request<String>("template.generate.singleUse", singleUseTemplateData, DeliveryOptions().setCodecName(
        SingleUseTemplateDataCodec().name())).onFailure {
        testContext.failNow(it)
      }.onComplete { message ->
        assert(message.succeeded())
        assertEquals(
          message.result().body()::class.simpleName,
          "String",
          "Received class isn't String")

        val result: String = message.result().body()
        assertEquals(result, expectedResult, "Output isn't equal to the expected output")

        // Mark as successful
        checkpoint.flag()
      }
    })
  }
}