package com.ex_dock.ex_dock.frontend.cache

import com.ex_dock.ex_dock.database.account.FullUser
import com.ex_dock.ex_dock.database.service.InvalidCacheKeyException
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import java.util.concurrent.TimeUnit

class CacheVerticle: AbstractVerticle() {
  private lateinit var cache: LoadingCache<String, CacheData>
  private val mapDeliveryOptions = DeliveryOptions().setCodecName("MapCodec")
  private val expireDuration = 10L
  private val refreshDuration = 1L
  private val maxHitCount = 5

  private lateinit var eventBus: EventBus

  override fun start() {
    eventBus = vertx.eventBus()

    // Initialize the cache
    cache = Caffeine.newBuilder()
      .expireAfterWrite(expireDuration, TimeUnit.MINUTES)
      .refreshAfterWrite(refreshDuration, TimeUnit.MINUTES)
      .build { k -> convertRequestedDataToAddress(k) }

    // Initialize the eventbus address to handle the cache requests
    getData()
  }

  private fun getData() {
    val cacheConsumer = eventBus.consumer<String>("process.cache.requestData")
    cacheConsumer.handler { message ->
      val futures = mutableListOf<Future<Void>>()
      val requestedData = message.body()
      val cacheMap = emptyMap<String, List<Any>>().toMutableMap()
      val keyString = requestedData.split(";")

      //Iterate through all keys in the key string
      for (key in keyString) {
        futures.add(Future.future { promise ->
          var cacheData = cache[key]

          // If the cache data is still the initial value, wait until it's loaded
          if (cacheData.initial) {
            vertx.setPeriodic(100) { // Check periodically without blocking
              cacheData = cache[key]
              if (!cacheData.initial) {
                incrementHitCount(key)
                cacheMap[key] = cacheData.data
                promise.complete()
                vertx.cancelTimer(it) // Cancel the periodic check once done
              }
            }
          } else {
            // If data is already loaded, just increment the hit count and add to map
            incrementHitCount(key)
            cacheMap[key] = cacheData.data
            promise.complete()
          }
        })
      }

      // Wait for all cache keys to be processed before replying
      Future.all(futures).onComplete{ ar ->
        if (ar.succeeded()) {
          // Once all the futures are completed, send the response back
          message.reply(cacheMap.toMap(), mapDeliveryOptions)
        } else {
          // Handle any failure (shouldn't occur in this case)
          message.fail(500, "Failed to load cache data")
        }
      }
    }
  }

  /**
   * Converts the given key to the cache data from the database with the correct address
   * @param key The ket to use
   * @return The cache data from the database
   */
  private fun convertRequestedDataToAddress(key: String): CacheData {
    return when (key) {
      "accounts" -> getDataFromDatabase(key, "account.getAllFullUserInfo")
      "categories" -> getDataFromDatabase(key, "categories.getAllFullInfo")
      "custom_attributes" -> getDataFromDatabase(key, "attributes.getAllCustomAttributes")
      "global_eav" -> getDataFromDatabase(key, "eavGlobal.getAllEavGlobalInfo")
      "products" -> getDataFromDatabase(key, "products.getAllFullProducts")
      "multi_select" -> getDataFromDatabase(key, "multiSelect.getAllMultiSelectAttributesInfo")
      "store_view_eav" -> getDataFromDatabase(key, "eavStoreView.getAllEavStoreViewInfo")
      "website_eav" -> getDataFromDatabase(key, "eavWebsite.getAllEavWebsiteInfo")
      "scopes" -> getDataFromDatabase(key, "scope.getAllScopes")
      "server_data" -> getDataFromDatabase(key, "server.getAllServerData")
      "server_version" -> getDataFromDatabase(key, "server.getAllServerVersions")
      "text_pages" -> getDataFromDatabase(key, "textPages.getAllFullTextPages")
      "urls" -> getDataFromDatabase(key, "url.getAllFullUrls")
      else -> throw InvalidCacheKeyException("Unknown cache key: $key")
    }
  }

  /**
   * Increments the hit count of the data with the given key and invalidates
   * the cache when the hit count exceeds the maximum hit count
   */
  private fun incrementHitCount(key: String) {
    val cacheData = cache.getIfPresent(key)

    // Check if the cache data exists and is not expired or deleted
    if (cacheData != null) {
      // Check if the cache data is over the threshold of max hit count or if the flag is set
      if (cacheData.hits > maxHitCount || cacheData.flag) {

        // Delete the cache data to reset the hit count
        cache.invalidate(key)
        println("CACHE DATA EXPIRED")
        return
      }

      // Increment the hit count of the cache data
      cacheData.hits++
      cache.put(key, cacheData)
    }
  }

  private fun getDataFromDatabase(key: String, address: String): CacheData {
    //Sets the cache data to an initial value to avoid null values
    val cacheData = CacheData(
      data = emptyList(),
      hits = 0,
      flag = false,
      initial = true
    )

    // Makes a future that runs in the background fetching the data
    Future.future { promise ->
      eventBus.request<MutableList<FullUser>>("process.$address", "")
      {
        if (it.succeeded()) {
          cacheData.data = it.result().body()
          cacheData.initial = false
        } else {
          cacheData.flag = true
        }

        cache.put(key, cacheData)

        promise.complete(cacheData)
      }
    }

    return cacheData
  }
}

/**
 * Cache data object for use in the cache
 * @param data The data from the database as a list
 * @param hits The amount of time the cache got hit
 * @param flag The flag if the data has expired due to other reasons. f.e. new data
 * @param initial If the data is the initial data that gets returned when fetching the new data
 */
data class CacheData(
  var data: List<Any>,
  var hits: Int,
  var flag: Boolean,
  var initial: Boolean
)
