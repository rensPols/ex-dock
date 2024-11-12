package com.ex_dock.ex_dock.database.url

import com.ex_dock.ex_dock.database.category.Categories
import com.ex_dock.ex_dock.database.product.Products
import com.ex_dock.ex_dock.database.text_pages.TextPages

data class UrlKeys(
  var urlKey: String,
  var upperKey: String,
  var pageType: PageType
)

data class TextPageUrls(
  var urlKeys: String,
  var upperKey: String,
  var textPagesId: Int
)

data class ProductUrls(
  var urlKeys: String,
  var upperKey: String,
  var productId: Int
)

data class CategoryUrls(
  var urlKeys: String,
  var upperKey: String,
  var categoryId: Int
)

data class FullUrlKeys(
  var urlKeys: UrlKeys,
  var textPage: TextPages?,
  var product: Products?,
  var category: Categories?
)

data class JoinList(
  var joinTextPage: Boolean,
  var joinProduct: Boolean,
  var joinCategory: Boolean,
)

data class FullUrlRequestInfo(
  val urlKeys: String?,
  val upperKey: String?,
  val joinList: JoinList
)

enum class PageType(name: String) {
  PRODUCT("product"),
  CATEGORY("category"),
  TEXT_PAGE("text_page")
}
