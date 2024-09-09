package com.ex_dock.ex_dock.frontend

import com.ex_dock.ex_dock.frontend.account.AccountFrontendVerticle
import com.ex_dock.ex_dock.frontend.category.CategoryFrontendVerticle
import com.ex_dock.ex_dock.frontend.checkout.CheckoutFrontendVerticle
import com.ex_dock.ex_dock.frontend.home.HomeFrontendVerticle
import com.ex_dock.ex_dock.frontend.product.ProductFrontendVerticle
import com.ex_dock.ex_dock.frontend.text_pages.TextPagesFrontendVerticle
import com.ex_dock.ex_dock.helper.VerticleDeployHelper
import io.vertx.core.AbstractVerticle

class FrontendStarter: AbstractVerticle() {
  override fun start() {
    VerticleDeployHelper.deployHelper(vertx, HomeFrontendVerticle::class.qualifiedName.toString())
    VerticleDeployHelper.deployHelper(vertx, ProductFrontendVerticle::class.qualifiedName.toString())
    VerticleDeployHelper.deployHelper(vertx, CategoryFrontendVerticle::class.qualifiedName.toString())
    VerticleDeployHelper.deployHelper(vertx, CheckoutFrontendVerticle::class.qualifiedName.toString())
    VerticleDeployHelper.deployHelper(vertx, AccountFrontendVerticle::class.qualifiedName.toString())
    VerticleDeployHelper.deployHelper(vertx, TextPagesFrontendVerticle::class.qualifiedName.toString())
  }
}
