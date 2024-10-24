package com.ex_dock.ex_dock.database.account

import com.ex_dock.ex_dock.database.codec.GenericCodec
import com.ex_dock.ex_dock.database.codec.GenericListCodec
import com.ex_dock.ex_dock.helper.deployWorkerVerticleHelper
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mindrot.jbcrypt.BCrypt

@ExtendWith(VertxExtension::class)
class AccountJdbcVerticleTest {
  private val userCodec: MessageCodec<User, User> = GenericCodec(User::class.java)
  private val userCreationCodec: MessageCodec<UserCreation, UserCreation> = GenericCodec(UserCreation::class.java)
  private val backendPermissionsCodec: MessageCodec<BackendPermissions, BackendPermissions> =
    GenericCodec(BackendPermissions::class.java)
  private val fullUserCodec: MessageCodec<FullUser, FullUser> = GenericCodec(FullUser::class.java)
  private val userListCodec: MessageCodec<List<User>, List<User>> = GenericListCodec(User::class)
  private val backendPermissionsListCodec: MessageCodec<List<BackendPermissions>, List<BackendPermissions>> =
    GenericListCodec(BackendPermissions::class)
  private val fullUserListCodec: MessageCodec<List<FullUser>, List<FullUser>> = GenericListCodec(FullUser::class)

  private val userDeliveryOptions = DeliveryOptions().setCodecName(userCodec.name())
  private val userCreationDeliveryOptions = DeliveryOptions().setCodecName(userCreationCodec.name())
  private val backendPermissionsDeliveryOptions = DeliveryOptions().setCodecName(backendPermissionsCodec.name())
  private val fullUserDeliveryOptions = DeliveryOptions().setCodecName(fullUserCodec.name())
  private val userListDeliveryOptions = DeliveryOptions().setCodecName(userListCodec.name())
  private val backendPermissionsListDeliveryOptions = DeliveryOptions().setCodecName(backendPermissionsListCodec.name())
  private val fullUserListDeliveryOptions = DeliveryOptions().setCodecName(fullUserListCodec.name())

  private var backendPermissionsList: MutableList<BackendPermissions> = emptyList<BackendPermissions>().toMutableList()

  private lateinit var eventBus: EventBus

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    eventBus = vertx.eventBus()
      .registerCodec(userCodec)
      .registerCodec(userListCodec)
      .registerCodec(backendPermissionsCodec)
      .registerCodec(backendPermissionsListCodec)
      .registerCodec(fullUserCodec)
      .registerCodec(fullUserListCodec)

    deployWorkerVerticleHelper(
      vertx,
      AccountJdbcVerticle::class.qualifiedName.toString(), 5, 5
    ).onComplete {
      testContext.completeNow()
    }
  }

  @Test
  fun getAllUsersEmpty(vertx: Vertx, testContext: VertxTestContext) {
    val request = eventBus.request<String>("process.account.getAllUsers", "")

    request.onFailure { testContext.failNow(it) }
    request.onComplete { msg ->
      if (msg.failed()) testContext.failNow(msg.result().toString())
      var body: List<User> = msg.result().body() as List<User>
      if (body!= emptyList<User>()) testContext.failNow(
        "result is not equal to emptyList<User>()\nmsg.result().toString(): ${msg.result().body()}\n" +
            "msg.result()::class: ${msg.result()::class}\n" +
            "msg.result().body()::class: ${msg.result().body()::class}\n" +
            "body: $body\nbody::class: ${body::class}"
      )
      testContext.completeNow()
    }
  }

  @Test
  fun testUserData(vertx: Vertx, testContext: VertxTestContext) {
    val processAccountCreateUserCheckpoint = testContext.checkpoint()
    val processAccountGetAllUsersCheckpoint = testContext.checkpoint(2)
    val processAccountGetUserByIdCheckpoint = testContext.checkpoint(2)
    val processAccountUpdateUserCheckpoint = testContext.checkpoint()
    val processAccountDeleteUserCheckpoint = testContext.checkpoint()

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
        try {
          assert(createMsg.succeeded())
        } catch (e: Exception) {
          testContext.failNow(e)
        }

        testUser.userId = createMsg.result().body().userId
        newUserId = testUser.userId
        updateUser = User(
          userId = newUserId,
          email = "test@example.com",
          password = "updatedPassword"
        )

        try {
          assertEquals(createMsg.result().body(), testUser)
        } catch (e: Exception) {
          testContext.failNow(e)
        }

        processAccountCreateUserCheckpoint.flag()

        eventBus.request<List<User>>("process.account.getAllUsers", "").onFailure {
          testContext.failNow(it)
        }.onComplete { getAllMsg ->
          try {
            assert(getAllMsg.succeeded())
            assertNotEquals(emptyList<User>(), getAllMsg.result().body())
            assertTrue(
              BCrypt.checkpw(
                "password",
                getAllMsg.result().body()[0].password
              )
            )
          } catch (e: Exception) {
            testContext.failNow(e)
          }

          processAccountGetAllUsersCheckpoint.flag()

          eventBus.request<User>("process.account.getUserById", newUserId).onFailure {
            testContext.failNow(it)
          }.onComplete { getMsg ->
            try {
              assert(getMsg.succeeded())
            } catch (e: Exception) {
              testContext.failNow(e)
            }

            val user: User = getMsg.result().body()

            try {
              assertEquals(testUser.userId, user.userId)
              assertEquals(testUser.email, user.email)
              assertTrue(
                BCrypt.checkpw(
                  "password",
                  user.password
                )
              )
            } catch (e: Exception) {
              testContext.failNow(e)
            }

            processAccountGetUserByIdCheckpoint.flag()

            eventBus.request<User>("process.account.updateUser", updateUser, userDeliveryOptions).onFailure {
              testContext.failNow(it)
            }.onComplete { updateMsg ->
              try {
                assert(updateMsg.succeeded())
                assertEquals(updateMsg.result().body(), updateUser)
              } catch (e: Exception) {
                testContext.failNow(e)
              }

              processAccountUpdateUserCheckpoint.flag()

              eventBus.request<User>("process.account.getUserById", newUserId).onFailure {
                testContext.failNow(it)
              }.onComplete { getUpdatedUserMsg ->
                try {
                  assert(getUpdatedUserMsg.succeeded())
                } catch (e: Exception) {
                  testContext.failNow(e)
                }

                val updatedUser: User = getUpdatedUserMsg.result().body()

                try {
                  assertEquals(updateUser.userId, updatedUser.userId)
                  assertEquals(updateUser.email, updatedUser.email)
                  assertTrue(
                    BCrypt.checkpw(
                      "updatedPassword",
                      updatedUser.password
                    )
                  )
                } catch (e: Exception) {
                  testContext.failNow(e)
                }

                processAccountGetUserByIdCheckpoint.flag()

                eventBus.request<String>("process.account.deleteUser", newUserId).onFailure {
                  testContext.failNow(it)
                }.onComplete { deleteMsg ->
                  try {
                    assert(deleteMsg.succeeded())
                    assertEquals(deleteMsg.result().body(), "User deleted successfully")
                  } catch (e: Exception) {
                    testContext.failNow(e)
                  }

                  processAccountDeleteUserCheckpoint.flag()

                  eventBus.request<MutableList<User>>("process.account.getAllUsers", "").onFailure {
                    testContext.failNow(it)
                  }.onComplete { emptyMsg ->
                    try {
                      assert(emptyMsg.succeeded())
                      assertEquals(emptyMsg.result().body(), emptyList<User>().toMutableList())
                    } catch (e: Exception) {
                      testContext.failNow(e)
                    }

                    processAccountGetAllUsersCheckpoint.flag()
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
    var permissionId: Int?

    var permissionResult: BackendPermissions
    backendPermissionsList = emptyList<BackendPermissions>().toMutableList()

    var testUserCreation = UserCreation(
      email = "test@example.com",
      password = BCrypt.hashpw("password", BCrypt.gensalt())
    )
    lateinit var testUser: User

    vertx.deployVerticle(
      AccountJdbcVerticle(),
      testContext.succeeding {
        eventBus.request<User>("process.account.createUser", testUserCreation, userDeliveryOptions).onFailure {
          println("failure in testBackendPermissions on: process.account.createUser")
          testContext.failNow(it)
        }.onComplete { createUserMsg ->
          assert(createUserMsg.succeeded())
          testUser = createUserMsg.result().body()
          permissionId = testUser.userId

          permissionResult = BackendPermissions(
            userId = testUser.userId,
            userPermission = Permission.fromString("none"),
            serverSettings = Permission.fromString("none"),
            template = Permission.fromString("none"),
            categoryContent = Permission.fromString("none"),
            categoryProducts = Permission.fromString("none"),
            productContent = Permission.fromString("none"),
            productPrice = Permission.fromString("none"),
            productWarehouse = Permission.fromString("none"),
            textPages = Permission.fromString("none"),
            apiKey = null
          )
          backendPermissionsList.add(permissionResult)

          assertEquals(createUserMsg.result().body(), testUser)
          eventBus.request<BackendPermissions>(
            "process.account.createBackendPermissions",
            permissionResult,
            backendPermissionsDeliveryOptions
          ).onFailure {
            println("failure in testBackendPermissions on: process.account.createBackendPermissions")
            testContext.failNow(it)
          }.onComplete { createMsg ->
            assert(createMsg.succeeded())
            assertEquals(permissionResult, createMsg.result().body())

            eventBus.request<MutableList<BackendPermissions>>("process.account.getAllBackendPermissions", "")
              .onFailure {
                println("failure in testBackendPermissions on: process.account.getAllBackendPermissions")
                testContext.failNow(it)
              }.onComplete { getAllMsg ->
              assert(getAllMsg.succeeded())
              assertEquals(backendPermissionsList, getAllMsg.result().body())

              eventBus.request<BackendPermissions>(
                "process.account.getBackendPermissionsByUserId",
                permissionResult.userId
              )
                .onFailure {
                  println("failure in testBackendPermissions on: process.account.getBackendPermissionsByUserId")
                  testContext.failNow(it)
                }.onComplete { getMsg ->
                  assert(getMsg.succeeded())
                  val permission: BackendPermissions = getMsg.result().body()
                  assertEquals(permissionResult, permission)

                  permissionResult = BackendPermissions(
                    userId = permissionResult.userId,
                    userPermission = Permission.fromString("read"),
                    serverSettings = Permission.fromString("read"),
                    template = Permission.fromString("read"),
                    categoryContent = Permission.fromString("read"),
                    categoryProducts = Permission.fromString("read"),
                    productContent = Permission.fromString("read"),
                    productPrice = Permission.fromString("read"),
                    productWarehouse = Permission.fromString("read"),
                    textPages = Permission.fromString("read"),
                    apiKey = null
                  )

                  eventBus.request<BackendPermissions>(
                    "process.account.updateBackendPermissions",
                    permissionResult,
                    backendPermissionsDeliveryOptions
                  ).onFailure {
                    println("failure in testBackendPermissions on: process.account.updateBackendPermissions")
                    testContext.failNow(it)
                  }.onComplete { updateMsg ->
                    assert(updateMsg.succeeded())
                    assertEquals(updateMsg.result().body(), permissionResult)

                    eventBus.request<BackendPermissions>(
                      "process.account.getBackendPermissionsByUserId",
                      permissionResult.userId
                    )
                      .onFailure {
                        println("failure in testBackendPermissions on: process.account.getBackendPermissionsByUserId")
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
                            println("failure in testBackendPermissions on: process.account.deleteBackendPermissions")
                            testContext.failNow(it)
                          }.onComplete { deleteMsg ->
                            assert(deleteMsg.succeeded())
                            assertEquals(
                              deleteMsg.result().body(),
                              AccountJdbcVerticle.BACKEND_PERMISSION_DELETED
                            )

                            eventBus.request<MutableList<BackendPermissions>>(
                              "process.account.getAllBackendPermissions",
                              ""
                            ).onFailure {
                              println("failure in testBackendPermissions on: process.account.getAllBackendPermissions")
                              testContext.failNow(it)
                            }.onComplete { emptyMsg ->
                              assert(emptyMsg.succeeded())
                              assertEquals(emptyMsg.result().body(), emptyList<BackendPermissions>().toMutableList())

                              eventBus.request<String>("process.account.deleteUser", permissionId).onFailure {
                                println("failure in testBackendPermissions on: process.account.deleteUser")
                                // TODO: resolve error: User does not exist
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
      }
    )
  }

  @Test
  fun testFullUser(vertx: Vertx, testContext: VertxTestContext) {
    val processAccountCreateUserCheckpoint = testContext.checkpoint()
    val processAccountCreateBackendPermissionsCheckpoint = testContext.checkpoint()
    val processAccountGetAllFullUserInfoCheckpoint = testContext.checkpoint()
    val processAccountGetFullUserByUserIdCheckpoint = testContext.checkpoint()
    val processAccountDeleteBackendPermissionsCheckpoint = testContext.checkpoint()
    val processAccountDeleteUserCheckpoint = testContext.checkpoint()

    var userId = -1
    val FullUserList: MutableList<FullUser> = emptyList<FullUser>().toMutableList()

    var testUser = User(
      userId = userId,
      email = "test@example.com",
      password = BCrypt.hashpw("password", BCrypt.gensalt())
    )

    var allInfoResult: FullUser

    var testPermission: BackendPermissions

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
      eventBus.request<User>("process.account.createUser", testUser, userDeliveryOptions).onFailure {
        testContext.failNow(it)
      }.onComplete { createUserMsg ->
        assert(createUserMsg.succeeded())

        processAccountCreateUserCheckpoint.flag()

        testUser = createUserMsg.result().body()
        userId = testUser.userId

        testPermission = BackendPermissions(
          userId = userId,
          userPermission = Permission.fromString("read"),
          serverSettings = Permission.fromString("read"),
          template = Permission.fromString("read"),
          categoryContent = Permission.fromString("read"),
          categoryProducts = Permission.fromString("read"),
          productContent = Permission.fromString("read"),
          productPrice = Permission.fromString("read"),
          productWarehouse = Permission.fromString("read"),
          textPages = Permission.fromString("read"),
          apiKey = null
        )

        allInfoResult = FullUser(
          testUser,
          testPermission
        )
        allInfoResult.user.password = ""

        FullUserList.add(allInfoResult)

        assertEquals(createUserMsg.result().body(), testUser)

        eventBus.request<BackendPermissions>(
          "process.account.createBackendPermissions",
          testPermission,
          backendPermissionsDeliveryOptions
        ).onFailure {
          testContext.failNow(it)
        }.onComplete { createPermissionMsg ->
          assert(createPermissionMsg.succeeded())
          assertEquals(testPermission, createPermissionMsg.result().body())

          processAccountCreateBackendPermissionsCheckpoint.flag()

          eventBus.request<MutableList<FullUser>>("process.account.getAllFullUserInfo", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllFullMsg ->
            val fullBody = getAllFullMsg.result().body()
            fullBody[0].user.password = ""
            assertEquals(fullBody, FullUserList)

            processAccountGetAllFullUserInfoCheckpoint.flag()

            eventBus.request<FullUser>("process.account.getFullUserByUserId", userId).onFailure {
              testContext.failNow(it)
            }.onComplete { fullInfoByIdMsg ->
              val fullBodyById = fullInfoByIdMsg.result().body()
              fullBodyById.user.password = ""
              assertEquals(fullBodyById, allInfoResult)

              processAccountGetFullUserByUserIdCheckpoint.flag()

              eventBus.request<String>("process.account.deleteBackendPermissions", userId).onFailure {
                testContext.failNow(it)
              }.onComplete { deletePermissionMsg ->
                assert(deletePermissionMsg.succeeded())
                assertEquals(deletePermissionMsg.result().body(), AccountJdbcVerticle.BACKEND_PERMISSION_DELETED)

                processAccountDeleteBackendPermissionsCheckpoint.flag()

                eventBus.request<String>("process.account.deleteUser", userId).onFailure {
                  testContext.failNow(it)
                }.onComplete {
                  processAccountDeleteUserCheckpoint.flag()
                }
              }
            }
          }
        }
      }
    })
  }
}
