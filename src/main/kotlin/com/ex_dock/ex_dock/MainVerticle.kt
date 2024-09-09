package com.ex_dock.ex_dock

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import java.util.Properties

class MainVerticle : AbstractVerticle() {

  val props : Properties = javaClass.classLoader.getResourceAsStream("secret.properties").use {
    Properties().apply { load(it) }
  }

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

    val mainRouter : Router = Router.router(vertx)

    vertx
      .createHttpServer()
      .requestHandler(mainRouter)
      .listen(props.getProperty("FRONTEND_PORT").toInt()) {http ->
        if (http.succeeded()) {
          println("HTTP server started on port ${props.getProperty("FRONTEND_PORT")}")
          startPromise.complete()
        } else {
          println("Failed to start HTTP server: ${http.cause()}")
          startPromise.fail(http.cause())
        }
      }
  }
}
