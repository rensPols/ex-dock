package com.ex_dock.ex_dock.frontend.template_engine.template_data.single_use

class SingleUseTemplateData{
  val template: String
  val templateData: Map<String, Any?>

  constructor(template: String, templateData: Map<String, Any?>) {
    this.template = template
    this.templateData = templateData
  }
}