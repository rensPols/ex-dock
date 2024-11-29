package com.ex_dock.ex_dock.frontend.product

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus

class ProductFrontendVerticle: AbstractVerticle() {

  private lateinit var eventBus: EventBus

  override fun start() {
    eventBus = vertx.eventBus()

    getProductHomePage()
  }

  private fun getProductHomePage() {
    eventBus.consumer<String>("frontend.retrieveHTML.productHome") { message ->
      eventBus.request<String>("template.generate.compiled", "productHome").onFailure {
        // TODO: handle error
      }.onSuccess { res ->
        message.reply(res.body())
      }
    }
  }
}
