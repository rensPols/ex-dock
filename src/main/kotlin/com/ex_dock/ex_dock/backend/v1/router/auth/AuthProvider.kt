package com.ex_dock.ex_dock.backend.v1.router.auth

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64

class AuthProvider {
  private val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
  private val keyPair: KeyPair = generator.generateKeyPair()
  private val beginPrivateKey = "-----BEGIN PRIVATE KEY-----\n"
  private val endPrivateKey = "\n-----END PRIVATE KEY-----"
  private val beginPublicKey = "-----BEGIN PUBLIC KEY-----\n"
  private val endPublicKey = "\n-----END PUBLIC KEY-----"
  val privateKey = beginPrivateKey +
    Base64.getMimeEncoder().encodeToString(keyPair.private.encoded) +
    endPrivateKey
  val publicKey = beginPublicKey +
    Base64.getMimeEncoder().encodeToString(keyPair.public.encoded) +
    endPublicKey
}
