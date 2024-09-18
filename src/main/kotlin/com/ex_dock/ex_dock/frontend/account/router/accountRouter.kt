package com.ex_dock.ex_dock.frontend.account.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.Json

fun Router.initAccount(vertx: Vertx) {
  val accountRouter = Router.router(vertx)
  val eventBus: EventBus = vertx.eventBus()

  accountRouter.get("/").handler { ctx ->
    eventBus.request<Any>("process.account.getData", "testUser")
      .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }
      .onFailure { error ->
        ctx.end("Error retrieving account data: ${error.localizedMessage}")
      }
  }

  this.route("/account*").subRouter(accountRouter)
}
