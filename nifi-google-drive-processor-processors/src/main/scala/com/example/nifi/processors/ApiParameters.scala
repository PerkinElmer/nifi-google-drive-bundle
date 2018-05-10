package com.example.nifi.processors

import java.io.File
import java.util

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import org.apache.nifi.processor.ProcessContext

class ApiParameters(val clientSecret: String,
    val clientId: String,
    var http_transport: NetHttpTransport = null,
    var data_store_factory: FileDataStoreFactory = null,
    var data_store_dir: File = new java.io.File(System.getProperty("user.home"), ".credentials/drive-java-quickstart"),
    val query: String = ("fileExtension = 'sas7bdat'"),
    val json_factory: JsonFactory = JacksonFactory.getDefaultInstance,
    val scopes: util.List[String] = util.Arrays.asList("https://www.googleapis.com/auth/drive"),
    val application_name: String = "Nifi Drive Processor",
    val accessType: String = "online",
    val user: String = "user",
    var credentials: String) {
  def this(context: ProcessContext) = {
    this(
      clientSecret = context
        .getProperty(ApiProcessorProperties.ClientSecretProperty)
        .evaluateAttributeExpressions()
        .getValue,
      clientId = context
        .getProperty(ApiProcessorProperties.ClientIdProperty)
        .evaluateAttributeExpressions()
        .getValue,
      credentials = context
        .getProperty(ApiProcessorProperties.CredentialProperty)
        .evaluateAttributeExpressions()
        .getValue)
    try {
      http_transport = GoogleNetHttpTransport.newTrustedTransport
      data_store_factory = new FileDataStoreFactory(data_store_dir)
    } catch {
      case t: Throwable =>
        t.printStackTrace()
        System.exit(1)
    }
  }
}

