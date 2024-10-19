package com.ex_dock.ex_dock.database.connection

import io.vertx.core.Vertx
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import java.util.*

class Connection {
  fun getConnection(vertx: Vertx): Pool {
    val connection: Pool
    val connectOptions: JDBCConnectOptions = JDBCConnectOptions()

    try {
      val props: Properties = javaClass.classLoader.getResourceAsStream("secret.properties").use {
        Properties().apply { load(it) }
      }

      connectOptions
        .setJdbcUrl(props.getProperty("DATABASE_URL"))
        .setUser(props.getProperty("DATABASE_USERNAME"))
        .setPassword(props.getProperty("DATABASE_PASSWORD"))
    } catch (e: Exception) {
      try {
          val isDocker: Boolean = System.getenv("DOCKER_RUNNING").toBoolean()
          if (isDocker) {
            connectOptions
              .setJdbcUrl("jdbc:postgresql://localhost:8890/ex-dock")
              .setUser("postgres")
              .setPassword("docker")
          } else {
            error("Could not load the Properties file!")
          }
      } catch (e: Exception) {
        error("Could not read the Properties file!")
      }
    }

    val poolOptions = PoolOptions()
      .setMaxSize(16)
      .setName("ex-dock")

    connection = JDBCPool.pool(vertx, connectOptions, poolOptions)

    return connection
  }
}
