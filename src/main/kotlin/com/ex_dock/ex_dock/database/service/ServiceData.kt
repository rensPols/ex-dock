package com.ex_dock.ex_dock.database.service

import com.ex_dock.ex_dock.database.template.Template

fun getAllStandardTemplatesData(): List<Template> {
  val templates: MutableList<Template> = mutableListOf()

  templates.add(Template(
    "home",
    "<test>{% for user in accounts %} {{ user.component1().component1() }} {% endfor %}</test>",
    "accounts"
  ))
  return templates.toList()
}

class PopulateException(message: String) : Exception(message)

class InvalidCacheKeyException(message: String) : Exception(message)
