package com.ex_dock.ex_dock.database.category

import com.ex_dock.ex_dock.database.product.Products

data class Categories(
  var categoryId: Int?,
  var upperCategory: Int?,
  var name: String,
  var shortDescription: String,
  var description: String
)

data class CategoriesProducts(
  val categoryId: Categories,
  val productId: Products
)

data class CategoriesSeo(
  val categoryId: Int,
  var metaTitle: String?,
  var metaDescription: String?,
  var metaKeywords: String?,
  var pageIndex: PageIndex
)

data class FullCategoryInfo(
  val categories: Categories,
  val categoriesSeo: CategoriesSeo
)

enum class PageIndex(pIndex: String) {
  IndexFollow("index, follow"),
  IndexNoFollow("index, nofollow"),
  NoIndexFollow("noindex, follow"),
  NoIndexNoFollow("noindex, nofollow");

  companion object {
    fun fromString(value: String): PageIndex? {
      return values().find { it.name == value }
    }

    fun toString(pageIndex: PageIndex): String {
      return when (pageIndex) {
        IndexFollow -> "index, follow"
        IndexNoFollow -> "index, nofollow"
        NoIndexFollow -> "noindex, follow"
        NoIndexNoFollow -> "noindex, nofollow"
      }
    }
  }
}
