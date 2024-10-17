package com.ex_dock.ex_dock.backend

import com.ex_dock.ex_dock.backend.v1.router.enableBackendV1Router
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

fun Router.enableBackendRouter(vertx: Vertx) {
  val backendRouter: Router = Router.router(vertx)

  backendRouter.get("/about").handler { ctx ->
    ctx.end("about page for the APIs")
  }

  backendRouter.enableBackendV1Router(vertx)

  this.route("/api*").subRouter(backendRouter)
}