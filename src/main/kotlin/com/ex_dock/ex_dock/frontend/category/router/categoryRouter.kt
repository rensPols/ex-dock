package com.ex_dock.ex_dock.frontend.category.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx

fun Router.initCategory(vertx: Vertx) {
  val categoryRouter = Router.router(vertx)

  categoryRouter.get("/").handler { ctx ->
    ctx.end("request to categoryRouter successful")
  }

  this.route("/category*").subRouter(categoryRouter)
}
