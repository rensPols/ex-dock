package com.ex_dock.ex_dock.database.connection

import io.vertx.core.Vertx
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import java.util.*

class Connection {
  fun getConnection(vertx: Vertx): Pool {
    var connection: Pool

    var props: Properties = javaClass.classLoader.getResourceAsStream("secret.properties").use {
      Properties().apply { load(it) }
    }

    val connectOptions = JDBCConnectOptions()
      .setJdbcUrl(props.getProperty("DATABASE_URL"))
      .setUser(props.getProperty("DATABASE_USERNAME"))
      .setPassword(props.getProperty("DATABASE_PASSWORD"))

    val poolOptions = PoolOptions()
      .setMaxSize(16)
      .setName("ex-dock")

    connection = JDBCPool.pool(vertx, connectOptions, poolOptions)

    return connection;
  }
}
