package com.ex_dock.ex_dock.helper

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx

class VerticleDeployHelper {

  fun deployHelper(vertx: Vertx, name: String): Future<Void> {
    val promise: Promise<Void> = Promise.promise<Void>()
    vertx.deployVerticle(name)
      .onComplete{ res ->
        if (res.failed()) {
          println("Failed to deploy $name Verticle!")
          promise.fail(res.cause())
        } else {
          println("$name Verticle deployed successfully!")
          promise.complete()
        }
      }

    return promise.future()
  }
}
