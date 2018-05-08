package com.example.nifi.processors

import java.io.{ IOException, InputStreamReader }

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{ GoogleAuthorizationCodeFlow, GoogleClientSecrets }
import com.google.api.services.drive.Drive

class ApiWrapper(val apiParameters: ApiParameters) {

  @throws[IOException]
  def authorize: Credential = {
    val in = getClass.getResourceAsStream("/client_secret.json")
    val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(apiParameters.json_factory, new InputStreamReader(in))
    val flow = new GoogleAuthorizationCodeFlow.Builder(
      apiParameters.http_transport,
      apiParameters.json_factory,
      clientSecrets,
      apiParameters.scopes)
      .setDataStoreFactory(apiParameters.data_store_factory)
      .setAccessType(apiParameters.accessType)
      .build
    val credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(apiParameters.user)
    return credential
  }

  @throws[IOException]
  def getDriveService: Drive = {
    new Drive.Builder(apiParameters.http_transport, apiParameters.json_factory, authorize)
      .setApplicationName(apiParameters.application_name)
      .build
  }
}
