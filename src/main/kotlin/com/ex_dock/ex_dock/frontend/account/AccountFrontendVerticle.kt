package com.ex_dock.ex_dock.frontend.account

import com.ex_dock.ex_dock.database.account.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.MultiMap
import io.vertx.core.Promise
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.Json

class AccountFrontendVerticle: AbstractVerticle() {

  private lateinit var eventBus: EventBus
  private var userDeliveryOptions = DeliveryOptions().setCodecName("UserCreationCodec")
  private var backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsCodec")
  private var fullUserDeliveryOptions = DeliveryOptions().setCodecName("FullUserCodec")
  override fun start(startPromise: Promise<Void>?) {
    eventBus = vertx.eventBus()

    handleAccountCreation()
  }

  private fun handleAccountCreation() {
    eventBus.consumer<MultiMap>("account.router.createUser") { message ->
      val data = message.body()
      val createUser = UserCreation(
        data["email"],
        data["password"])

      eventBus.request<User>("process.account.createUser", createUser, userDeliveryOptions)
        .onComplete { createUserMessage ->
        if (createUserMessage.succeeded()) {
          val newUser = createUserMessage.result().body()

          val backendPermissions = BackendPermissions(
            newUser.userId,
            convertStringToPermission(data["userPermission"]),
            convertStringToPermission(data["serverSettings"]),
            convertStringToPermission(data["template"]),
            convertStringToPermission(data["categoryContent"]),
            convertStringToPermission(data["categoryProducts"]),
            convertStringToPermission(data["productContent"]),
            convertStringToPermission(data["productPrice"]),
            convertStringToPermission(data["productWarehouse"]),
            convertStringToPermission(data["textPages"]),
            data["apiKey"],
            )

          eventBus.request<BackendPermissions>("process.account.createBackendPermissions",
            backendPermissions, backendPermissionsDeliveryOptions).onComplete {
              if (it.succeeded()) {
                message.reply(FullUser(newUser, it.result().body()), fullUserDeliveryOptions)
              } else {
                it.cause().message
              }

          }
        } else {
          createUserMessage.cause().message
        }
      }
    }
  }

  private fun convertStringToPermission(name: String): Permission {
    when (name) {
      "read" -> return Permission.READ
      "write" -> return Permission.WRITE
      "read-write" -> return Permission.READ_WRITE
    }

    return Permission.NONE
  }
}
