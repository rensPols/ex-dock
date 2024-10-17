package com.ex_dock.ex_dock.frontend

import com.ex_dock.ex_dock.frontend.account.AccountFrontendVerticle
import com.ex_dock.ex_dock.frontend.category.CategoryFrontendVerticle
import com.ex_dock.ex_dock.frontend.checkout.CheckoutFrontendVerticle
import com.ex_dock.ex_dock.frontend.home.HomeFrontendVerticle
import com.ex_dock.ex_dock.frontend.product.ProductFrontendVerticle
import com.ex_dock.ex_dock.frontend.text_pages.TextPagesFrontendVerticle
import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import com.ex_dock.ex_dock.helper.deployVerticleHelper
import io.vertx.core.AbstractVerticle
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.delay

class FrontendVerticle: AbstractVerticle() {
  override fun start() {
    deployVerticleHelper(vertx, HomeFrontendVerticle::class.qualifiedName.toString())
    deployVerticleHelper(vertx, ProductFrontendVerticle::class.qualifiedName.toString())
    deployVerticleHelper(vertx, CategoryFrontendVerticle::class.qualifiedName.toString())
    deployVerticleHelper(vertx, CheckoutFrontendVerticle::class.qualifiedName.toString())
    deployVerticleHelper(vertx, AccountFrontendVerticle::class.qualifiedName.toString())
    deployVerticleHelper(vertx, TextPagesFrontendVerticle::class.qualifiedName.toString())
  }
}
