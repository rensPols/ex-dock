package com.ex_dock.ex_dock.database.account

import io.vertx.core.Vertx
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

  @Test
  fun testUserData(vertx: Vertx, testContext: VertxTestContext) {
    val eventBus: EventBus = vertx.eventBus()
    val createUser = testContext.checkpoint()
    val getAllUsers = testContext.checkpoint()
    val getUserById = testContext.checkpoint()
    val updateUser = testContext.checkpoint()
    val deleteUser = testContext.checkpoint()
    var newUserId: Int = -1

    var testUserJson: JsonObject = json {
      obj(
        "user_id" to newUserId,
        "email" to "test@example.com",
        "password" to "password",
      )
    }

    var updateUserJson: JsonObject

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
        eventBus.request<Int>("process.account.createUser", testUserJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createMsg ->
          assert(createMsg.succeeded())
          assertEquals(createMsg.result().body()::class.simpleName, "Int")
          newUserId = createMsg.result().body()

          testUserJson = json {
            obj(
              "user_id" to newUserId,
              "email" to "test@example.com",
              "password" to "efc13eb549289c919d235c7cc325ae4c3bfc2dabc14c125dea6afefbbc8f15f2",
            )
          }
          updateUserJson = json {
            obj(
              "user_id" to newUserId,
              "email" to "updated@example.com",
              "password" to "updatedPassword",
            )
          }

          createUser.flag()

          eventBus.request<JsonObject>("process.account.getAllUsers", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllMsg ->
            assert(getAllMsg.succeeded())
            assertNotEquals(json { obj("user" to "{}") }, getAllMsg.result().body())
            assertTrue(
              BCrypt.checkpw("password",
              getAllMsg.result().body().getJsonArray("users").getJsonObject(0).getString("password"))
            )
            getAllUsers.flag()

            eventBus.request<JsonObject>("process.account.getUserById", newUserId).onFailure {
              testContext.failNow(it)
            }.onComplete { getMsg ->
              assert(getMsg.succeeded())
              val user: JsonObject = getMsg.result().body()
              assertEquals(testUserJson.getInteger("user_id"), user.getInteger("user_id"))
              assertEquals(testUserJson.getString("email"), user.getString("email"))
              assertTrue(
                BCrypt.checkpw("password",
                  user.getString("password"))
              )
              getUserById.flag()

              eventBus.request<JsonObject>("process.account.updateUser", updateUserJson).onFailure {
                testContext.failNow(it)
              }.onComplete { updateMsg ->
                assert(updateMsg.succeeded())
                assertEquals(updateMsg.result().body(), "User updated successfully")

                eventBus.request<JsonObject>("process.account.getUserById", newUserId).onFailure {
                  testContext.failNow(it)
                }.onComplete { getUpdatedUserMsg ->
                  assert(getUpdatedUserMsg.succeeded())
                  val updatedUser: JsonObject = getUpdatedUserMsg.result().body()
                  assertEquals(updateUserJson.getInteger("user_id"), updatedUser.getInteger("user_id"))
                  assertEquals(updateUserJson.getString("email"), updatedUser.getString("email"))
                  assertTrue(
                    BCrypt.checkpw("updatedPassword",
                      updatedUser.getString("password"))
                  )
                  updateUser.flag()

                  eventBus.request<JsonObject>("process.account.deleteUser", newUserId).onFailure {
                    testContext.failNow(it)
                  }.onComplete { deleteMsg ->
                    assert(deleteMsg.succeeded())
                    assertEquals(deleteMsg.result().body(), "User deleted successfully")

                    eventBus.request<JsonObject>("process.account.getAllUsers", "").onFailure {
                      testContext.failNow(it)
                    }.onComplete { emptyMsg ->
                      assert(emptyMsg.succeeded())
                      assertEquals(emptyMsg.result().body(), json { obj("users" to "{}") })
                      deleteUser.flag()
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
    val createPermission = testContext.checkpoint()
    val getAllPermissions = testContext.checkpoint()
    val getPermissionById = testContext.checkpoint()
    val updatePermission = testContext.checkpoint()
    val deletePermission = testContext.checkpoint()
    var permissionId = -1

    var permissionResult: JsonObject

    val testUserJson: JsonObject = json {
      obj(
        "user_id" to permissionId,
        "email" to "test@example.com",
        "password" to "password",
      )
    }

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
      eventBus.request<Int>("process.account.createUser", testUserJson).onFailure {
        testContext.failNow(it)
      }.onComplete { createUserMsg ->
        assert(createUserMsg.succeeded())
        assertEquals(createUserMsg.result().body()::class.simpleName, "Int")
        permissionId = createUserMsg.result().body()

        permissionResult = json {
          obj(
            "user_id" to permissionId,
            "user_permissions" to "none",
            "server_settings" to "none",
            "template" to "none",
            "category_content" to "none",
            "category_products" to "none",
            "product_content" to "none",
            "product_price" to "none",
            "product_warehouse" to "none",
            "text_pages" to "none",
            "api_key" to null
          )
        }
        eventBus.request<String>("process.account.createBackendPermissions", permissionResult).onFailure {
          testContext.failNow(it)
        }.onComplete { createMsg ->
          assert(createMsg.succeeded())

          createPermission.flag()

          eventBus.request<JsonObject>("process.account.getAllBackendPermissions", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllMsg ->
            assert(getAllMsg.succeeded())
            assertEquals(json {
              obj(
                "backend_permissions" to listOf(permissionResult)
              )
            }, getAllMsg.result().body())
            getAllPermissions.flag()

            eventBus.request<JsonObject>(
              "process.account.getBackendPermissionsByUserId",
              permissionResult.getInteger("user_id")
            )
              .onFailure {
                testContext.failNow(it)
              }.onComplete { getMsg ->
                assert(getMsg.succeeded())
                val permission: JsonObject = getMsg.result().body()
                assertEquals(permissionResult, permission)
                getPermissionById.flag()

                permissionResult = json {
                  obj(
                    "user_id" to permissionId,
                    "user_permissions" to "read",
                    "server_settings" to "read",
                    "template" to "read",
                    "category_content" to "read",
                    "category_products" to "read",
                    "product_content" to "read",
                    "product_price" to "read",
                    "product_warehouse" to "read",
                    "text_pages" to "read",
                    "api_key" to null
                  )
                }

                eventBus.request<JsonObject>("process.account.updateBackendPermissions", permissionResult).onFailure {
                  testContext.failNow(it)
                }.onComplete { updateMsg ->
                  assert(updateMsg.succeeded())
                  assertEquals(updateMsg.result().body(), "Backend permissions updated successfully")

                  eventBus.request<JsonObject>(
                    "process.account.getBackendPermissionsByUserId",
                    permissionResult.getInteger("user_id")
                  )
                    .onFailure {
                      testContext.failNow(it)
                    }.onComplete { getUpdatedMsg ->
                      assert(getUpdatedMsg.succeeded())
                      val updatedPermission: JsonObject = getUpdatedMsg.result().body()
                      assertEquals(permissionResult, updatedPermission)
                      updatePermission.flag()

                      eventBus.request<JsonObject>(
                        "process.account.deleteBackendPermissions",
                        permissionResult.getInteger("user_id")
                      )
                        .onFailure {
                          testContext.failNow(it)
                        }.onComplete { deleteMsg ->
                          assert(deleteMsg.succeeded())
                          assertEquals(deleteMsg.result().body(), "Backend permissions deleted successfully")

                          eventBus.request<JsonObject>("process.account.getAllBackendPermissions", "").onFailure {
                            testContext.failNow(it)
                          }.onComplete { emptyMsg ->
                            assert(emptyMsg.succeeded())
                            assertEquals(emptyMsg.result().body(), json { obj("backend_permissions" to "{}") })

                            eventBus.request<String>("process.account.deleteUser", permissionId).onFailure {
                              testContext.failNow(it)
                            }.onComplete {
                              deletePermission.flag()
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
    val getAllDataCheckpoint = testContext.checkpoint()
    val getDataById = testContext.checkpoint()
    var userId = -1

    val testUserJson: JsonObject = json {
      obj(
        "user_id" to userId,
        "email" to "test@example.com",
        "password" to "password",
      )
    }

    var allInfoResult: JsonObject

    var testPermissionJson: JsonObject

    vertx.deployVerticle(AccountJdbcVerticle(), testContext.succeeding {
      eventBus.request<Int>("process.account.createUser", testUserJson).onFailure {
        testContext.failNow(it)
      }.onComplete{ createUserMsg ->
        assert(createUserMsg.succeeded())
        assertEquals(createUserMsg.result().body()::class.simpleName, "Int")
        userId = createUserMsg.result().body()

        allInfoResult = json {
          obj(
            "user_id" to userId,
            "email" to "test@example.com",
            "user_permissions" to "none",
            "server_settings" to "none",
            "template" to "none",
            "category_content" to "none",
            "category_products" to "none",
            "product_content" to "none",
            "product_price" to "none",
            "product_warehouse" to "none",
            "text_pages" to "none",
            "api_key" to null
          )
        }

        testPermissionJson = json {
          obj(
            "user_id" to userId,
            "user_permissions" to "none",
            "server_settings" to "none",
            "template" to "none",
            "category_content" to "none",
            "category_products" to "none",
            "product_content" to "none",
            "product_price" to "none",
            "product_warehouse" to "none",
            "text_pages" to "none",
            "api_key" to null
          )
        }

        eventBus.request<String>("process.account.createBackendPermissions", testPermissionJson).onFailure {
          testContext.failNow(it)
        }.onComplete { createPermissionMsg ->
          assert(createPermissionMsg.succeeded())

          eventBus.request<JsonObject>("process.account.getAllFullUserInfo", "").onFailure {
            testContext.failNow(it)
          }.onComplete { getAllFullMsg ->
            val fullBody = getAllFullMsg.result().body()
            fullBody.getJsonArray("full_user_info").getJsonObject(0).remove("password")
            assertEquals(fullBody, json {
              obj(
                "full_user_info" to listOf(allInfoResult)
              )
            })

            getAllDataCheckpoint.flag()

            eventBus.request<JsonObject>("process.account.getFullUserInformationByUserId", userId).onFailure {
              testContext.failNow(it)
            }.onComplete { fullInfoByIdMsg ->
              val fullBodyById = fullInfoByIdMsg.result().body()
              fullBodyById.remove("password")
              assertEquals(fullBodyById, allInfoResult)

              eventBus.request<String>("process.account.deleteBackendPermissions", userId).onFailure {
                testContext.failNow(it)
              }.onComplete { deletePermissionMsg ->
                assert(deletePermissionMsg.succeeded())
                assertEquals(deletePermissionMsg.result().body(), "Backend permissions deleted successfully")

                eventBus.request<String>("process.account.deleteUser", userId).onFailure {
                  testContext.failNow(it)
                }.onComplete {
                  getDataById.flag()
                }
              }
            }
          }
        }
      }
    })
  }
}
