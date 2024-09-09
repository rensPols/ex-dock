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
    vertx.deployVerticle(ExtensionsLauncher())
      .onSuccess{ _ -> (
        println("MainVerticle started successfully")
        )}
     .onFailure { err ->
        println("Failed to start MainVerticle: $err")
        startPromise.fail(err)
      }
  }
}
