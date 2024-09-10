package com.ex_dock.ex_dock.frontend.text_pages.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx

fun Router.initTextPages(vertx: Vertx) {
  val textPagesRouter = Router.router(vertx)

  textPagesRouter.get("/").handler { ctx ->
    ctx.end("request to textPagesRouter successful")
  }

  this.route("/text-pages*").subRouter(textPagesRouter)
}
