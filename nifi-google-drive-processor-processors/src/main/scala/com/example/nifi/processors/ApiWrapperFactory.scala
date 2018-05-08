package com.example.nifi.processors

import org.apache.nifi.processor.ProcessContext

class ApiWrapperFactory {
  def createApiWrapper(context: ProcessContext): ApiWrapper = {
    val apiParameters = new ApiParameters(context)
    new ApiWrapper(apiParameters)
  }
}
