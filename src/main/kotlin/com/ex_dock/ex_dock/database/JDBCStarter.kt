package com.ex_dock.ex_dock.database

import com.ex_dock.ex_dock.database.account.AccountJdbcVerticle
import com.ex_dock.ex_dock.database.category.CategoryJdbcVerticle
import com.ex_dock.ex_dock.database.checkout.CheckoutJdbcVerticle
import com.ex_dock.ex_dock.database.home.HomeJdbcVerticle
import com.ex_dock.ex_dock.database.product.*
import com.ex_dock.ex_dock.database.scope.ScopeJdbcVerticle
import com.ex_dock.ex_dock.database.server.ServerJDBCVerticle
import com.ex_dock.ex_dock.database.text_pages.TextPagesJdbcVerticle
import com.ex_dock.ex_dock.database.url.UrlJdbcVerticle
import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise

class JDBCStarter: AbstractVerticle() {

  private var verticles: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

  override fun start(starPromise: Promise<Void>) {
    addAllVerticles()

    Future.all(verticles)
      .onComplete {
        println("All JDBC verticles deployed")
        TODO("Add all codecs to the eventbus")
      }
     .onFailure { error ->
        println("Failed to deploy JDBC verticles: $error")
      }
  }

  private fun addAllVerticles() {
    verticles.add(deployWorkerVerticleHelper(vertx, AccountJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, CategoryJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, CheckoutJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, HomeJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, TextPagesJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ScopeJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ServerJDBCVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, UrlJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductCompleteEavJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductGlobalEavJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductMultiSelectJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductStoreViewEavJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductWebsiteEavJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ProductCustomAttributesJdbcVerticle::class.qualifiedName.toString(), 5, 5))
  }

}
