package com.ex_dock.ex_dock.frontend.product.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus

fun Router.initProduct(vertx: Vertx) {
  val productRouter = Router.router(vertx)
  val eventBus: EventBus = vertx.eventBus()

  productRouter.get("/").handler { ctx ->
    eventBus.request<Any>("process.products.getAll", "")
      .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving products: ${error.localizedMessage}")
      }
  }

  this.route("/product*").subRouter(productRouter)
}
