package com.ex_dock.ex_dock.database

import com.ex_dock.ex_dock.database.account.AccountJdbcVerticle
import com.ex_dock.ex_dock.database.category.CategoryJdbcVerticle
import com.ex_dock.ex_dock.database.checkout.CheckoutJdbcVerticle
import com.ex_dock.ex_dock.database.home.HomeJdbcVerticle
import com.ex_dock.ex_dock.database.product.ProductJdbcVerticle
import com.ex_dock.ex_dock.database.text_pages.Text_pagesJdbcVerticle
import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise

class JDBCStarter: AbstractVerticle() {

  private var verticles: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()

  private var verticleDeployHelper = VerticleDeployHelper()

  override fun start(starPromise: Promise<Void>) {
    addAllVerticles()

    Future.all(verticles)
      .onComplete {
        println("All JDBC verticles deployed")
      }
     .onFailure { error ->
        println("Failed to deploy JDBC verticles: $error")
      }
  }

  private fun addAllVerticles() {
    verticles.add(verticleDeployHelper
      .deployWorkerHelper(vertx, AccountJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(verticleDeployHelper
      .deployWorkerHelper(vertx, CategoryJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(verticleDeployHelper
      .deployWorkerHelper(vertx, CheckoutJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(verticleDeployHelper
      .deployWorkerHelper(vertx, HomeJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(verticleDeployHelper
      .deployWorkerHelper(vertx, ProductJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(verticleDeployHelper
      .deployWorkerHelper(vertx, Text_pagesJdbcVerticle::class.qualifiedName.toString(), 5, 5))
  }

}
