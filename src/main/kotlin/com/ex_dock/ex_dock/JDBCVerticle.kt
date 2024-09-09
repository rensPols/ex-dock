package com.ex_dock.ex_dock

import io.vertx.core.AbstractVerticle

class JDBCVerticle: AbstractVerticle() {

  override fun start() {
    vertx.createHttpServer()
      .requestHandler { req ->
        req.response()
         .putHeader("content-type", "text/plain")
         .end("Hello from JDBC Vert.x!")
      }
      .listen(8888)
      .onComplete { http ->
        if (http.succeeded()) {
          println("HTTP server started on port 8888")
        } else {
          println("Failed to start HTTP server: ${http.cause()}")
        }
      }
    }
  }
