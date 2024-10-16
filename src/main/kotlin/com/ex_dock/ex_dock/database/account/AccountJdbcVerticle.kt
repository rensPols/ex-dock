package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCPool
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import org.mindrot.jbcrypt.BCrypt

class AccountJdbcVerticle: AbstractVerticle() {

  private lateinit var client: Pool

  private lateinit var eventBus: EventBus


  companion object {
    const val FAILED_MESSAGE = "failed"
    const val NO_USER_MESSAGE = "User does not exist"
    const val USER_DELETED_SUCCESS = "User deleted successfully"

    const val BACKEND_PERMISSION_CREATION_FAILED = "Failed to create backend permissions"
    const val BACKEND_PERMISSION_UPDATED = "Backend Permissions were successfully updated!"
    const val BACKEND_PERMISSION_UPDATE_FAILED = "Failed to update backend permissions"
    const val BACKEND_PERMISSION_DELETED = "Backend Permissions were successfully deleted!"
    const val BACKEND_PERMISSION_DELETE_FAILED = "Failed to delete backend permissions"
  }

  private val userDeliveryOptions = DeliveryOptions().setCodecName("UserCodec")
  private val userListDeliveryOptions = DeliveryOptions().setCodecName("UserListCodec")
  private val backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsCodec")
  private val backendPermissionsListDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsListCodec")
  private val fullUserDeliveryOptions = DeliveryOptions().setCodecName("FullUserCodec")
  private val fullUserListDeliveryOptions = DeliveryOptions().setCodecName("FullUserListCodec")

  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    // Initialize all eventbus connections for user management
    getAllUsers()
    getUserById()
    createUser()
    updateUser()
    deleteUser()

    // Initialize all eventbus connections for backend permissions
    getAllBackendPermissions()
    getBackendPermissionsByUserId()
    createBackendPermissions()
    updateBackendPermissions()
    deleteBackendPermissions()

    // Initialize all eventbus connections for full user information
    getAllFullUserInfo()
    getFullUserInformationByUserId()
  }

  private fun getAllUsers() {
    val allUserDataConsumer = eventBus.consumer<String>("process.account.getAllUsers")
    allUserDataConsumer.handler { message ->
      val query = "SELECT * FROM users"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
//          json = json {
//            obj(
//              "users" to rows.map { row ->
//                obj(
//                  makeUserJsonFields(row)
//                )
//              }
//            )
//          }
//          message.reply(json)
          message.reply(rows.map { row -> makeUserObject(row) }, userListDeliveryOptions)
        } else {
          message.reply(emptyList<User>(), userListDeliveryOptions)
        }
      }
    }
  }

  private fun getUserById() {
    val getUserByIdConsumer = eventBus.consumer<Int>("process.account.getUserById")
    getUserByIdConsumer.handler { message ->
      val userId = message.body()
      val query = "SELECT * FROM users WHERE user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
//          json = json {
//            obj(
//              makeUserJsonFields(rows.first())
//            )
//          }
//          message.reply(json)
          message.reply(makeUserObject(rows.first()), userDeliveryOptions)
        } else {
//          message.reply(json { obj("user" to "{}") })
          message.fail(404, json {obj("user" to "{}") }.toString())
        }
      }
    }
  }

  private fun createUser() {
    val createUserConsumer = eventBus.consumer<JsonObject>("process.account.createUser")
    createUserConsumer.handler { message ->
      val query = "INSERT INTO users (email, password) VALUES (?,?) RETURNING user_id AS UID"
      val rowsFuture = client
        .preparedQuery(query)
        .execute(makeUserTuple(message.body(), false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(400, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val lastInsertID: Row = res.property(JDBCPool.GENERATED_KEYS)
        message.reply(lastInsertID.getInteger(0))
      }
    }
  }

  private fun updateUser() {
    val updateUserConsumer = eventBus.consumer<JsonObject>("process.account.updateUser")
    updateUserConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE users SET email = ?, password = ? WHERE user_id = ?"
      val userTuple = makeUserTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(userTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply("User updated successfully")
        } else {
          message.fail(404, NO_USER_MESSAGE)
        }
      }
    }
  }

  private fun deleteUser() {
    val deleteUserConsumer = eventBus.consumer<Int>("process.account.deleteUser")
    deleteUserConsumer.handler { message ->
      val userId = message.body()
      val query = "DELETE FROM users WHERE user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply(USER_DELETED_SUCCESS)
        } else {
          message.fail(404, NO_USER_MESSAGE)
        }
      }
    }
  }

  private fun getAllBackendPermissions() {
    val allBackendPermissionsDataConsumer = eventBus.consumer<String>("process.account.getAllBackendPermissions")
    allBackendPermissionsDataConsumer.handler { message ->
      val query = "SELECT * FROM backend_permissions"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
//          json = json {
//            obj(
//              "backend_permissions" to rows.map { row ->
//                obj(
//                  makeBackendPermissionsJsonFields(row)
//                )
//              }
//            )
//          }
//          message.reply(json)
          message.reply(rows.map { row -> makeBackendPermissionsObject(row) }, backendPermissionsListDeliveryOptions)
        } else {
          message.reply(emptyList<BackendPermissions>(), backendPermissionsListDeliveryOptions)
        }
      }
    }
  }

  private fun getBackendPermissionsByUserId() {
    val getBackendPermissionsByUserIdConsumer =
      eventBus.consumer<Int>("process.account.getBackendPermissionsByUserId")
    getBackendPermissionsByUserIdConsumer.handler { message ->
      val userId = message.body()
      val query = "SELECT * FROM backend_permissions WHERE user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
//          json = json {
//            obj(
//              makeBackendPermissionsJsonFields(rows.first())
//            )
//          }
//          message.reply(json)
          message.reply(makeBackendPermissionsObject(rows.first()), backendPermissionsDeliveryOptions)
        } else {
          message.fail(404, json { obj("backend_permissions" to "{}") }.toString())
        }
      }
    }
  }

  private fun createBackendPermissions() {
    val createBackendPermissionsConsumer =
      eventBus.consumer<JsonObject>("process.account.createBackendPermissions")
    createBackendPermissionsConsumer.handler { message ->
      val query = "INSERT INTO backend_permissions " +
        "(user_id, user_permissions, server_settings, template, category_content, category_products, " +
        "product_content, product_price, product_warehouse, text_pages, \"API_KEY\") VALUES " +
        "(?,?::b_permissions,?::b_permissions,?::b_permissions,?::b_permissions,?::b_permissions,?::b_permissions," +
        "?::b_permissions,?::b_permissions,?::b_permissions,?)"
      val createBackendPermissionTuple = makeBackendPermissionsTuple(message.body(), false)
      val rowsFuture = client.preparedQuery(query).execute(createBackendPermissionTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          val lastInsertID: Row = res.property(JDBCPool.GENERATED_KEYS)
          message.reply(lastInsertID.getInteger(0))
        } else {
          message.fail(400, BACKEND_PERMISSION_CREATION_FAILED)
        }
      }
    }
  }

  private fun updateBackendPermissions() {
    val updateBackendPermissionsConsumer =
      eventBus.consumer<JsonObject>("process.account.updateBackendPermissions")
    updateBackendPermissionsConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE backend_permissions " +
        "SET user_permissions =?::b_permissions, server_settings =?::b_permissions, template =?::b_permissions, " +
        "category_content =?::b_permissions, " +
        "category_products =?::b_permissions, product_content =?::b_permissions, product_price =?::b_permissions, " +
        "product_warehouse =?::b_permissions, " +
        "text_pages =?::b_permissions, \"API_KEY\" =? WHERE user_id =?"
      val updateBackendPermissionTuple = makeBackendPermissionsTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(updateBackendPermissionTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply(BACKEND_PERMISSION_UPDATED)
        } else {
          message.fail(400, BACKEND_PERMISSION_UPDATE_FAILED)
        }
      }
    }
  }

  private fun deleteBackendPermissions() {
    val deleteBackendPermissionsConsumer =
      eventBus.consumer<Int>("process.account.deleteBackendPermissions")
    deleteBackendPermissionsConsumer.handler { message ->
      val userId = message.body()
      val query = "DELETE FROM backend_permissions WHERE user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply(BACKEND_PERMISSION_DELETED)
        } else {
          message.fail(400, BACKEND_PERMISSION_DELETE_FAILED)
        }
      }
    }
  }

  private fun getAllFullUserInfo() {
    val getAllFullUserInfoConsumer = eventBus.consumer<String>("process.account.getAllFullUserInfo")
    getAllFullUserInfoConsumer.handler { message ->
      val query = "SELECT u.user_id, u.email, u.password, bp.user_permissions, bp.server_settings, " +
        "bp.template, bp.category_content, bp.category_products, bp.product_content, bp.product_price, " +
        "bp.product_warehouse, bp.text_pages, bp.\"API_KEY\" FROM users u " +
        "JOIN backend_permissions bp ON u.user_id = bp.user_id"
      val rowsFuture = client.preparedQuery(query).execute()
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
//          json = json {
//            obj(
//              "full_user_info" to rows.map { row ->
//                obj(
//                  makeFullUserInformationJsonFields(row)
//                )
//              }
//            )
//          }
//          message.reply(json)
          message.reply(rows.map { row -> makeFullUserObject(row) }, fullUserListDeliveryOptions)
        } else {
          message.reply(emptyList<FullUser>(), fullUserListDeliveryOptions)
        }
      }
    }
  }

  private fun getFullUserInformationByUserId() {
    val getFullUserInformationByUserIdConsumer =
      eventBus.consumer<Int>("process.account.getFullUserInformationByUserId")
    getFullUserInformationByUserIdConsumer.handler { message ->
      val userId = message.body()
      val query = "SELECT u.user_id, u.email, u.password, bp.user_permissions, bp.server_settings, " +
        "bp.template, bp.category_content, bp.category_products, bp.product_content, bp.product_price, " +
        "bp.product_warehouse, bp.text_pages, bp.\"API_KEY\" FROM users u " +
        "JOIN backend_permissions bp ON u.user_id = bp.user_id WHERE u.user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))
      var json: JsonObject

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED_MESSAGE)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
//          json = json {
//            obj(
//                  makeFullUserInformationJsonFields(rows.first())
//              )
//            }
//          message.reply(json)
          message.reply(makeFullUserObject(rows.first()), fullUserDeliveryOptions)
          } else {
          message.fail(404, json { obj("full_user_info" to "{}") }.toString())
        }
      }
    }
  }

  private fun makeUserJsonFields(row: Row): List<Pair<String, Any>> {
    return listOf(
      "user_id" to row.getInteger("user_id"),
      "email" to row.getString("email"),
      "password" to row.getString("password"),
    )
  }

  private fun makeUserObject(row: Row): User {
    return User(
      userId = row.getInteger("user_id"),
      email = row.getString("email"),
      password = row.getString("password")
    )
  }

  private fun makeBackendPermissionsJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "user_id" to row.getInteger("user_id"),
      "user_permissions" to row.getString("user_permissions"),
      "server_settings" to row.getString("server_settings"),
      "template" to row.getString("template"),
      "category_content" to row.getString("category_content"),
      "category_products" to row.getString("category_products"),
      "product_content" to row.getString("product_content"),
      "product_price" to row.getString("product_price"),
      "product_warehouse" to row.getString("product_warehouse"),
      "text_pages" to row.getString("text_pages"),
      "api_key" to row.getString("API_KEY")
    )
  }

  private fun makeBackendPermissionsObject(row: Row) : BackendPermissions {
    return BackendPermissions(
      userId = row.getInteger("user_id"),
      userPermission = Permission.fromString(row.getString("user_permissions")),
      serverSettings = Permission.fromString(row.getString("server_settings")),
      template = Permission.fromString(row.getString("template")),
      categoryContent = Permission.fromString(row.getString("category_content")),
      categoryProducts = Permission.fromString(row.getString("category_products")),
      productContent = Permission.fromString(row.getString("product_content")),
      productPrice = Permission.fromString(row.getString("product_price")),
      productWarehouse = Permission.fromString(row.getString("product_warehouse")),
      textPages = Permission.fromString(row.getString("text_pages")),
      apiKey = row.getString("API_KEY")
    )
  }

  private fun makeFullUserInformationJsonFields(row: Row): List<Pair<String, Any?>> {
    return listOf(
      "user_id" to row.getInteger("user_id"),
      "email" to row.getString("email"),
      "password" to row.getString("password"),
      "user_permissions" to row.getString("user_permissions"),
      "server_settings" to row.getString("server_settings"),
      "template" to row.getString("template"),
      "category_content" to row.getString("category_content"),
      "category_products" to row.getString("category_products"),
      "product_content" to row.getString("product_content"),
      "product_price" to row.getString("product_price"),
      "product_warehouse" to row.getString("product_warehouse"),
      "text_pages" to row.getString("text_pages"),
      "api_key" to row.getString("API_KEY")
    )
  }

  private fun makeFullUserObject(row: Row): FullUser {
    return FullUser(
      user = makeUserObject(row),
      backendPermissions = makeBackendPermissionsObject(row)
    )
  }

  private fun makeUserTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val userTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("email"),
        hashPassword(body.getString("password")),
          body.getInteger("user_id")
      )
    } else {
      Tuple.of(
        body.getString("email"),
        hashPassword(body.getString("password")),
      )
    }

    return userTuple
  }

  private fun makeBackendPermissionsTuple(body: JsonObject, isPutRequest: Boolean): Tuple {
    val backendPermissionsTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.getString("user_permissions"),
        body.getString("server_settings"),
        body.getString("template"),
        body.getString("category_content"),
        body.getString("category_products"),
        body.getString("product_content"),
        body.getString("product_price"),
        body.getString("product_warehouse"),
        body.getString("text_pages"),
        body.getString("API_KEY"),
        body.getInteger("user_id")
      )
    } else {
      Tuple.of(
        body.getInteger("user_id"),
        body.getString("user_permissions"),
        body.getString("server_settings"),
        body.getString("template"),
        body.getString("category_content"),
        body.getString("category_products"),
        body.getString("product_content"),
        body.getString("product_price"),
        body.getString("product_warehouse"),
        body.getString("text_pages"),
        body.getString("API_KEY"),
      )
    }

    return backendPermissionsTuple
  }

  private fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt(12))
  }
}
