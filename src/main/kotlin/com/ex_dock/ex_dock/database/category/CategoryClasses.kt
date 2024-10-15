package com.ex_dock.ex_dock.database.category

data class Categories(
  var categoryId: Int,
  var upperCategory: Int?,
  var name: String,
  var shortDescription: String,
  var description: String
)

data class CategoriesProducts(
  val categoryId: Int,
  val productId: Int
)

data class CategoriesSeo(
  val categoryId: Int,
  var metaTitle: String?,
  var metaDescription: String?,
  var metaKeywords: String?,
  var pageIndex: PageIndex
)

enum class PageIndex(name: String) {
  IndexFollow("index, follow"),
  IndexNoFollow("index, nofollow"),
  NoIndexFollow("noindex, follow"),
  NoIndexNoFollow("noindex, nofollow");
}
