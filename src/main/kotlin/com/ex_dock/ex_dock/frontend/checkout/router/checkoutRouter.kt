package com.ex_dock.ex_dock.frontend.checkout.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx

fun Router.initCheckout(vertx: Vertx) {
  val checkoutRouter = Router.router(vertx)

  checkoutRouter.get("/").handler { ctx ->
    ctx.end("request to checkoutRouter successful")
  }

  this.route("/checkout*").subRouter(checkoutRouter)
}
