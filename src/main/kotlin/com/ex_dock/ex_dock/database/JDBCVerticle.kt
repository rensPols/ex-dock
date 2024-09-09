package com.ex_dock.ex_dock.database

import io.vertx.ext.web.RoutingContext
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import java.util.*

class JDBCVerticle: CoroutineVerticle() {

  private lateinit var client: Pool

  private lateinit var props: Properties


  override suspend fun start() {
    props = javaClass.classLoader.getResourceAsStream("secret.properties").use {
      Properties().apply { load(it) }
    }

    val connectOptions = JDBCConnectOptions()
      .setJdbcUrl(props.getProperty("DATABASE_URL"))
      .setUser(props.getProperty("DATABASE_USERNAME"))
      .setPassword(props.getProperty("DATABASE_PASSWORD"))

    val poolOptions = PoolOptions()
      .setMaxSize(16)
      .setName("ex-dock")


    client = JDBCPool.pool(vertx, connectOptions, poolOptions)
  }

  private suspend fun getAll(ctx: RoutingContext) {
    val rows = client.preparedQuery("SELECT * FROM importance").execute().coAwait()
    if (rows.size() > 0) {
      ctx.response().end(json {
        obj("importance_levels" to rows.iterator().next().getInteger("importance_levels")).encode()
      })
    } else {
      ctx.response().setStatusCode(404).end()
    }
  }
}
