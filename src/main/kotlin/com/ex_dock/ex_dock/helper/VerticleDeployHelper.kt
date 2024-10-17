package com.ex_dock.ex_dock.helper

import io.vertx.core.*

class VerticleDeployHelper {
  companion object {

    @Deprecated("Function is moved outside of class", ReplaceWith("deployVerticleHelper()"))
    fun deployHelper(vertx: Vertx, name: String): Future<Void> {
      return deployVerticleHelper(vertx, name)
    }
  }

  @Deprecated("Function is moved outside of class", ReplaceWith("deployWorkerVerticleHelper()"))
  fun deployWorkerHelper(vertx: Vertx, name: String, workerPoolSize: Int, instances: Int): Future<Void> {
    return deployWorkerVerticleHelper(vertx, name, workerPoolSize, instances)
  }

  @Deprecated("Function is moved outside of class", ReplaceWith("deployVirtualVerticleHelper()"))
  fun deployVirtualVerticle(vertx: Vertx, name: String): Future<Void> {
    return deployVirtualVerticleHelper(vertx, name)
  }
}


fun deployVerticleHelper(vertx: Vertx, name: String): Future<Void> {
  val promise: Promise<Void> = Promise.promise<Void>()
  vertx.deployVerticle(name)
    .onComplete{ res ->
      if (res.failed()) {
        println(buildString {
          append("\u001b[31m")
          append("⨯ - Failed to deploy Verticle: $name\nCause: ${res.cause()}")
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


fun deployWorkerVerticleHelper(vertx: Vertx, name: String, workerPoolSize: Int, instances: Int): Future<Void> {
  val promise: Promise<Void> = Promise.promise<Void>()
  val options: DeploymentOptions = DeploymentOptions()
    .setThreadingModel(ThreadingModel.WORKER)
    .setWorkerPoolName(name)
    .setWorkerPoolSize(workerPoolSize)
    .setInstances(instances)

  vertx.deployVerticle(name, options)
    .onComplete{ res ->
      if (res.failed()) {
        println(buildString {
          append("\u001b[31m")
          append("⨯ - Failed to worker Verticle: $name")
          append("\u001b[0m")
        })
        promise.fail(res.cause())
      } else {
        println(buildString {
          append("\u001b[32m")
          append("✓ - $name worker Verticle was successfully deployed")
          append("\u001b[0m")
        })
        promise.complete()
      }
    }

  return promise.future()
}


fun deployVirtualVerticleHelper(vertx: Vertx, name: String): Future<Void> {
  val promise: Promise<Void> = Promise.promise<Void>()
  val options: DeploymentOptions = DeploymentOptions().setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
  vertx.deployVerticle(name, options)
    .onComplete{ res ->
      if (res.failed()) {
        println(buildString {
          append("\u001b[31m")
          append("⨯ - Failed to virtual Verticle: $name")
          append("\u001b[0m")
        })
        promise.fail(res.cause())
      } else {
        println(buildString {
          append("\u001b[32m")
          append("✓ - $name virtual Verticle was successfully deployed")
          append("\u001b[0m")
        })
        promise.complete()
      }
    }

  return promise.future()
}
