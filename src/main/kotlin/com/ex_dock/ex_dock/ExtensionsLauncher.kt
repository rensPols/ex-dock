package com.ex_dock.ex_dock

import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.ext.web.client.WebClient
import java.util.Properties

class ExtensionsLauncher: AbstractVerticle() {

  private var extension: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

  private val verticleDeployHelper: VerticleDeployHelper = VerticleDeployHelper()

  private lateinit var props: Properties

  override fun start(startPromise: Promise<Void>) {
    props = javaClass.classLoader.getResourceAsStream("secret.properties").use {
      Properties().apply { load(it) }
    }
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

    //ADD JDBC Vertex
    extension.add(verticleDeployHelper.deployHelper(vertx, JDBCVerticle::class.qualifiedName.toString()))
  }
}
