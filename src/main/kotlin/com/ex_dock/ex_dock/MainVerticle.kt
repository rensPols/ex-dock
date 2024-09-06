package com.ex_dock.ex_dock

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class MainVerticle : AbstractVerticle() {

  /**
 * This function is the entry point for the Vert.x application. It starts an HTTP server and listens on port 8888.
 *
 * @param startPromise A [Promise] that will be completed when the HTTP server has started successfully or failed to start.
 *
 * @return Nothing is returned from this function.
 */
override fun start(startPromise: Promise<Void>) {
    vertx
      .createHttpServer()
      .requestHandler { req ->
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!")
      }
      .listen(8888).onComplete { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause())
        }
      }
  }
}
