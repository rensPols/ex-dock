package com.ex_dock.ex_dock.database.url

data class UrlKeys(
  var urlKey: String,
  var upperKey: String,
  var pageType: PageType
)

data class TextPageUrls(
  var urlKeys: String,
  var upperKey: String,
  var textPagesId: String
)

data class ProductUrls(
  var urlKeys: String,
  var upperKey: String,
  var productId: String
)

data class CategoryUrls(
  var urlKeys: String,
  var upperKey: String,
  var categoryId: String
)

enum class PageType(name: String) {
  PRODUCT("product"),
  CATEGORY("category"),
  TEXT_PAGE("text_page")
}
