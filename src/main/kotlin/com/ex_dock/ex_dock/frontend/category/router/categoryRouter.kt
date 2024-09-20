package com.ex_dock.ex_dock.frontend.category.router

import io.vertx.ext.web.Router
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj

fun Router.initCategory(vertx: Vertx) {
  val categoryRouter = Router.router(vertx)
  val eventBus = vertx.eventBus()

  // Make it so the RequestBodies can be read
  categoryRouter.post("/").handler(BodyHandler.create())
  categoryRouter.post("/seo").handler(BodyHandler.create())
  categoryRouter.post("/full").handler(BodyHandler.create())

  // Get all categories
  categoryRouter["/"].handler { ctx ->
    eventBus.request<JsonObject>("process.categories.getAll", "")
      .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving products: ${error.localizedMessage}")
      }
  }

  // Get category by ID
  categoryRouter["/id/:id"].handler { ctx ->
    val categoryId: Int = ctx.pathParam("id").toInt()
    eventBus.request<JsonObject>("process.categories.getById", categoryId)
      .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving product with id $categoryId: ${error.localizedMessage}")
      }
  }

  // Create a new category
  categoryRouter.post("/").handler { ctx ->
    val requestBody = ctx.body().asJsonObject()
    eventBus.request<String>("process.categories.create", requestBody)
      .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error creating product: ${error.localizedMessage}")
      }
  }

  // Update a category
  categoryRouter.put("/").handler { ctx ->
    val requestBody = ctx.body().asJsonObject()
    eventBus.request<String>("process.categories.edit", requestBody)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error updating product: ${error.localizedMessage}")
      }
  }

  // Delete a category
  categoryRouter.delete("/id/:id").handler { ctx ->
    val categoryId: Int = ctx.pathParam("id").toInt()
    eventBus.request<String>("process.categories.delete", categoryId)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error deleting product with id $categoryId: ${error.localizedMessage}")
      }
  }

  // Get SEO settings for all categories
  categoryRouter["/seo"].handler { ctx ->
    eventBus.request<JsonObject>("process.categories.getAllSeo", "")
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving SEO settings: ${error.localizedMessage}")
      }
  }

  // Get SEO settings for a specific category
  categoryRouter["/seo/id/:id"].handler { ctx ->
    val categoryId: Int = ctx.pathParam("id").toInt()
    eventBus.request<JsonObject>("process.categories.getSeoById", categoryId)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving SEO settings for category with id $categoryId: ${error.localizedMessage}")
      }
  }

  // Create SEO settings for a specific category
  categoryRouter.post("/seo").handler { ctx ->
    val requestBody = ctx.body().asJsonObject()
    eventBus.request<String>("process.categories.createSeoCategory", requestBody)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error creating SEO settings for category: ${error.localizedMessage}")
      }
  }

  // Update SEO settings for a specific category
  categoryRouter.put("/seo").handler { ctx ->
    val requestBody = ctx.body().asJsonObject()
    eventBus.request<String>("process.categories.editSeoCategory", requestBody)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error updating SEO settings for category: ${error.localizedMessage}")
      }
  }

  // Delete SEO settings for a specific category
  categoryRouter.delete("/seo/id/:id").handler { ctx ->
    val categoryId: Int = ctx.pathParam("id").toInt()
    eventBus.request<String>("process.categories.deleteSeoCategory", categoryId)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error deleting SEO settings for category with id $categoryId: ${error.localizedMessage}")
      }
  }

  // Get full settings for all categories
  categoryRouter["/full"].handler { ctx ->
    eventBus.request<JsonObject>("process.categories.getAllFullInfo", "")
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving full settings: ${error.localizedMessage}")
      }
  }

  // Get full settings for a specific category
  categoryRouter["/full/id/:id"].handler { ctx ->
    val categoryId: Int = ctx.pathParam("id").toInt()
    eventBus.request<JsonObject>("process.categories.getFullInfoById", categoryId)
     .onSuccess{ reply ->
        ctx.end(reply.body().toString())
      }.onFailure { error ->
        ctx.end("Error retrieving full settings for category with id $categoryId: ${error.localizedMessage}")
      }
  }

  this.route("/category*").subRouter(categoryRouter)
}
