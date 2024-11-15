package com.ex_dock.ex_dock.frontend.account.router

import com.ex_dock.ex_dock.database.account.FullUser
import com.ex_dock.ex_dock.database.account.Permission
import com.ex_dock.ex_dock.frontend.auth.ExDockAuthHandler
import com.ex_dock.ex_dock.frontend.login.router.formatErrorMessage
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.ext.auth.User
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

fun Router.initAccount(vertx: Vertx) {
  val accountRouter = Router.router(vertx)
  val eventBus: EventBus = vertx.eventBus()
  val singleUseTemplateDataDeliveryOptions = DeliveryOptions().setCodecName("singleUseTemplateDataCodec")
  val mapDeliveryOptions = DeliveryOptions().setCodecName("MultiMapCodec")
  val authHandler = ExDockAuthHandler(vertx)

  accountRouter["/createUser"].handler(BodyHandler.create())

  accountRouter["/"].handler { ctx ->
    val session = ctx.session()
    val user = session.get<User>("user")

    authHandler.verifyPermissionAuthorization(user, "userRead") {
      if (it.getBoolean("success")) {
        var fullUserList: MutableList<FullUser>
        eventBus.request<MutableList<FullUser>>("account.router.homeData", "").onComplete { homeDataMsg ->
          fullUserList = homeDataMsg.result().body()

          val accountTemplateMap: Map<String, Any> = mapOf(Pair("name", "test"), Pair("accounts", fullUserList))
          val accountTemplate = SingleUseTemplateData(
            template = "templates/account.peb",
            templateData = accountTemplateMap,
          )

          eventBus.request<String>(
            "template.generate.singleUse",
            accountTemplate,
            singleUseTemplateDataDeliveryOptions
          ).onFailure {
            ctx.fail(it)
          }.onComplete {
            ctx.response().putHeader("Content-Type", "text/html").end(it.result().body())
          }
        }
      } else {
        ctx.response().putHeader("Content-Type", "text/html").end("Unauthorized")
      }
    }
  }

  accountRouter["/new"].handler { ctx ->
    val session = ctx.session()
    val user = session.get<User>("user")
    val params = ctx.queryParams()
    var error = false
    var errorMessage = ""

    try {
      errorMessage = formatErrorMessage(params["error"])
      error = true
    } catch (_: Exception) {}

    authHandler.verifyPermissionAuthorization(user, "userWrite") {
      if (it.getBoolean("success")) {
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
          Pair("permissions", permissionList),
          Pair("error", error),
          Pair("errorMessage", errorMessage),
        )
        val newAccountTemplate = SingleUseTemplateData(
          template = "templates/newAccount.peb",
          templateData = newAccountTemplateMap,
        )

        eventBus.request<String>(
          "template.generate.singleUse",
          newAccountTemplate,
          singleUseTemplateDataDeliveryOptions
        ).onFailure {
          ctx.fail(it)
        }.onComplete {
          ctx.response().putHeader("Content-Type", "text/html").end(it.result().body())
        }
      } else {
        ctx.fail(403)
      }
    }
  }

  accountRouter["/new/handler"].handler { ctx ->
    val requestBody = ctx.queryParams()
    if (requestBody["password"] != requestBody["cpassword"]) {
      ctx.redirect("/account/new?error=password1")
      ctx.end()
    }

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

  accountRouter["/:userId"].handler { ctx ->
    val session = ctx.session()
    val user: User? = session["user"]

    authHandler.verifyPermissionAuthorization(user, "userWrite") {
      if (it.getBoolean("success")) {
        generateUserInfoPage(ctx, eventBus, false)
      } else {
        authHandler.verifyPermissionAuthorization(user, "userRead") {
          if (it.getBoolean("success")) {
            generateUserInfoPage(ctx, eventBus, true)
          } else {
            ctx.fail(403)
          }
        }
      }
    }
  }

  accountRouter.get("/").handler { ctx ->
    eventBus.request<Any>("process.account.getData", "testUser")
      .onSuccess { reply ->
        ctx.end(reply.body().toString())
      }
      .onFailure { error ->
        ctx.end("Error retrieving account data: ${error.localizedMessage}")
      }
  }

  accountRouter["/:userId/edit"].handler { ctx ->
    val session = ctx.session()
    val user: User? = session["user"]
    val userId = ctx.request().params()["userId"]
    val params = ctx.queryParams()
    params.add("userId", userId)

    if (params["password"] != params["cpassword"]) {
      ctx.redirect("/account/new?error=password1")
      ctx.end()
    }

    authHandler.verifyPermissionAuthorization(user, "userWrite") {
      if (it.getBoolean("success")) {
        eventBus.request<String>("account.router.editAccountData", params, mapDeliveryOptions)
          .onFailure { failure ->
            ctx.fail(failure)
          }.onComplete { _ ->
            ctx.redirect("/account")
          }
      } else {
        ctx.fail(403)
      }
    }
  }

  this.route("/account*").subRouter(accountRouter)
}

fun generateUserInfoPage(ctx: RoutingContext, eventBus: EventBus, readOnly: Boolean) {
  val userId = ctx.request().params()["userId"]?.toInt()
  var readOnlyValue = readOnly
  if (userId == 1) readOnlyValue = true
  val params = ctx.queryParams()
  var error = false
  var errorMessage = ""

  try {
    errorMessage = formatErrorMessage(params["error"])
    error = true
  } catch (_: Exception) {}

  eventBus.request<FullUser>("process.account.getFullUserByUserId", userId).onFailure {
    ctx.fail(it)
  }.onSuccess {
    val fullUser = it.body()
    val permissionList: List<PermissionInfo> = listOf(
      PermissionInfo("userPermission", "User permissions",
        Permission.fromName(fullUser.backendPermissions.userPermission)),
      PermissionInfo("serverSettings", "Server settings",
        Permission.fromName(fullUser.backendPermissions.serverSettings)),
      PermissionInfo("template", "Templates",
        Permission.fromName(fullUser.backendPermissions.template)),
      PermissionInfo("categoryContent", "Category content",
        Permission.fromName(fullUser.backendPermissions.categoryContent)),
      PermissionInfo("categoryProducts", "Category products",
        Permission.fromName(fullUser.backendPermissions.categoryProducts)),
      PermissionInfo("productContent", "Product content",
        Permission.fromName(fullUser.backendPermissions.productContent)),
      PermissionInfo("productPrice", "Product price",
        Permission.fromName(fullUser.backendPermissions.productPrice)),
      PermissionInfo("productWarehouse", "Product warehouse",
        Permission.fromName(fullUser.backendPermissions.productWarehouse)),
      PermissionInfo("textPages", "Text pages",
        Permission.fromName(fullUser.backendPermissions.textPages))
    )
    val accountTemplateMap = mapOf(
      Pair("user", fullUser),
      Pair("permissions", permissionList),
      Pair("readOnly", readOnlyValue),
      Pair("error", error),
      Pair("errorMessage", errorMessage),
    )

    val accountTemplate = SingleUseTemplateData(
      template = "templates/accountInfo.peb",
      templateData = accountTemplateMap,
    )

    eventBus.request<String>("template.generate.singleUse", accountTemplate,
      DeliveryOptions().setCodecName("singleUseTemplateDataCodec")).onFailure { failure ->
      ctx.fail(failure)
    }.onComplete { result ->
      ctx.response().putHeader("Content-Type", "text/html").end(result.result().body())
    }
  }
}

data class PermissionInfo(val permission: String, val name: String, val value: String)
