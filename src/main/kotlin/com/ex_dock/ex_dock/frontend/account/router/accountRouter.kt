package com.ex_dock.ex_dock.frontend.account.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx

fun Router.initAccount(vertx: Vertx) {
  val accountRouter = Router.router(vertx)

  accountRouter.get("/").handler { ctx ->
    ctx.end("request to accountRouter successful")
  }

  this.route("/account*").subRouter(accountRouter)
}
