package com.ex_dock.ex_dock.frontend.account

import com.ex_dock.ex_dock.database.account.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.MultiMap
import io.vertx.core.Promise
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus

class AccountFrontendVerticle: AbstractVerticle() {

  private lateinit var eventBus: EventBus
  private var userDeliveryOptions = DeliveryOptions().setCodecName("UserCreationCodec")
  private var backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsCodec")
  private var fullUserDeliveryOptions = DeliveryOptions().setCodecName("FullUserCodec")
  private var listDeliveryOptions = DeliveryOptions().setCodecName("ListCodec")
  override fun start(startPromise: Promise<Void>?) {
    eventBus = vertx.eventBus()

    handleAccountCreation()
    getAccountHomeData()
    handleEditRequestPage()
    handleEditAccountData()
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

  private fun handleEditAccountData() {
    eventBus.consumer<MultiMap>("account.router.editAccountData").handler { message ->
      val data = message.body()
      val userId = data["userId"]?.toInt()?: -1
      val updatedUser = User(userId, data["email"], data["password"])
      val updatedBackendPermissions = BackendPermissions(
        userId,
        convertStringToPermission(data["userPermission"]),
        convertStringToPermission(data["serverSettings"]),
        convertStringToPermission(data["template"]),
        convertStringToPermission(data["categoryContent"]),
        convertStringToPermission(data["categoryProducts"]),
        convertStringToPermission(data["productContent"]),
        convertStringToPermission(data["productPrice"]),
        convertStringToPermission(data["productWarehouse"]),
        convertStringToPermission(data["textPages"]),
        data["apiKey"]
      )

      eventBus.request<User>("process.account.updateUser", updatedUser, userDeliveryOptions).onComplete {
        if (it.succeeded()) {
          eventBus.request<BackendPermissions>("process.account.updateBackendPermissions",
            updatedBackendPermissions, backendPermissionsDeliveryOptions)
            .onFailure { failure ->
              message.reply(failure.message)
            }.onComplete { msg ->
              if (msg.succeeded()) {
                message.reply("success")
              } else {
                message.reply(msg.cause().message)
              }
            }
        } else {
          message.reply(it.cause().message)
        }
      }
    }
  }

  private fun getAccountHomeData() {
    eventBus.consumer<String>("account.router.homeData").handler { message ->
      eventBus.request<MutableList<FullUser>>("process.account.getAllFullUserInfo", "").onComplete {
        if (it.succeeded()) {
          val fullUserList = it.result().body()
          message.reply(fullUserList, listDeliveryOptions)
        } else {
          it.cause().message
        }
      }
    }
  }

  private fun handleEditRequestPage() {
    eventBus.consumer<Int>("account.router.handleEditPage").handler { message ->
      val userId = message.body()

      eventBus.request<FullUser>("process.account.getFullUserById", userId).onComplete {
        if (it.succeeded()) {
          val fullUser = it.result().body()
          message.reply(fullUser, fullUserDeliveryOptions)
        } else {
          it.cause().message
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
