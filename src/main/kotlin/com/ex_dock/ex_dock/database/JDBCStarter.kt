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
import com.ex_dock.ex_dock.database.service.PopulateException
import com.ex_dock.ex_dock.database.service.ServiceVerticle
import com.ex_dock.ex_dock.database.template.Block
import com.ex_dock.ex_dock.database.template.Template
import com.ex_dock.ex_dock.database.template.TemplateJdbcVerticle
import com.ex_dock.ex_dock.database.text_pages.FullTextPages
import com.ex_dock.ex_dock.database.text_pages.TextPages
import com.ex_dock.ex_dock.database.text_pages.TextPagesJdbcVerticle
import com.ex_dock.ex_dock.database.text_pages.TextPagesSeo
import com.ex_dock.ex_dock.database.url.*
import com.ex_dock.ex_dock.frontend.cache.CacheVerticle
import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import com.sun.tools.javac.jvm.Gen
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
          throw PopulateException("Could not populate the database with standard data. Closing the server!")
        }.onSuccess {
          println("Database populated with standard Data")
          starPromise.complete()
        }
      }
      .onFailure { error ->
        println("Failed to deploy JDBC verticles: $error")
      }
  }

  private fun addAllVerticles() {
    verticles.add(vertx.deployWorkerVerticleHelper(AccountJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(CategoryJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(CheckoutJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(HomeJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(TextPagesJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ScopeJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ServerJDBCVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(UrlJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductCompleteEavJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductGlobalEavJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductMultiSelectJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductStoreViewEavJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductWebsiteEavJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ProductCustomAttributesJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(TemplateJdbcVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(ServiceVerticle::class))
    verticles.add(vertx.deployWorkerVerticleHelper(CacheVerticle::class))
  }

  private fun getAllCodecClasses() {
    vertx.eventBus()
      .registerCodec(GenericCodec(MutableList::class))
      .registerCodec(GenericCodec(Categories::class))
      .registerCodec(GenericCodec(CategoriesSeo::class))
      .registerCodec(GenericCodec(CategoriesProducts::class))
      .registerCodec(GenericCodec(FullCategoryInfo::class))
      .registerCodec(GenericCodec(CustomProductAttributes::class))
      .registerCodec(GenericCodec(Products::class))
      .registerCodec(GenericCodec(EavGlobalBool::class))
      .registerCodec(GenericCodec(EavGlobalFloat::class))
      .registerCodec(GenericCodec(EavGlobalInt::class))
      .registerCodec(GenericCodec(EavGlobalMoney::class))
      .registerCodec(GenericCodec(EavGlobalMultiSelect::class))
      .registerCodec(GenericCodec(EavGlobalString::class))
      .registerCodec(GenericCodec(Eav::class))
      .registerCodec(GenericCodec(EavGlobalInfo::class))
      .registerCodec(GenericCodec(ProductsSeo::class))
      .registerCodec(GenericCodec(ProductsPricing::class))
      .registerCodec(GenericCodec(FullProduct::class))
      .registerCodec(GenericCodec(MultiSelectBool::class))
      .registerCodec(GenericCodec(MultiSelectFloat::class))
      .registerCodec(GenericCodec(MultiSelectString::class))
      .registerCodec(GenericCodec(MultiSelectInt::class))
      .registerCodec(GenericCodec(MultiSelectMoney::class))
      .registerCodec(GenericCodec(MultiSelectInfo::class))
      .registerCodec(GenericCodec(Websites::class))
      .registerCodec(GenericCodec(StoreView::class))
      .registerCodec(GenericCodec(EavStoreViewBool::class))
      .registerCodec(GenericCodec(EavStoreViewFloat::class))
      .registerCodec(GenericCodec(EavStoreViewString::class))
      .registerCodec(GenericCodec(EavStoreViewInt::class))
      .registerCodec(GenericCodec(EavStoreViewMoney::class))
      .registerCodec(GenericCodec(EavStoreViewInfo::class))
      .registerCodec(GenericCodec(EavStoreViewMultiSelect::class))
      .registerCodec(GenericCodec(EavWebsiteBool::class))
      .registerCodec(GenericCodec(EavWebsiteFloat::class))
      .registerCodec(GenericCodec(EavWebsiteString::class))
      .registerCodec(GenericCodec(EavWebsiteInt::class))
      .registerCodec(GenericCodec(EavWebsiteMoney::class))
      .registerCodec(GenericCodec(EavWebsiteInfo::class))
      .registerCodec(GenericCodec(EavWebsiteMultiSelect::class))
      .registerCodec(GenericCodec(FullScope::class))
      .registerCodec(GenericCodec(ServerDataData::class))
      .registerCodec(GenericCodec(ServerVersionData::class))
      .registerCodec(GenericCodec(TextPages::class))
      .registerCodec(GenericCodec(TextPagesSeo::class))
      .registerCodec(GenericCodec(PageIndex::class))
      .registerCodec(GenericCodec(FullTextPages::class))
      .registerCodec(GenericCodec(TextPageUrls::class))
      .registerCodec(GenericCodec(ProductUrls::class))
      .registerCodec(GenericCodec(CategoryUrls::class))
      .registerCodec(GenericCodec(FullUrlKeys::class))
      .registerCodec(GenericCodec(FullUrlRequestInfo::class))
      .registerCodec(GenericCodec(JoinList::class))
      .registerCodec(GenericCodec(UrlKeys::class))
      .registerCodec(GenericCodec(User::class))
      .registerCodec(GenericCodec(UserCreation::class))
      .registerCodec(GenericCodec(BackendPermissions::class))
      .registerCodec(GenericCodec(FullUser::class))
      .registerCodec(GenericCodec(Template::class))
      .registerCodec(GenericCodec(Block::class))
      .registerCodec(GenericCodec(Map::class))
  }

}
