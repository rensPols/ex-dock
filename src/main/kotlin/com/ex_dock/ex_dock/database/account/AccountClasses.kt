package com.ex_dock.ex_dock.database.account

data class User(var userId: Int, var email: String, var password: String)

data class BackendPermissions(
  val userId: Int,
  var userPermissions: Permissions,
  var serverSettings: Permissions,
  var template: Permissions,
  var categoryContent: Permissions,
  var categoryProducts: Permissions,
  var productContent: Permissions,
  var productPrice: Permissions,
  var productWarehouse: Permissions,
  var textPages: Permissions,
  var apiKey: String?
)

data class FullUserInformation(
  val user: User,
  val backendPermissions: BackendPermissions
)

enum class Permissions(name: String) {
  NONE("none"),
  READ("read"),
  WRITE("write"),
  READ_WRITE("read-write"),
}
