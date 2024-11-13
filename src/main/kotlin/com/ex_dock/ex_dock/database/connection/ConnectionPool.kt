package com.ex_dock.ex_dock.database.connection

import com.ex_dock.ex_dock.ClassLoaderDummy
import com.ex_dock.ex_dock.MainVerticle
import io.vertx.core.Vertx
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import java.util.*
import kotlin.jvm.javaClass

class Connection {
  @Deprecated("Moved getConnection out of Connection() class")
  fun getConnection(vertx: Vertx): Pool {
    return com.ex_dock.ex_dock.database.connection.getConnection(vertx)
  }
}


fun getConnection(vertx: Vertx): Pool {
  val connection: Pool
  val connectOptions = JDBCConnectOptions()

  try {
    lateinit var props: Properties
    try {
      props = Thread.currentThread().contextClassLoader.getResourceAsStream("secret.properties").use {
        Properties().apply { load(it) }
      }
    } catch (e: Exception) {
      println("Could not load secret.properties")
      error("Could not load secret.properties")
    }
    connectOptions
      .setJdbcUrl(props.getProperty("DATABASE_URL"))
      .setUser(props.getProperty("DATABASE_USERNAME"))
      .setPassword(props.getProperty("DATABASE_PASSWORD"))
  } catch (e: Exception) {
    try {
      val isDocker: Boolean = !System.getenv("GITHUB_RUN_NUMBER").isNullOrEmpty()
      if (isDocker) {
        connectOptions
          .setJdbcUrl("jdbc:postgresql://localhost:8890/ex-dock")
          .setUser("postgres")
          .setPassword("docker")
      } else {
        error("Could not load the Properties file!")
      }
    } catch (e: Exception) {
      error("Could not read the Properties file and Docker backup check failed!")
    }
  }

  val poolOptions = PoolOptions()
    .setMaxSize(16)
    .setName("ex-dock")

  connection = JDBCPool.pool(vertx, connectOptions, poolOptions)

  return connection
}
