package com.ex_dock.ex_dock.backend.v1.router

import com.ex_dock.ex_dock.backend.apiMountingPath
import io.vertx.core.Vertx
import io.vertx.ext.web.Router


fun Router.enableBackendV1Router(vertx: Vertx, absoluteMounting: Boolean = false) {
  val backendV1Router: Router = Router.router(vertx)

  // TODO: routing

  this.route(
    if (absoluteMounting) "$apiMountingPath/v1*" else "/v1*"
  ).subRouter(backendV1Router)
}
