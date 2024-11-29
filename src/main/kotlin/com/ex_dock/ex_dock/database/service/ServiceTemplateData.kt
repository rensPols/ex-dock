package com.ex_dock.ex_dock.database.service

import com.ex_dock.ex_dock.database.template.Template

fun getAllStandardTemplatesData(): List<Template> {
  val templates: MutableList<Template> = mutableListOf()

  templates.add(Template(
    "home",
    "<test>{% for user in accounts %} {{ user.component1().component1() }} {% endfor %}</test>",
    "accounts"
  ))

  // The standard product home page
  templates.add(Template(
    "productHome",
    "<html>\n" +
      "    <head>\n" +
      "        <meta charset=\"utf-8\">\n" +
      "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
      "        <title></title>\n" +
      "        <meta name=\"description\" content=\"\">\n" +
      "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
      "        <link rel=\"stylesheet\" href=\"\">\n" +
      "        <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=search\" />\n" +
      "    </head>\n" +
      "    <body>\n" +
      "        <form>\n" +
      "            <label for=\"search-input\">Search</label><br>\n" +
      "            <input type=\"text\" id=\"search-input\" name=\"search-input\" />\n" +
      "            <input type=\"submit\" value=\"search\"/>\n" +
      "        </form>\n" +
      "\n" +
      "        {% for product in products %}\n" +
      "            <div style=\"display: flex; margin-left: 15vw;\">\n" +
      "                <div style=\"border: solid; min-width: 70vw;\">\n" +
      "                    <div style=\"display: flex;\">\n" +
      "                        <h2 style=\"margin-left: 4vw; min-width: 15vw;\">{{ product.component1().component2() }}</h2>\n" +
      "                        <p>{{ product.component1().component4() }}</p>\n" +
      "                    </div>\n" +
      "                    <p style=\"margin-left: 75vw\">{{ product.component3().component2() }}</p>\n" +
      "                </div>\n" +
      "            </div>\n" +
      "        {% endfor %}\n" +
      "    </body>\n" +
      "</html>",
    "products"
  ))

  return templates.toList()
}

class PopulateException(message: String) : Exception(message)

class InvalidCacheKeyException(message: String) : Exception(message)
