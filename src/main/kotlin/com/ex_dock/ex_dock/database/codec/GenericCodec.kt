package com.ex_dock.ex_dock.database.codec

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import java.io.*

class GenericCodec<T>(cls: Class<T>): MessageCodec<T, T>{
  private val clazz: Class<T> = cls
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
      @SuppressWarnings("UNCHECKED_CAST")
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
    return clazz.simpleName+"Codec"
  }

  override fun systemCodecID(): Byte {
    return -1
  }

  override fun transform(s: T): T {
    return s
  }
}
