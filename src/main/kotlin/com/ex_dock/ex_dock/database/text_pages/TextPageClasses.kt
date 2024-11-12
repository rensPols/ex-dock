package com.ex_dock.ex_dock.database.text_pages

import com.ex_dock.ex_dock.database.category.PageIndex

data class TextPages(
  var textPagesId: Int,
  var name: String,
  var shortText: String,
  var text: String
)

data class TextPagesSeo(
  var textPagesId: Int,
  var metaTitle: String?,
  var metaDescription: String?,
  var metaKeywords: String?,
  var pageIndex: PageIndex
)

data class FullTextPages(
  val textPages: TextPages,
  val textPagesSeo: TextPagesSeo
)
