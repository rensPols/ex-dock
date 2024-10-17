package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import org.mindrot.jbcrypt.BCrypt

class AccountJdbcVerticle: AbstractVerticle() {

  private lateinit var client: Pool

  private lateinit var eventBus: EventBus

  private val failedMessage = "failed"

  private val listDeliveryOptions = DeliveryOptions().setCodecName("ListCodec")
  private val userDeliveryOptions = DeliveryOptions().setCodecName("UserCodec")
  private val backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsCodec")
  private val fullUserInfoDeliveryOptions = DeliveryOptions().setCodecName("FullUserInformationCodec")

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
      val users: MutableList<User> = emptyList<User>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          rows.forEach { row ->
            users.add(makeUser(row))
          }
        }

        message.reply(users, listDeliveryOptions)
      }
    }
  }

  private fun getUserById() {
    val getUserByIdConsumer = eventBus.consumer<Int>("process.account.getUserById")
    getUserByIdConsumer.handler { message ->
      val userId = message.body()
      val query = "SELECT * FROM users WHERE user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeUser(rows.first()), userDeliveryOptions)
        } else {
          message.reply("No users found!")
        }
      }
    }
  }

  private fun createUser() {
    val createUserConsumer = eventBus.consumer<User>("process.account.createUser")
    createUserConsumer.handler { message ->
      val query = "INSERT INTO users (email, password) VALUES (?,?) RETURNING user_id AS UID"
      val rowsFuture = client
        .preparedQuery(query)
        .execute(makeUserTuple(message.body(), false))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val user: User = message.body()
        val lastInsertID: Row = res.property(JDBCPool.GENERATED_KEYS)
        user.userId = lastInsertID.getInteger(0)

        message.reply(user, userDeliveryOptions)
      }
    }
  }

  private fun updateUser() {
    val updateUserConsumer = eventBus.consumer<User>("process.account.updateUser")
    updateUserConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE users SET email = ?, password = ? WHERE user_id = ?"
      val userTuple = makeUserTuple(body, true)
      val rowsFuture = client.preparedQuery(query).execute(userTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply(body, userDeliveryOptions)
        } else {
          message.reply("Failed to update user")
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
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply("User deleted successfully")
        } else {
          message.reply("Failed to delete user")
        }
      }
    }
  }

  private fun getAllBackendPermissions() {
    val allBackendPermissionsDataConsumer = eventBus.consumer<String>("process.account.getAllBackendPermissions")
    allBackendPermissionsDataConsumer.handler { message ->
      val query = "SELECT * FROM backend_permissions"
      val rowsFuture = client.preparedQuery(query).execute()
      val backendPermissionsList: MutableList<BackendPermissions> = emptyList<BackendPermissions>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          rows.forEach { row ->
            backendPermissionsList.add(makeBackendPermissions(row))
          }
        }

        message.reply(backendPermissionsList, listDeliveryOptions)
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeBackendPermissions(rows.first()), backendPermissionsDeliveryOptions)
        } else {
          message.reply("No backend permissions were found!")
        }
      }
    }
  }

  private fun createBackendPermissions() {
    val createBackendPermissionsConsumer =
      eventBus.consumer<BackendPermissions>("process.account.createBackendPermissions")
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
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply(message.body(), backendPermissionsDeliveryOptions)
        } else {
          message.reply("Failed to create backend permissions")
        }
      }
    }
  }

  private fun updateBackendPermissions() {
    val updateBackendPermissionsConsumer =
      eventBus.consumer<BackendPermissions>("process.account.updateBackendPermissions")
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
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply(body, backendPermissionsDeliveryOptions)
        } else {
          message.reply("Failed to update backend permissions")
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
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          message.reply("Backend permissions deleted successfully")
        } else {
          message.reply("Failed to delete backend permissions")
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
      val fullUserInformations: MutableList<FullUserInformation> = emptyList<FullUserInformation>().toMutableList()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          rows.forEach { row ->
            fullUserInformations.add(makeFullUserInformation(row))
          }
        }

        message.reply(fullUserInformations, listDeliveryOptions)
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.reply(failedMessage)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeFullUserInformation(rows.first()), fullUserInfoDeliveryOptions)
          } else {
          message.reply("No users found!")
        }
      }
    }
  }

  private fun makeUser(row: Row): User {
    return User(
      row.getInteger("user_id"),
      row.getString("email"),
      row.getString("password")
    )
  }

  private fun makeBackendPermissions(row: Row): BackendPermissions {
    return BackendPermissions(
      row.getInteger("user_id"),
      convertStringToPermission(row.getString("user_permissions")),
      convertStringToPermission(row.getString("server_settings")),
      convertStringToPermission(row.getString("template")),
      convertStringToPermission(row.getString("category_content")),
      convertStringToPermission(row.getString("category_products")),
      convertStringToPermission(row.getString("product_content")),
      convertStringToPermission(row.getString("product_price")),
      convertStringToPermission(row.getString("product_warehouse")),
      convertStringToPermission(row.getString("text_pages")),
      row.getString("API_KEY")
    )
  }

  private fun makeFullUserInformation(row: Row): FullUserInformation {
    return FullUserInformation(
      makeUser(row),
      makeBackendPermissions(row)
    )
  }

  private fun makeUserTuple(body: User, isPutRequest: Boolean): Tuple {
    val userTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        body.email,
        hashPassword(body.password),
        body.userId
      )
    } else {
      Tuple.of(
        body.email,
        hashPassword(body.password),
      )
    }

    return userTuple
  }

  private fun makeBackendPermissionsTuple(body: BackendPermissions, isPutRequest: Boolean): Tuple {
    val backendPermissionsTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        convertPermissionToString(body.userPermissions),
        convertPermissionToString(body.serverSettings),
        convertPermissionToString(body.template),
        convertPermissionToString(body.categoryContent),
        convertPermissionToString(body.categoryProducts),
        convertPermissionToString(body.productContent),
        convertPermissionToString(body.productPrice),
        convertPermissionToString(body.productWarehouse),
        convertPermissionToString(body.textPages),
        body.apiKey,
        body.userId
      )
    } else {
      Tuple.of(
        body.userId,
        convertPermissionToString(body.userPermissions),
        convertPermissionToString(body.serverSettings),
        convertPermissionToString(body.template),
        convertPermissionToString(body.categoryContent),
        convertPermissionToString(body.categoryProducts),
        convertPermissionToString(body.productContent),
        convertPermissionToString(body.productPrice),
        convertPermissionToString(body.productWarehouse),
        convertPermissionToString(body.textPages),
        body.apiKey
      )
    }

    return backendPermissionsTuple
  }

  private fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt(12))
  }

  private fun convertStringToPermission(name: String): Permissions {
    when (name) {
      "read" -> return Permissions.READ
      "write" -> return Permissions.WRITE
      "read-write" -> return Permissions.READ_WRITE
    }

    return Permissions.NONE
  }

  private fun convertPermissionToString(permissions: Permissions): String {
    return when (permissions) {
      Permissions.READ -> "read"
      Permissions.WRITE -> "write"
      Permissions.READ_WRITE -> "read-write"
      Permissions.NONE -> "none"
    }
  }
}
