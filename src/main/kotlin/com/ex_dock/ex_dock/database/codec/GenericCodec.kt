package com.ex_dock.ex_dock.database.codec

import io.vertx.core.eventbus.MessageCodec
import kotlin.reflect.KClass
import io.vertx.core.buffer.Buffer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutput
import java.io.ObjectOutputStream

class GenericCodec<T: Any>(private val codecClass: Class<T>): MessageCodec<T, T> {
  override fun encodeToWire(buffer: Buffer, s: T) {
    val bos = ByteArrayOutputStream()
    var out: ObjectOutput? = null

    try {
      out = ObjectOutputStream(bos)
      out.writeObject(s)
      out.flush()
      val bytes: ByteArray = bos.toByteArray()
      buffer.appendInt(bytes.size)
      buffer.appendBytes(bytes)
      out.close()
    } catch (e: IOException) {
      e.printStackTrace()
    } finally {
      try {
        bos.close()
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

  override fun decodeFromWire(pos: Int, buffer: Buffer): T? {
    var _pos: Int = pos

    var length = buffer.getInt(_pos)

    val bytes: ByteArray = buffer.getBytes(_pos + 4, _pos + 4 + length)
    _pos += 4 + length
    val bis = ByteArrayInputStream(bytes)

    try {
      val ois = ObjectInputStream(bis)
      @Suppress("UNCHECKED_CAST")
      val msg: T = ois.readObject() as T
      ois.close()
      return msg
    } catch (e: IOException) {
      e.printStackTrace()
    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
    } finally {
        try {
            bis.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
    }

    return null
  }

  override fun name(): String {
    return codecClass.simpleName+"Codec"
  }

  override fun systemCodecID(): Byte {
    return -1
  }

  override fun transform(s: T): T {
    return s
  }
}
