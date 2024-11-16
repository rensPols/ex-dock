package com.ex_dock.ex_dock.database.service

import com.ex_dock.ex_dock.database.connection.getConnection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.sqlclient.Pool

class ServiceVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus

  override fun start() {
    client = getConnection(vertx)
    eventBus = vertx.eventBus()
  }

  fun populateTemplateTable() {
    val templateList = getAllStandardTemplates()
    val query = "INSERT INTO templates (template_key, template_data) SELECT "
    for (template in templateList) {
      val rowsFuture = client.preparedQuery(query).execute()
    }
  }
}
