package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.connection.getConnection
import com.ex_dock.ex_dock.frontend.cache.setCacheFlag
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple
import org.mindrot.jbcrypt.BCrypt

class AccountJdbcVerticle: AbstractVerticle() {

  private lateinit var client: Pool

  private lateinit var eventBus: EventBus


  companion object {
    const val FAILED = "failed"
    const val NO_USER = "User does not exist"
    const val USER_DELETED_SUCCESS = "User deleted successfully"
    const val CACHE_ADDRESS = "accounts"

    const val BACKEND_PERMISSION_CREATION_FAILED = "Failed to create backend permissions"
    const val BACKEND_PERMISSION_UPDATE_FAILED = "Failed to update backend permissions"
    const val BACKEND_PERMISSION_DELETED = "Backend Permissions were successfully deleted!"
    const val BACKEND_PERMISSION_DELETE_FAILED = "Failed to delete backend permissions"
    const val NO_BACKEND_PERMISSION = "No backend permissions found"
  }

  private val userDeliveryOptions = DeliveryOptions().setCodecName("UserCodec")
  private val userListDeliveryOptions = DeliveryOptions().setCodecName("UserListCodec")
  private val backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsCodec")
  private val backendPermissionsListDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsListCodec")
  private val fullUserDeliveryOptions = DeliveryOptions().setCodecName("FullUserCodec")
  private val listDeliveryOptions = DeliveryOptions().setCodecName("ListCodec")

  override fun start() {
    client = getConnection(vertx)
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
    getAllFullUser()
    getFullUserByEmail()
    getFullUserByUserId()
  }

  private fun getAllUsers() {
    val allUserDataConsumer = eventBus.consumer<String>("process.account.getAllUsers")
    allUserDataConsumer.handler { message ->
      val query = "SELECT * FROM users"
      val rowsFuture = client.preparedQuery(query).execute()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeUserObject(rows.first()), userDeliveryOptions)
        } else {
          message.fail(404, NO_USER)
        }
      }
    }
  }

  private fun createUser() {
    val createUserConsumer = eventBus.consumer<UserCreation>("process.account.createUser")
    createUserConsumer.handler { message ->
      val query = "INSERT INTO users (email, password) VALUES (?,?) RETURNING user_id AS UID"
      val rowsFuture = client
        .preparedQuery(query)
        .execute(makeUserCreationTuple(message.body()))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(400, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val userCreation: UserCreation = message.body()
        val lastInsertID: Row = res.property(JDBCPool.GENERATED_KEYS)
        val user = User(
          userId = lastInsertID.getInteger(0),
          email = userCreation.email,
          password = userCreation.password
        )

        setCacheFlag(eventBus, CACHE_ADDRESS)
        message.reply(user, userDeliveryOptions)
      }
    }
  }

  private fun updateUser() {
    val updateUserConsumer = eventBus.consumer<User>("process.account.updateUser")
    updateUserConsumer.handler { message ->
      val body = message.body()
      val query = "UPDATE users SET email = ?, password = ? WHERE user_id = ?"
      val userTuple = makeUserTuple(body)
      val rowsFuture = client.preparedQuery(query).execute(userTuple)

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          setCacheFlag(eventBus, CACHE_ADDRESS)
          message.reply(body, userDeliveryOptions)
        } else {
          message.fail(404, NO_USER)
        }
      }
    }
  }

  private fun deleteUser() {
    val deleteUserConsumer = eventBus.consumer<Int>("process.account.deleteUser")
    deleteUserConsumer.handler { message ->
      val userId = message.body()
      val userQuery = "DELETE FROM users WHERE user_id =?"
      val permissionsQuery = "DELETE FROM backend_permissions WHERE user_id =?"
      lateinit var userRowsFuture: Future<RowSet<Row>>
      client.withTransaction { transactionClient ->
        userRowsFuture = transactionClient.preparedQuery(userQuery).execute(Tuple.of(userId))
        transactionClient.preparedQuery(permissionsQuery).execute(Tuple.of(userId))
      }.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }.onComplete {
        userRowsFuture.onFailure { res ->
          println("Failed to execute query: $res")
          message.fail(500, FAILED)
        }

        userRowsFuture.onSuccess { res ->
          if (res.value().rowCount() > 0) {
            setCacheFlag(eventBus, CACHE_ADDRESS)
            message.reply(USER_DELETED_SUCCESS)
          } else {
            message.fail(404, NO_USER)
          }
        }
      }
    }
  }

  private fun getAllBackendPermissions() {
    val allBackendPermissionsDataConsumer = eventBus.consumer<String>("process.account.getAllBackendPermissions")
    allBackendPermissionsDataConsumer.handler { message ->
      val query = "SELECT * FROM backend_permissions"
      val rowsFuture = client.preparedQuery(query).execute()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
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

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeBackendPermissionsObject(rows.first()), backendPermissionsDeliveryOptions)
        } else {
          message.fail(404, NO_BACKEND_PERMISSION)
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
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          setCacheFlag(eventBus, CACHE_ADDRESS)
          message.reply(message.body(), backendPermissionsDeliveryOptions)
        } else {
          message.fail(400, BACKEND_PERMISSION_CREATION_FAILED)
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
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          setCacheFlag(eventBus, CACHE_ADDRESS)
          message.reply(body, backendPermissionsDeliveryOptions)
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
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        if (res.value().rowCount() > 0) {
          setCacheFlag(eventBus, CACHE_ADDRESS)
          message.reply(BACKEND_PERMISSION_DELETED)
        } else {
          message.fail(400, BACKEND_PERMISSION_DELETE_FAILED)
        }
      }
    }
  }

  private fun getAllFullUser() {
    val getAllFullUserInfoConsumer = eventBus.consumer<String>("process.account.getAllFullUserInfo")
    getAllFullUserInfoConsumer.handler { message ->
      val query = "SELECT u.user_id, u.email, u.password, bp.user_permissions, bp.server_settings, " +
        "bp.template, bp.category_content, bp.category_products, bp.product_content, bp.product_price, " +
        "bp.product_warehouse, bp.text_pages, bp.\"API_KEY\" FROM users u " +
        "JOIN backend_permissions bp ON u.user_id = bp.user_id"
      val rowsFuture = client.preparedQuery(query).execute()

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(rows.map { row -> makeFullUserObject(row) }, fullUserDeliveryOptions)
        } else {
          message.reply(emptyList<FullUser>(), fullUserDeliveryOptions)
        }
      }
    }
  }

  /**
   * Gets a full user by their unique username
   */
  private fun getFullUserByEmail() {
    val getFullUserInformationByEmailConsumer =
      eventBus.consumer<String>("process.account.getFullUserByEmail")
    getFullUserInformationByEmailConsumer.handler { message ->
      val email = message.body()
      val query = "SELECT u.user_id, u.email, u.password, bp.user_permissions, bp.server_settings, " +
        "bp.template, bp.category_content, bp.category_products, bp.product_content, bp.product_price, " +
        "bp.product_warehouse, bp.text_pages, bp.\"API_KEY\" FROM users u " +
        "JOIN backend_permissions bp ON u.user_id = bp.user_id WHERE u.email =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(email))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeFullUserObject(rows.first()), fullUserDeliveryOptions)
        } else {
          message.fail(404, NO_USER)
        }
      }
    }
  }

  private fun getFullUserByUserId() {
    val getFullUserInformationByUserIdConsumer =
      eventBus.consumer<Int>("process.account.getFullUserByUserId")
    getFullUserInformationByUserIdConsumer.handler { message ->
      val userId = message.body()
      val query = "SELECT u.user_id, u.email, u.password, bp.user_permissions, bp.server_settings, " +
        "bp.template, bp.category_content, bp.category_products, bp.product_content, bp.product_price, " +
        "bp.product_warehouse, bp.text_pages, bp.\"API_KEY\" FROM users u " +
        "LEFT JOIN backend_permissions bp ON u.user_id = bp.user_id WHERE u.user_id =?"
      val rowsFuture = client.preparedQuery(query).execute(Tuple.of(userId))

      rowsFuture.onFailure { res ->
        println("Failed to execute query: $res")
        message.fail(500, FAILED)
      }

      rowsFuture.onSuccess { res ->
        val rows = res.value()
        if (rows.size() > 0) {
          message.reply(makeFullUserObject(rows.first()), fullUserDeliveryOptions)
          } else {
          message.fail(404, NO_USER)
        }
      }
    }
  }

  private fun makeUserObject(row: Row): User {
    return User(
      userId = row.getInteger("user_id"),
      email = row.getString("email"),
      password = row.getString("password")
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

  private fun makeFullUserObject(row: Row): FullUser {
    return FullUser(
      user = makeUserObject(row),
      backendPermissions = makeBackendPermissionsObject(row)
    )
  }


  private fun makeUserTuple(body: User): Tuple {
    val userTuple: Tuple = Tuple.of(body.email, hashPassword(body.password), body.userId)

    return userTuple
  }


  private fun makeUserCreationTuple(body: UserCreation): Tuple {
    val userTuple: Tuple = Tuple.of(body.email, hashPassword(body.password))

    return userTuple
  }

  private fun makeBackendPermissionsTuple(body: BackendPermissions, isPutRequest: Boolean): Tuple {
    val backendPermissionsTuple: Tuple = if (isPutRequest) {
      Tuple.of(
        convertPermissionToString(body.userPermission),
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
        convertPermissionToString(body.userPermission),
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

  private fun convertPermissionToString(permission: Permission): String {
    return when (permission) {
      Permission.READ -> "read"
      Permission.WRITE -> "write"
      Permission.READ_WRITE -> "read-write"
      Permission.NONE -> "none"
    }
  }
}
