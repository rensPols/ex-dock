package com.ex_dock.ex_dock.database

import com.ex_dock.ex_dock.database.account.*
import com.ex_dock.ex_dock.database.category.*
import com.ex_dock.ex_dock.database.checkout.CheckoutJdbcVerticle
import com.ex_dock.ex_dock.database.codec.GenericCodec
import com.ex_dock.ex_dock.database.home.HomeJdbcVerticle
import com.ex_dock.ex_dock.database.product.*
import com.ex_dock.ex_dock.database.scope.FullScope
import com.ex_dock.ex_dock.database.scope.ScopeJdbcVerticle
import com.ex_dock.ex_dock.database.scope.StoreView
import com.ex_dock.ex_dock.database.scope.Websites
import com.ex_dock.ex_dock.database.server.ServerDataData
import com.ex_dock.ex_dock.database.server.ServerJDBCVerticle
import com.ex_dock.ex_dock.database.server.ServerVersionData
import com.ex_dock.ex_dock.database.service.ServiceVerticle
import com.ex_dock.ex_dock.database.template.Block
import com.ex_dock.ex_dock.database.template.Template
import com.ex_dock.ex_dock.database.template.TemplateJdbcVerticle
import com.ex_dock.ex_dock.database.text_pages.FullTextPages
import com.ex_dock.ex_dock.database.text_pages.TextPages
import com.ex_dock.ex_dock.database.text_pages.TextPagesJdbcVerticle
import com.ex_dock.ex_dock.database.text_pages.TextPagesSeo
import com.ex_dock.ex_dock.database.url.*
import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.eventbus.EventBus

class JDBCStarter : AbstractVerticle() {

  private var verticles: MutableList<Future<Void>> = emptyList<Future<Void>>().toMutableList()
  private lateinit var eventBus: EventBus

  override fun start(starPromise: Promise<Void>) {
    addAllVerticles()

    Future.all(verticles)
      .onComplete {
        println("All JDBC verticles deployed")
        getAllCodecClasses()
        eventBus = vertx.eventBus()

        eventBus.request<String>("process.service.populateTemplates", "").onFailure {
          // TODO: handle error
        }.onSuccess {
          println("Database populated with standard Data")
        }
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
    verticles.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductCompleteEavJdbcVerticle::class.qualifiedName.toString(),
        5,
        5
      )
    )
    verticles.add(deployWorkerVerticleHelper(vertx, ProductGlobalEavJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductMultiSelectJdbcVerticle::class.qualifiedName.toString(),
        5,
        5
      )
    )
    verticles.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductStoreViewEavJdbcVerticle::class.qualifiedName.toString(),
        5,
        5
      )
    )
    verticles.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductWebsiteEavJdbcVerticle::class.qualifiedName.toString(),
        5,
        5
      )
    )
    verticles.add(
      deployWorkerVerticleHelper(
        vertx,
        ProductCustomAttributesJdbcVerticle::class.qualifiedName.toString(),
        5,
        5
      )
    )
    verticles.add(deployWorkerVerticleHelper(vertx, TemplateJdbcVerticle::class.qualifiedName.toString(), 5, 5))
    verticles.add(deployWorkerVerticleHelper(vertx, ServiceVerticle::class.qualifiedName.toString(), 1, 1))
  }

  private fun getAllCodecClasses() {
    vertx.eventBus()
      .registerCodec(GenericCodec(MutableList::class.java))
      .registerCodec(GenericCodec(Categories::class.java))
      .registerCodec(GenericCodec(CategoriesSeo::class.java))
      .registerCodec(GenericCodec(CategoriesProducts::class.java))
      .registerCodec(GenericCodec(FullCategoryInfo::class.java))
      .registerCodec(GenericCodec(CustomProductAttributes::class.java))
      .registerCodec(GenericCodec(Products::class.java))
      .registerCodec(GenericCodec(EavGlobalBool::class.java))
      .registerCodec(GenericCodec(EavGlobalFloat::class.java))
      .registerCodec(GenericCodec(EavGlobalInt::class.java))
      .registerCodec(GenericCodec(EavGlobalMoney::class.java))
      .registerCodec(GenericCodec(EavGlobalMultiSelect::class.java))
      .registerCodec(GenericCodec(EavGlobalString::class.java))
      .registerCodec(GenericCodec(Eav::class.java))
      .registerCodec(GenericCodec(EavGlobalInfo::class.java))
      .registerCodec(GenericCodec(ProductsSeo::class.java))
      .registerCodec(GenericCodec(ProductsPricing::class.java))
      .registerCodec(GenericCodec(FullProduct::class.java))
      .registerCodec(GenericCodec(MultiSelectBool::class.java))
      .registerCodec(GenericCodec(MultiSelectFloat::class.java))
      .registerCodec(GenericCodec(MultiSelectString::class.java))
      .registerCodec(GenericCodec(MultiSelectInt::class.java))
      .registerCodec(GenericCodec(MultiSelectMoney::class.java))
      .registerCodec(GenericCodec(MultiSelectInfo::class.java))
      .registerCodec(GenericCodec(Websites::class.java))
      .registerCodec(GenericCodec(StoreView::class.java))
      .registerCodec(GenericCodec(EavStoreViewBool::class.java))
      .registerCodec(GenericCodec(EavStoreViewFloat::class.java))
      .registerCodec(GenericCodec(EavStoreViewString::class.java))
      .registerCodec(GenericCodec(EavStoreViewInt::class.java))
      .registerCodec(GenericCodec(EavStoreViewMoney::class.java))
      .registerCodec(GenericCodec(EavStoreViewInfo::class.java))
      .registerCodec(GenericCodec(EavStoreViewMultiSelect::class.java))
      .registerCodec(GenericCodec(EavWebsiteBool::class.java))
      .registerCodec(GenericCodec(EavWebsiteFloat::class.java))
      .registerCodec(GenericCodec(EavWebsiteString::class.java))
      .registerCodec(GenericCodec(EavWebsiteInt::class.java))
      .registerCodec(GenericCodec(EavWebsiteMoney::class.java))
      .registerCodec(GenericCodec(EavWebsiteInfo::class.java))
      .registerCodec(GenericCodec(EavWebsiteMultiSelect::class.java))
      .registerCodec(GenericCodec(FullScope::class.java))
      .registerCodec(GenericCodec(ServerDataData::class.java))
      .registerCodec(GenericCodec(ServerVersionData::class.java))
      .registerCodec(GenericCodec(TextPages::class.java))
      .registerCodec(GenericCodec(TextPagesSeo::class.java))
      .registerCodec(GenericCodec(PageIndex::class.java))
      .registerCodec(GenericCodec(FullTextPages::class.java))
      .registerCodec(GenericCodec(TextPageUrls::class.java))
      .registerCodec(GenericCodec(ProductUrls::class.java))
      .registerCodec(GenericCodec(CategoryUrls::class.java))
      .registerCodec(GenericCodec(FullUrlKeys::class.java))
      .registerCodec(GenericCodec(FullUrlRequestInfo::class.java))
      .registerCodec(GenericCodec(JoinList::class.java))
      .registerCodec(GenericCodec(UrlKeys::class.java))
      .registerCodec(GenericCodec(User::class.java))
      .registerCodec(GenericCodec(UserCreation::class.java))
      .registerCodec(GenericCodec(BackendPermissions::class.java))
      .registerCodec(GenericCodec(FullUser::class.java))
      .registerCodec(GenericCodec(Template::class.java))
      .registerCodec(GenericCodec(Block::class.java))
  }

}
