package com.ex_dock.ex_dock.codecs.template_engine

import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateData
import com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use.SingleUseTemplateDataCodec
import io.vertx.core.buffer.Buffer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class SingleUseTemplateDataCodecTest {

  @Test
  fun testEncodeAndDecode() {
    // Create test data
    val template = "Hello {{name}}"
    val templateData = mapOf("name" to "World")

    val originalData = SingleUseTemplateData(template, templateData)
    val codec = SingleUseTemplateDataCodec()

    // Encode the data
    val buffer = Buffer.buffer()
    codec.encodeToWire(buffer, originalData)

    // Decode the data
    val decodedData: SingleUseTemplateData? = codec.decodeFromWire(0, buffer)

    assertNotNull(decodedData, "SingleUseTemplateDataCodec.decodeFromWire() returned null")

    // Assertions
    assertEquals(originalData.template, decodedData?.template, "Template should match")
    assertEquals(originalData.templateData, decodedData?.templateData, "Template data should match")
  }
}
