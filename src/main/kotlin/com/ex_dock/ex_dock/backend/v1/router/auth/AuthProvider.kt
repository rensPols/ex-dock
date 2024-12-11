package com.ex_dock.ex_dock.backend.v1.router.auth

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64

class AuthProvider {
  private val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
  private val keyPair: KeyPair = generator.generateKeyPair()

  private companion object {
    const val BEGINPRIVATEKEY = "-----BEGIN PRIVATE KEY-----\n"
    const val ENDPRIVATEKEY = "\n-----END PRIVATE KEY-----"
    const val BEGINPUBLICKEY = "-----BEGIN PUBLIC KEY-----\n"
    const val ENDPUBLICKEY = "\n-----END PUBLIC KEY-----"
  }

  val privateKey = BEGINPRIVATEKEY +
    Base64.getMimeEncoder().encodeToString(keyPair.private.encoded) +
    ENDPRIVATEKEY
  val publicKey = BEGINPUBLICKEY +
    Base64.getMimeEncoder().encodeToString(keyPair.public.encoded) +
    ENDPUBLICKEY
}
