package com.ex_dock.ex_dock.database.account

import java.util.*

data class User(var userId: Int, var email: String, var password: String)

data class UserCreation(var email: String, var password: String)

data class BackendPermissions(
    val userId: Int,
    var userPermission: Permission,
    var serverSettings: Permission,
    var template: Permission,
    var categoryContent: Permission,
    var categoryProducts: Permission,
    var productContent: Permission,
    var productPrice: Permission,
    var productWarehouse: Permission,
    var textPages: Permission,
    var apiKey: String?
)

data class FullUser(var user: User, var backendPermissions: BackendPermissions) {
  init {
    require(user.userId == backendPermissions.userId)
  }
}

data class BackendPermissionsEditPage(
  val userId: Int,
  var userPermission: Pair<String, Permission>,
  var serverSettings: Pair<String, Permission>,
  var template: Pair<String, Permission>,
  var categoryContent: Pair<String, Permission>,
  var categoryProducts: Pair<String, Permission>,
  var productContent: Pair<String, Permission>,
  var productPrice: Pair<String, Permission>,
  var productWarehouse: Pair<String, Permission>,
  var textPages: Pair<String, Permission>,
  var apiKey: String?
)

enum class Permission(name: String) {
  NONE("None"),
  READ("Read"),
  WRITE("Write"),
  READ_WRITE("Read-Write");

  companion object {
    fun fromString(value: String): Permission {
      return when (value) {
        "read" -> READ
        "write" -> WRITE
        "read-write" -> READ_WRITE
        else -> NONE
      }
    }

    fun fromName(permission: Permission): String {
      return when (permission) {
        READ -> "Read"
        WRITE -> "Write"
        READ_WRITE -> "ReadWrite"
        NONE -> "None"
      }
    }
  }
}
