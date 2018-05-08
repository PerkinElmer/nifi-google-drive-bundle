package com.example.nifi.processors

import org.apache.nifi.components.PropertyDescriptor

//no validators implemented at this point since everything is hardcoded
//properties also not required since everything is hardcoded
trait ApiProcessorProperties {
  val ClientSecretProperty: PropertyDescriptor =
    new PropertyDescriptor.Builder()
      .name("Drive Client Secret")
      .description("Google Drive Client Secret.")
      .required(false)
      .expressionLanguageSupported(true)
      .sensitive(true)
      .build

  val ClientIdProperty: PropertyDescriptor =
    new PropertyDescriptor.Builder()
      .name("Drive Client ID")
      .description("Google Drive Client ID.")
      .required(false)
      .expressionLanguageSupported(true)
      .build

  val CredentialProperty: PropertyDescriptor =
    new PropertyDescriptor.Builder()
      .name("Login Credentials File")
      .description("Location of Credentials File")
      .required(false)
      .expressionLanguageSupported(true)
      .build

  lazy val properties = List(
    ClientSecretProperty,
    ClientIdProperty,
    CredentialProperty)
}

object ApiProcessorProperties extends ApiProcessorProperties {}
