package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.codec.GenericCodec
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.coAwait
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mindrot.jbcrypt.BCrypt
import kotlin.reflect.typeOf

@ExtendWith(VertxExtension::class)
class AccountJdbcVerticleTest {
  private val userDeliveryOptions = DeliveryOptions().setCodecName("UserCodec")
  private val backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName("BackendPermissionsCodec")
  private var backendPermissionsList: MutableList<BackendPermissions> = emptyList<BackendPermissions>().toMutableList()

  @Test
  fun testUserData(vertx: Vertx, testContext: VertxTestContext) {
    val eventBus: EventBus = vertx.eventBus()
      .registerCodec(GenericCodec(User::class.java))
      .registerCodec(GenericCodec(BackendPermissions::class.java))
      .registerCodec(GenericCodec(FullUserInformation::class.java))
      .registerCodec(GenericCodec(MutableList::class.java))
    var newUserId: Int = -1

    val testUser = User(
      userId = newUserId,
      email = "test@example.com",
      password = "password"
    )

    var updateUser: User

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
        eventBus.request<User>("process.account.createUser", testUser, userDeliveryOptions).onFailure {
          testContext.failNow(it)
        }.onComplete { createMsg ->
          assert(createMsg.succeeded())
          testUser.userId = createMsg.result().body().userId
          newUserId = testUser.userId
          updateUser= User(
            userId = newUserId,
            email = "test@example.com",
            password = "updatedPassword"
          )

          assertEquals(createMsg.result().body(), testUser)

          eventBus.request<MutableList<User>>("process.account.getAllUsers", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllMsg ->
            assert(getAllMsg.succeeded())
            assertNotEquals(emptyList<User>().toMutableList(), getAllMsg.result().body())
            assertTrue(
              BCrypt.checkpw("password",
              getAllMsg.result().body()[0].password)
            )

            eventBus.request<User>("process.account.getUserById", newUserId).onFailure {
              testContext.failNow(it)
            }.onComplete { getMsg ->
              assert(getMsg.succeeded())
              val user: User = getMsg.result().body()
              assertEquals(testUser.userId, user.userId)
              assertEquals(testUser.email, user.email)
              assertTrue(
                BCrypt.checkpw("password",
                  user.password)
              )

              eventBus.request<User>("process.account.updateUser", updateUser, userDeliveryOptions).onFailure {
                testContext.failNow(it)
              }.onComplete { updateMsg ->
                assert(updateMsg.succeeded())
                assertEquals(updateMsg.result().body(), updateUser)

                eventBus.request<User>("process.account.getUserById", newUserId).onFailure {
                  testContext.failNow(it)
                }.onComplete { getUpdatedUserMsg ->
                  assert(getUpdatedUserMsg.succeeded())
                  val updatedUser: User = getUpdatedUserMsg.result().body()
                  assertEquals(updateUser.userId, updatedUser.userId)
                  assertEquals(updateUser.email, updatedUser.email)
                  assertTrue(
                    BCrypt.checkpw("updatedPassword",
                      updatedUser.password)
                  )

                  eventBus.request<String>("process.account.deleteUser", newUserId).onFailure {
                    testContext.failNow(it)
                  }.onComplete { deleteMsg ->
                    assert(deleteMsg.succeeded())
                    assertEquals(deleteMsg.result().body(), "User deleted successfully")

                    eventBus.request<MutableList<User>>("process.account.getAllUsers", "").onFailure {
                      testContext.failNow(it)
                    }.onComplete { emptyMsg ->
                      assert(emptyMsg.succeeded())
                      assertEquals(emptyMsg.result().body(), emptyList<User>().toMutableList())

                      testContext.completeNow()
                    }
                  }
                }
              }
            }
          }
        }
    })
  }

  @Test
  fun testBackendPermissions(vertx: Vertx, testContext: VertxTestContext) {
    val eventBus = vertx.eventBus()
      .registerCodec(GenericCodec(User::class.java))
      .registerCodec(GenericCodec(BackendPermissions::class.java))
      .registerCodec(GenericCodec(FullUserInformation::class.java))
      .registerCodec(GenericCodec(MutableList::class.java))
    val permissionId = -1

    var permissionResult: BackendPermissions
    backendPermissionsList = emptyList<BackendPermissions>().toMutableList()

    var testUser = User(
      userId = permissionId,
      email = "test@example.com",
      password = BCrypt.hashpw("password", BCrypt.gensalt())
    )

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
      eventBus.request<User>("process.account.createUser", testUser, userDeliveryOptions).onFailure {
        testContext.failNow(it)
      }.onComplete { createUserMsg ->
        assert(createUserMsg.succeeded())
        testUser = createUserMsg.result().body()

        permissionResult = BackendPermissions(
          userId = testUser.userId,
          userPermissions = convertStringToPermission("none"),
          serverSettings = convertStringToPermission("none"),
          template = convertStringToPermission("none"),
          categoryContent = convertStringToPermission("none"),
          categoryProducts = convertStringToPermission("none"),
          productContent = convertStringToPermission("none"),
          productPrice = convertStringToPermission("none"),
          productWarehouse = convertStringToPermission("none"),
          textPages = convertStringToPermission("none"),
          apiKey = null
        )
        backendPermissionsList.add(permissionResult)

        assertEquals(createUserMsg.result().body(), testUser)
        eventBus.request<BackendPermissions>("process.account.createBackendPermissions", permissionResult, backendPermissionsDeliveryOptions).onFailure {
          testContext.failNow(it)
        }.onComplete { createMsg ->
          assert(createMsg.succeeded())
          assertEquals(permissionResult, createMsg.result().body())

          eventBus.request<MutableList<BackendPermissions>>("process.account.getAllBackendPermissions", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllMsg ->
            assert(getAllMsg.succeeded())
            assertEquals(backendPermissionsList, getAllMsg.result().body())

            eventBus.request<BackendPermissions>(
              "process.account.getBackendPermissionsByUserId",
              permissionResult.userId
            )
              .onFailure {
                testContext.failNow(it)
              }.onComplete { getMsg ->
                assert(getMsg.succeeded())
                val permission: BackendPermissions = getMsg.result().body()
                assertEquals(permissionResult, permission)

                permissionResult = BackendPermissions(
                  userId = permissionResult.userId,
                  userPermissions = convertStringToPermission("read"),
                  serverSettings = convertStringToPermission("read"),
                  template = convertStringToPermission("read"),
                  categoryContent = convertStringToPermission("read"),
                  categoryProducts = convertStringToPermission("read"),
                  productContent = convertStringToPermission("read"),
                  productPrice = convertStringToPermission("read"),
                  productWarehouse = convertStringToPermission("read"),
                  textPages = convertStringToPermission("read"),
                  apiKey = null
                )

                eventBus.request<BackendPermissions>("process.account.updateBackendPermissions", permissionResult, backendPermissionsDeliveryOptions).onFailure {
                  testContext.failNow(it)
                }.onComplete { updateMsg ->
                  assert(updateMsg.succeeded())
                  assertEquals(updateMsg.result().body(), permissionResult)

                  eventBus.request<BackendPermissions>(
                    "process.account.getBackendPermissionsByUserId",
                    permissionResult.userId
                  )
                    .onFailure {
                      testContext.failNow(it)
                    }.onComplete { getUpdatedMsg ->
                      assert(getUpdatedMsg.succeeded())
                      val updatedPermission: BackendPermissions = getUpdatedMsg.result().body()
                      assertEquals(permissionResult, updatedPermission)

                      eventBus.request<String>(
                        "process.account.deleteBackendPermissions",
                        permissionResult.userId
                      )
                        .onFailure {
                          testContext.failNow(it)
                        }.onComplete { deleteMsg ->
                          assert(deleteMsg.succeeded())
                          assertEquals(deleteMsg.result().body(), "Backend permissions deleted successfully")

                          eventBus.request<MutableList<BackendPermissions>>("process.account.getAllBackendPermissions", "").onFailure {
                            testContext.failNow(it)
                          }.onComplete { emptyMsg ->
                            assert(emptyMsg.succeeded())
                            assertEquals(emptyMsg.result().body(), emptyList<BackendPermissions>().toMutableList())

                            eventBus.request<String>("process.account.deleteUser", permissionId).onFailure {
                              testContext.failNow(it)
                            }.onComplete {
                              testContext.completeNow()
                            }
                          }
                        }
                    }
                }
              }
          }
        }
      }
    })
  }

  @Test
  fun testFullUserInformation(vertx: Vertx, testContext: VertxTestContext) {
    val eventBus = vertx.eventBus()
      .registerCodec(GenericCodec(User::class.java))
      .registerCodec(GenericCodec(BackendPermissions::class.java))
      .registerCodec(GenericCodec(FullUserInformation::class.java))
      .registerCodec(GenericCodec(MutableList::class.java))
    var userId = -1
    val fullUserInformationList: MutableList<FullUserInformation> = emptyList<FullUserInformation>().toMutableList()

    var testUser = User(
      userId = userId,
      email = "test@example.com",
      password = BCrypt.hashpw("password", BCrypt.gensalt())
    )

    var allInfoResult: FullUserInformation

    var testPermission: BackendPermissions

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
      eventBus.request<User>("process.account.createUser", testUser, userDeliveryOptions).onFailure {
        testContext.failNow(it)
      }.onComplete{ createUserMsg ->
        assert(createUserMsg.succeeded())
        testUser = createUserMsg.result().body()
        userId = testUser.userId

        testPermission = BackendPermissions(
          userId = userId,
          userPermissions = convertStringToPermission("read"),
          serverSettings = convertStringToPermission("read"),
          template = convertStringToPermission("read"),
          categoryContent = convertStringToPermission("read"),
          categoryProducts = convertStringToPermission("read"),
          productContent = convertStringToPermission("read"),
          productPrice = convertStringToPermission("read"),
          productWarehouse = convertStringToPermission("read"),
          textPages = convertStringToPermission("read"),
          apiKey = null
        )

        allInfoResult = FullUserInformation(
          testUser,
          testPermission
        )
        allInfoResult.user.password = ""

        fullUserInformationList.add(allInfoResult)

        assertEquals(createUserMsg.result().body(), testUser)

        eventBus.request<BackendPermissions>("process.account.createBackendPermissions", testPermission, backendPermissionsDeliveryOptions).onFailure {
          testContext.failNow(it)
        }.onComplete { createPermissionMsg ->
          assert(createPermissionMsg.succeeded())
          assertEquals(testPermission, createPermissionMsg.result().body())

          eventBus.request<MutableList<FullUserInformation>>("process.account.getAllFullUserInfo", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllFullMsg ->
            val fullBody = getAllFullMsg.result().body()
            fullBody[0].user.password = ""
            assertEquals(fullBody, fullUserInformationList)

            eventBus.request<FullUserInformation>("process.account.getFullUserInformationByUserId", userId).onFailure {
              testContext.failNow(it)
            }.onComplete { fullInfoByIdMsg ->
              val fullBodyById = fullInfoByIdMsg.result().body()
              fullBodyById.user.password = ""
              assertEquals(fullBodyById, allInfoResult)

              eventBus.request<String>("process.account.deleteBackendPermissions", userId).onFailure {
                testContext.failNow(it)
              }.onComplete { deletePermissionMsg ->
                assert(deletePermissionMsg.succeeded())
                assertEquals(deletePermissionMsg.result().body(), "Backend permissions deleted successfully")

                eventBus.request<String>("process.account.deleteUser", userId).onFailure {
                  testContext.failNow(it)
                }.onComplete {
                  testContext.completeNow()
                }
              }
            }
          }
        }
      }
    })
  }

  private fun convertStringToPermission(name: String): Permissions {
    when (name) {
      "read" -> return Permissions.READ
      "write" -> return Permissions.WRITE
      "read-write" -> return Permissions.READ_WRITE
    }

    return Permissions.NONE
  }
}
