package com.ex_dock.ex_dock.frontend.product.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx

fun Router.initProduct(vertx: Vertx) {
  val productRouter = Router.router(vertx)

  productRouter.get("/").handler { ctx ->
    ctx.end("request to productRouter successful")
  }

  this.route("/product*").subRouter(productRouter)
}
