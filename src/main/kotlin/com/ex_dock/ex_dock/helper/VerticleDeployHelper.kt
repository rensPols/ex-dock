package com.ex_dock.ex_dock.helper

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx

class VerticleDeployHelper {
  companion object {
    fun deployHelper(vertx: Vertx, name: String): Future<Void> {
      val promise: Promise<Void> = Promise.promise<Void>()
      vertx.deployVerticle(name)
        .onComplete{ res ->
          if (res.failed()) {
            println(buildString {
              append("\u001b[31m")
              append("⨯ - Failed to deploy Verticle: $name")
              append("\u001b[0m")
            })
            promise.fail(res.cause())
          } else {
            println(buildString {
              append("\u001b[32m")
              append("✓ - $name Verticle was successfully deployed")
              append("\u001b[0m")
            })
            promise.complete()
          }
        }

      return promise.future()
    }
  }
}
