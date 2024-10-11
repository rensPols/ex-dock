package com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject

class SingleUseTemplateDataCodec: MessageCodec<SingleUseTemplateData, SingleUseTemplateData> {
  override fun encodeToWire(
      buffer: Buffer,
      singleUseTemplateData: SingleUseTemplateData,
  ) {

    // Add template to the buffer
    buffer.appendInt(singleUseTemplateData.template.length)
    buffer.appendString(singleUseTemplateData.template)

    // Add the json data to the buffer
    val contextJson: String = JsonObject(singleUseTemplateData.templateData).toString()
    buffer.appendInt(contextJson.length)
    buffer.appendString(contextJson)
  }

  override fun decodeFromWire(
      position: Int,
      buffer: Buffer,
  ): SingleUseTemplateData? {

    var pos: Int = position
    var endPos: Int = position

    // Template
    var templateLength: Int = buffer.getInt(pos)
    // Int is 4 bytes
    pos += 4
    endPos = pos + templateLength
    val template: String = buffer.getString(pos, endPos)
    pos = endPos

    // Data
    var dataLength: Int = buffer.getInt(pos)
    // Int is 4 bytes
    pos += 4
    endPos = pos + dataLength
    val dataJson: String = buffer.getString(pos, endPos)
    val templateData: Map<String, Any?> = JsonObject(dataJson).map

    return SingleUseTemplateData(template, templateData)
  }

  override fun transform(p0: SingleUseTemplateData?): SingleUseTemplateData? {
    return p0
  }

  override fun name(): String? {
    return "singleUseTemplateDataCodec"
  }

  override fun systemCodecID(): Byte {
    return -1
  }
}