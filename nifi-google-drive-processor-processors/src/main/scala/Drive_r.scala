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
import com.google.api.services.drive.model.File
import java.io._
import java.util
import scala.collection.JavaConverters._

object Drive_r {

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
    val in = getClass.getResourceAsStream("/client_secret.json")
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

//  @throws[FileNotFoundException]
//  def downloadDriveFile(fileIds: List[(String, String)], driveService: Drive) = {
//
//    for(fileId <- fileIds){
//      // store in this folder under same file name
//
//      var file = new File("C:\\Users\\redderre\\PEfiles\\" + fileId._1)
//      var fileOutputStream = new FileOutputStream(file)
//      try{
//      // download file
//      driveService.files().get(fileId._2).executeMediaAndDownloadTo(fileOutputStream)
//    }catch{
//        case e: Exception => println("NonBinary File found")
//      }
//    }
//  }

  //takes file/folder ID as input
  //if it's just a file, return that path
  //if it's a folder, return a list of its children's paths
  def listFiles(service: Drive, fileId: String): List[(File, String)] = {
    var list : List[(File,String)] = List()
    val file = service.files().get(fileId).setFields("parents, name, mimeType, id").execute()
    if (file.getMimeType != "application/vnd.google-apps.folder") {
      list = (file, getParents(service, file, file.getName)) :: list
    }
    else {
      println("the folder you are looking into: " + file)
      val files = service.files.list.setQ("'" + file.getId +"' in parents and trashed=false").setFields("files(name, id, mimeType, parents)").execute.getFiles
      for ((file, index) <- files.asScala.zipWithIndex) {
        list = (file, getParents(service, file, file.getName)) :: list
      }
    }
    list
  }

  def getParents(service: Drive, file: File, filePath: String): String = {

    var finalPath = filePath
    //if you have no (parents, you're the root, so continue while this is not true
    if (file.getParents != null) {
      val parentId: String = file.getParents.get(0)
      val parentFile = service.files.get(parentId).setFields("id, name, parents").execute()
      finalPath = parentFile.getName + "\\" + filePath
      getParents(service, parentFile, finalPath)
    }
    else {
      finalPath
    }
  }
  @throws[IOException]
  def main(args: Array[String]): Unit = { // Build a new authorized API client service.

    val service = getDriveService
    service.changes.getStartPageToken.execute
    val x = listFiles(service, "14JJ3vRHebTHAws7xfBNUI55M58NaMSU6")//14JJ3vRHebTHAws7xfBNUI55M58NaMSU6 |||| 1wvI23jzfNCiLduOPT-bMDzIC9CZCAh6i : the former is a folder, the latter is a file
    print(x)

    //downloadDriveFile(fileId, service)
    //val f = changes(response, service)
    //print("a")
  }


}
