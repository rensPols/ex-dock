package com.ex_dock.ex_dock.frontend.auth

import com.ex_dock.ex_dock.database.account.FullUser
import com.ex_dock.ex_dock.database.account.Permission
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authentication.AuthenticationProvider
import io.vertx.ext.auth.authentication.Credentials
import io.vertx.ext.auth.authorization.Authorization
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization
import org.mindrot.jbcrypt.BCrypt
import java.util.function.Consumer

/**
 * Custom implementation of the authentication provider.
 * All authentication methods are in this class
 */
class ExDockAuthHandler(vertx: Vertx) : AuthenticationProvider{
  private val eventBus: EventBus = vertx.eventBus()
  private val saveAuthorization: MutableSet<Authorization> = setOf(
    PermissionBasedAuthorization.create("userRead"),
    PermissionBasedAuthorization.create("userWrite"),
    PermissionBasedAuthorization.create("serverRead"),
    PermissionBasedAuthorization.create("serverWrite"),
    PermissionBasedAuthorization.create("templateRead"),
    PermissionBasedAuthorization.create("templateWrite"),
    PermissionBasedAuthorization.create("categoryContentRead"),
    PermissionBasedAuthorization.create("categoryContentWrite"),
    PermissionBasedAuthorization.create("categoryProductRead"),
    PermissionBasedAuthorization.create("categoryProductWrite"),
    PermissionBasedAuthorization.create("productContentRead"),
    PermissionBasedAuthorization.create("productContentWrite"),
    PermissionBasedAuthorization.create("productPriceRead"),
    PermissionBasedAuthorization.create("productPriceWrite"),
    PermissionBasedAuthorization.create("productWarehouseRead"),
    PermissionBasedAuthorization.create("productWarehouseWrite"),
    PermissionBasedAuthorization.create("textPagesRead"),
    PermissionBasedAuthorization.create("textPagesWrite")
  ).toMutableSet()

  /**
   * Authenticate the user based on the given credentials
   */
  @Deprecated("Deprecated in Kotlin")
  override fun authenticate(credentials: JsonObject?, resultHandler: Handler<AsyncResult<User>>?) {
    //Test if credentials are not null
    if (credentials == null) {
      resultHandler?.handle(Future.failedFuture("Missing credentials"))
      return
    }

    val email = credentials.getString("username")
    val password = credentials.getString("password")

    //Request a user based on the given email
    eventBus.request<FullUser>("process.account.getFullUserByEmail", email).onComplete {
      if (it.succeeded()) {
        val user = it.result().body()
        //Check if the password matches the hashed password in the database
        if (BCrypt.checkpw(password, user.user.password)) {
          resultHandler?.handle(Future.succeededFuture(convertUser(user)))
        } else {
          resultHandler?.handle(Future.failedFuture("Invalid password"))
        }
      } else {
        resultHandler?.handle(Future.failedFuture("User not found"))
      }
    }
  }

  /**
   * Authenticates the user with given UsernamePasswordCredentials
   */
  override fun authenticate(credentials: Credentials?, resultHandler: Handler<AsyncResult<User>>?) {
    return authenticate(credentials?.toJson(), resultHandler)
  }

  /**
   * Converts the full User from the database to a vertx user
   */
  private fun convertUser(fullUser: FullUser): User {
    val exDockUser = fullUser.user
    val principal = JsonObject()
      .put("id", exDockUser.userId)
      .put("email", exDockUser.email)
      .put("password", exDockUser.password)
    var user = User.create(principal)

    user = addPermission(fullUser.backendPermissions.userPermission, "user", user)
    user = addPermission(fullUser.backendPermissions.serverSettings, "server", user)
    user = addPermission(fullUser.backendPermissions.template, "template", user)
    user = addPermission(fullUser.backendPermissions.categoryContent, "categoryContent", user)
    user = addPermission(fullUser.backendPermissions.categoryProducts, "categoryProducts", user)
    user = addPermission(fullUser.backendPermissions.productContent, "productContent", user)
    user = addPermission(fullUser.backendPermissions.productPrice, "productPrice", user)
    user = addPermission(fullUser.backendPermissions.productWarehouse, "productWarehouse", user)
    user = addPermission(fullUser.backendPermissions.textPages, "textPages", user)

    return user
  }

  /**
   * Adds permissions to the user account based on full user permissions
   */
  private fun addPermission(permission: Permission, task: String, user: User): User {
    return when (permission) {
      Permission.NONE -> user
      Permission.READ -> addAuth(task + Permission.fromName(Permission.READ), user)
      Permission.WRITE -> addAuth(task + Permission.fromName(Permission.WRITE), user)
      Permission.READ_WRITE -> {
        val newUser = addAuth(task + Permission.fromName(Permission.READ), user)
        addAuth(task + Permission.fromName(Permission.WRITE), newUser)
      }
    }
  }

  /**
   * Adds unknown authorizations to the list of authorizations
   */
  private fun addAuth(name: String, user: User): User {
    if (saveAuthorization.contains(PermissionBasedAuthorization.create(name))) {
      user.authorizations().add(name,
        PermissionBasedAuthorization.create(name))
    } else {
      saveAuthorization.add(PermissionBasedAuthorization.create(name))
      user.authorizations().add(name,
        PermissionBasedAuthorization.create(name))
    }

    return user
  }

  /**
   * Verify if the user is authorized to view the requested resource
   */
  fun verifyPermissionAuthorization(user: User?, task: String, callBack: Consumer<JsonObject>) {
    if (user == null) {
      callBack.accept(JsonObject().apply {
        put("success", false)
        put("message", "User not authenticated")
      })
      return
    }

    if (saveAuthorization.contains(PermissionBasedAuthorization.create(task))
      && saveAuthorization.first { authorization -> authorization == PermissionBasedAuthorization.create(task) }
        .match(user)) {
      callBack.accept(JsonObject().apply {
        put("success", true)
      })
    } else {
      callBack.accept(JsonObject().apply {
        put("success", false)
        put("message", "Permission denied for task: $task")
      })
    }
   }
}
