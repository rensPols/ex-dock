package com.ex_dock.ex_dock.frontend.template_engine

import com.ex_dock.ex_dock.database.product.FullProductWithCategory

open class Filter<T : Any>(open val name: String) {
  open fun apply(list: List<T>) : List<T> {
    // Default implementation does nothing
    return emptyList()
  }
}

class ProductFilter(
  override val name: String,
  val lowPrice: Int,
  val highPrice: Int,
  val category: String
) : Filter<FullProductWithCategory>(name) {
  override fun apply(list: List<FullProductWithCategory>): List<FullProductWithCategory> {
    return list.filter { product ->
      product.product.name.contains(name)
        && product.productsPricing.price > lowPrice
        && product.productsPricing.price < highPrice
        && product.category.any { it.name == category }
    }
  }
}

//private data class ProductFilter(
//  val lowPrice: Int,
//  val highPrice: Int,
//  val category: String,
//  val name: String,
//)
