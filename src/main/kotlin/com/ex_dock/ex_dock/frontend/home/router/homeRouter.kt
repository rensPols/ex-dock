package com.ex_dock.ex_dock.frontend.home.router

import io.vertx.ext.web.Router

fun Router.initHome() {
  this.get("/").handler { ctx ->
    ctx.end("You've successfully accessed the '/' path for the exDock server")
  }
}
