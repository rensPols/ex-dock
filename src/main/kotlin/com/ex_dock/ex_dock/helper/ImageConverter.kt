package com.ex_dock.ex_dock.helper

import io.vertx.core.Vertx
import java.io.File

fun convertImageToWebp(vertx: Vertx, name: String, extension: String) {
  val validExtensions = listOf(".png", ".jpg", ".jpeg", ".webp")
  val path = System.getProperty("user.dir") + "\\src\\main\\resources\\images"
  val folder = File(path)
  for (file in folder.listFiles()!!) {
    if (!validExtensions.contains(file.name)) {
      val newName = File("$path\\$name.$extension")
      println(file.renameTo(newName))
    }
  }
}
