import java.io._
import java.util

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive

//import com.google.api.services.drive.model.{Change, StartPageToken}

import scala.collection.immutable.List
import scala.collection.JavaConverters._

object drive_r{
  /** Application name. */
  private val APPLICATION_NAME = "Drive API Java Quickstart"
  /** Directory to store user credentials for this application. */
  private val DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/drive-java-quickstart")
  /** Global instance of the {@link FileDataStoreFactory}. */
  private var DATA_STORE_FACTORY: FileDataStoreFactory = null
  /** Global instance of the JSON factory. */
  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  /** Global instance of the HTTP transport. */
  private var HTTP_TRANSPORT: NetHttpTransport = null
  /** Global instance of the scopes required by this quickstart.
    *
    * If modifying these scopes, delete your previously saved credentials
    * at ~/.credentials/drive-java-quickstart
    */
  private val SCOPES = util.Arrays.asList("https://www.googleapis.com/auth/drive")

  private val Q = ("fileExtension = 'sas7bdat'")

  try {
    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport
    DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR)
  } catch {
    case t: Throwable =>
      t.printStackTrace()
      System.exit(1)
  }

  /**
    * Creates an authorized Credential object.
    *
    * @return an authorized Credential object.
    * @throws IOException
    */
  @throws[IOException]
  def authorize: Credential = { // Load client secrets.
    val in = getClass.getResourceAsStream("/client_secret.json");
    val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in))
    // Build flow and trigger user authorization request.
    val flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).
      setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("online").build
    val credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
    System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath)
    return credential
  }

  /**
    * Build and return an authorized Drive client service.
    *
    * @return an authorized Drive client service
    * @throws IOException
    */
  @throws[IOException]
  def getDriveService: Drive = {
    val credential = authorize
    new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build
  }

  @throws[FileNotFoundException]
  def downloadDriveFile(fileIds: List[(String, String)], driveService: Drive) = {

    for(fileId <- fileIds){
      // store in this folder under same file name
      var file = new File("C:\\Users\\redderre\\PEfiles\\" + fileId._1)
      var fileOutputStream = new FileOutputStream(file)

      // download file
      driveService.files().get(fileId._2).executeMediaAndDownloadTo(fileOutputStream)
    }
  }
  /*
    def changes(savedPageToken: StartPageToken, driveService: Drive): List[(String, String)] ={
      var pageToken: String = (savedPageToken).asInstanceOf[String]
      var fileId: List[(String, String)] = List()
      while(pageToken != null){
        val changes = driveService.changes.list(pageToken).execute
        for(change <- changes.getChanges){
          //Store in list
          var file = change.getFile
          fileId = (file.getName, file.getId) :: fileId
        }
        if(changes.getNewStartPageToken != null){
            pageToken = changes.getNextPageToken
          }
      }
      fileId
    }
  */
  @throws[IOException]
  def main(args: Array[String]): Unit = { // Build a new authorized API client service.
    val service = getDriveService
    val response = service.changes.getStartPageToken.execute

    val result = service.files.list.setQ(Q).setFields("nextPageToken, files(id, name)").execute
    val files = result.getFiles

    var fileId: List[(String, String)] = List()

    if (files == null || files.size == 0) System.out.println("No files found.")
    else {
      System.out.println("Files:")
      for ((file, index) <- files.asScala.zipWithIndex) {
        System.out.printf("%s (%s)\n", file.getName, file.getId)
        fileId = (file.getName, file.getId) :: fileId
      }
    }

    downloadDriveFile(fileId, service)
    //val f = changes(response, service)
    //print("a")
  }

}
