package com.ex_dock.ex_dock.frontend.account.router

import com.ex_dock.ex_dock.database.account.FullUser
import com.ex_dock.ex_dock.database.account.User
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import com.ex_dock.ex_dock.frontend.auth.ExDockAuthHandler
import io.vertx.ext.web.Router
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials
import io.vertx.ext.web.Session

fun Router.initAccount(vertx: Vertx) {
  val accountRouter = Router.router(vertx)
  val eventBus: EventBus = vertx.eventBus()
  val singleUseTemplateDataDeliveryOptions = DeliveryOptions().setCodecName("singleUseTemplateDataCodec")
  val mapDeliveryOptions = DeliveryOptions().setCodecName("MultiMapCodec")

  accountRouter["/createUser"].handler(BodyHandler.create())

  accountRouter["/"].handler { ctx ->
    var fullUserList: MutableList<FullUser>
    eventBus.request<MutableList<FullUser>>("account.router.homeData", "").onComplete { homeDataMsg ->
      fullUserList = homeDataMsg.result().body()

      val accountTemplateMap: Map<String, Any> = mapOf(Pair("name", "test"), Pair("accounts", fullUserList))
      val accountTemplate = SingleUseTemplateData(
        template = "templates/account.peb",
        templateData = accountTemplateMap,
      )

      eventBus.request<String>("template.generate.singleUse",
        accountTemplate,
        singleUseTemplateDataDeliveryOptions).onFailure {
        ctx.fail(it)
      }.onComplete {
        ctx.response().putHeader("Content-Type", "text/html").end(it.result().body())
      }
    }
  }

  accountRouter["/new"].handler { ctx ->
    val permissionList = listOf(
      Pair("userPermission", "User permissions"),
      Pair("serverSettings", "Server settings"),
      Pair("template", "Templates"),
      Pair("categoryContent", "Category content"),
      Pair("categoryProducts", "Category products"),
      Pair("productContent", "Product content"),
      Pair("productPrice", "Product price"),
      Pair("productWarehouse", "Product warehouse"),
      Pair("textPages", "Text pages")
    )
    val newAccountTemplateMap: Map<String, Any> = mapOf(
      Pair("permissions", permissionList)
    )
    val newAccountTemplate = SingleUseTemplateData(
      template = "templates/newAccount.peb",
      templateData = newAccountTemplateMap,
    )

    eventBus.request<String>("template.generate.singleUse",
      newAccountTemplate,
      singleUseTemplateDataDeliveryOptions).onFailure {
      ctx.fail(it)
    }.onComplete {
      ctx.response().putHeader("Content-Type", "text/html").end(it.result().body())
    }
  }

  accountRouter["/createUser"].handler { ctx ->
    val requestBody = ctx.queryParams()
    eventBus.request<FullUser>("account.router.createUser", requestBody, mapDeliveryOptions) {
      println(it.result().body())
      ctx.redirect("/account")
    }
  }

  accountRouter["/delete/:userId"].handler { ctx ->
    val userId = ctx.request().getParam("userId").toInt()
    eventBus.request<String>("process.account.deleteBackendPermissions", userId) {
      if (it.succeeded()) {
        eventBus.request<String>("process.account.deleteUser", userId) { deleteUserMsg ->
          if (deleteUserMsg.succeeded()) {
            ctx.redirect("/account")
          } else {
            ctx.fail(deleteUserMsg.cause())
          }
        }
      } else {
        ctx.fail(it.cause())
      }
    }
  }
  val authHandler = ExDockAuthHandler(vertx)

  accountRouter.get("/").handler { ctx ->
    eventBus.request<Any>("process.account.getData", "testUser")
      .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }
      .onFailure { error ->
        ctx.end("Error retrieving account data: ${error.localizedMessage}")
      }
  }

  this.route("/account*").subRouter(accountRouter)
}
