package com.ex_dock.ex_dock.frontend.product.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj

fun Router.initProduct(vertx: Vertx) {
  val productRouter = Router.router(vertx)
  val eventBus: EventBus = vertx.eventBus()

  productRouter["/"].handler { ctx ->
    eventBus.request<String>("frontend.retrieveHTML.productHome", "").onFailure {
      ctx.end("You've successfully accessed the '/product' path for the exDock server, " +
        "but the product page failed to load")
    }.onSuccess {
      ctx.end(it.body())
    }
  }

  this.route("/product*").subRouter(productRouter)
}
