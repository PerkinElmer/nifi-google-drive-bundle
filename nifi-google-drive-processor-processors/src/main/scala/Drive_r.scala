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

  private var CLIENT_ID = "7695128173-dpt0u3galufa6jk22q51lsuv6rta65vp.apps.googleusercontent.com"

  private var CLIENT_SECRET = "maXDAoyuytcz5tdI-3dF-kCf"

  private var ACCESS_TYPE = "online"

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
  def authorize: Credential = {
    // Build flow and trigger user authorization request.
    val flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES).
      setDataStoreFactory(DATA_STORE_FACTORY).setAccessType(ACCESS_TYPE).build
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

    import java.io.File

    for(fileId <- fileIds){
      // strip off file name
      var index = fileId._2.lastIndexOf("\\")
      var fileToDL = fileId._2.substring(index + 1)
      var filePath = "C:\\Users\\redderre\\" + fileId._2.substring(0, index)
      var file = new File(filePath)

      //if current directory for file dne, create
      if(!file.exists()){
        file.mkdirs();
        file.setExecutable(true);
      }

      var absFilePath = "C:\\Users\\redderre\\" + fileId._2
      var fileOutputStream = new FileOutputStream(absFilePath)
      try{
      // todo download file - .get(need fileID) - won't DL otherwise
        driveService.files().get(fileId._1).executeMediaAndDownloadTo(fileOutputStream)
      }catch{
        case e: Exception => println("NonBinary File found")
      }
    }
  }

  //takes file/folder ID as input
  //if it's just a file, return that path
  //if it's a folder, return a list of its children's paths
  def listFiles(service: Drive, fileId: String): List[(String, String)] = {
    var list : List[(String,String)] = List()
    val file = service.files().get(fileId).setFields("parents, name, mimeType, id").execute()
    if (file.getMimeType != "application/vnd.google-apps.folder") {
      list = (file.getId, getParents(service, file, file.getName)) :: list
    }
    else {
      println("the folder you are looking into: " + file)
      val files = service.files.list.setQ("'" + file.getId +"' in parents and trashed=false").setFields("files(name, id, mimeType, parents)").execute.getFiles
      for ((file, index) <- files.asScala.zipWithIndex) {
        list = (file.getId, getParents(service, file, file.getName)) :: list
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

    downloadDriveFile(x, service)
  }
}
