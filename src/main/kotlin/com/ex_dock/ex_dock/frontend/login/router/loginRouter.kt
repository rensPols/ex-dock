package com.ex_dock.ex_dock.frontend.login.router

import com.ex_dock.ex_dock.database.account.User
import com.ex_dock.ex_dock.frontend.auth.ExDockAuthHandler
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import io.vertx.core.MultiMap
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

fun Router.initLogin(vertx: Vertx) {
  val loginRouter = Router.router(vertx)
  val logoutRouter = Router.router(vertx)
  val eventBus = vertx.eventBus()
  val singleUseTemplateDataDeliveryOptions = DeliveryOptions().setCodecName("singleUseTemplateDataCodec")
  val authHandler = ExDockAuthHandler(vertx)

  loginRouter.route().handler(BodyHandler.create())

  loginRouter["/"].handler { ctx ->
    val params = ctx.queryParams()
    var error = false
    var errorMessage = ""

    try {
      errorMessage = formatErrorMessage(params["error"])
      error = true
    } catch (_: Exception) {}

    val loginMap = mapOf(
      Pair("error", error),
      Pair("errorMessage", errorMessage)
    )
    val loginTemplate = SingleUseTemplateData(
      "templates/login.peb",
      loginMap
    )

    eventBus.request<String>("template.generate.singleUse",
      loginTemplate,
      singleUseTemplateDataDeliveryOptions).onFailure {
      ctx.fail(it)
    }.onComplete {
      ctx.response().putHeader("Content-Type", "text/html").end(it.result().body())
    }
  }

  loginRouter["/handler"].handler { ctx ->
    val session = ctx.session()
    val requestBody: MultiMap = ctx.queryParams()
    val credentials = UsernamePasswordCredentials(requestBody["email"], requestBody["password"])

    authHandler.authenticate(credentials).onComplete {
      if (it.succeeded()) {
        session.put("user", it.result())
        ctx.redirect("/")
      } else {
        ctx.redirect("/login?error=password2")
      }
    }
  }

  logoutRouter["/"].handler { ctx ->
    val session = ctx.session()
    session.remove<User>("user")
    ctx.response().putHeader("Content-Type", "text/html").end("Logged out")
  }

  this.route("/login*").subRouter(loginRouter)
  this.route("/logout*").subRouter(logoutRouter)
}

fun formatErrorMessage(string: String): String {
  return when (string) {
    "password1" -> "Passwords do not match"
    "password2" -> "Incorrect email or password"
    "email" -> "Invalid email address"
    else -> "Unknown error"
  }
}
