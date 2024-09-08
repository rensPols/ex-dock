package com.ex_dock.ex_dock

import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.ext.web.client.WebClient

class ExtensionLauncher: AbstractVerticle() {

  private var extension: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

  private val verticleDeployHelper: VerticleDeployHelper = VerticleDeployHelper()

  override fun start(startPromise: Promise<Void>) {
    checkExtensions()

    Future.all(extension)
      .onComplete { _ ->
        println("All extensions started successfully")
        startPromise.complete()
      }
     .onFailure { startPromise.fail(it) }
  }

  private fun checkExtensions() {
    val client = WebClient.create(vertx)

    // Check for JDBC extension
    client.get(8889, "localhost", "/activate")
     .send()
     .onSuccess { response ->
       if (response.bodyAsString().contains("Activated!")) {
         extension.add(verticleDeployHelper.deployHelper(vertx, JDBCVerticle::class.qualifiedName.toString()))
       } else {
         println("Something else is running on port 8889")
       }
     }
     .onFailure {
       println("Failed to check for JDBC extension: ${it.message}")
     }
  }
}
