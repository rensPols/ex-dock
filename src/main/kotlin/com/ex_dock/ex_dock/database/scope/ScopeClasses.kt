package com.ex_dock.ex_dock.database.scope

data class Websites(
  var websiteId: Int,
  var websiteName: String
)

data class StoreView(
  var storeViewId: Int,
  var websiteId: Int,
  var storeViewName: String
)
