package com.ex_dock.ex_dock.frontend.home

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus

class HomeFrontendVerticle: AbstractVerticle() {

  private lateinit var eventBus: EventBus

  override fun start() {
    eventBus = vertx.eventBus()

    getHomePage()
  }

  private fun getHomePage() {
    val getHomeConsumer = eventBus.consumer<Any?>("frontend.retrieveHTML.home")
    getHomeConsumer.handler { message ->
      val data = getHomeData()
      val template = retrieveHomeTemplate()
      eventBus.request<String>("template.generate.compiled", "home").onFailure {
        // TODO: implement
      }.onSuccess { response ->
        message.reply(response)
      }
    }
  }

  private fun retrieveHomeTemplate(): String {
    // TODO: get template from templates table with key "home"
    return """
      <!DOCTYPE html>
      <html>
        <head>
          <title>exDock Home Page</title>
        </head>
        <body>
          <h1>Welcome to exDock!</h1>
          <p>This is the home page of the exDock server.</p>
        </body>
      </html>
    """.trimIndent();
  }

  private fun getHomeData(): Map<String, Any> {
    // TODO: get data needed for the home page
    return mapOf()
  }
}
