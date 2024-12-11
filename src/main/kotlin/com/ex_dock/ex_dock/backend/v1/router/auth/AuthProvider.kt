package com.ex_dock.ex_dock.backend.v1.router.auth

import java.nio.charset.Charset
import java.nio.file.Files
import kotlin.io.path.Path

class AuthProvider {
  private val publicKeyFile = Path(System.getProperty("user.dir") + "\\public.pem")
  private val privateKeyFile = Path(System.getProperty("user.dir") + "\\private.pem")
  val publicKey = String(Files.readAllBytes(publicKeyFile), charset = Charset.defaultCharset())
  val privateKey = String(Files.readAllBytes(privateKeyFile), charset = Charset.defaultCharset())
}
