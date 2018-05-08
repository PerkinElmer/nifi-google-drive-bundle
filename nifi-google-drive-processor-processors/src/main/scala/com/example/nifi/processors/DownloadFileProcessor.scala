package com.example.nifi.processors

import java.io.{ InputStream, OutputStream }

import org.apache.nifi.components.PropertyDescriptor
import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor._
import org.apache.nifi.processor.io.StreamCallback

class DownloadFileProcessor(private val apiWrapperFactory: ApiWrapperFactory)
    extends AbstractProcessor
    with ApiProcessorProperties
    with DownloadFileProcessorRelationships {

  import scala.collection.JavaConverters._

  def this() = this(new ApiWrapperFactory)

  override def getSupportedPropertyDescriptors: java.util.List[PropertyDescriptor] = {
    properties.asJava
  }

  override def getRelationships: java.util.Set[Relationship] = {
    relationships.asJava
  }

  override def onTrigger(processContext: ProcessContext, processSession: ProcessSession): Unit = {
    val flowFile = Option(processSession.get())

    flowFile.foreach { file =>
      val fileWithDownload = processSession.write(file, new DownloadFileStreamCallback(processSession, processContext, flowFile.orNull))
      processSession.transfer(fileWithDownload, RelSuccess)
    }
  }

  protected[this] override def init(context: ProcessorInitializationContext): Unit = {}

  protected class DownloadFileStreamCallback(val session: ProcessSession,
    val context: ProcessContext,
    val flowFile: FlowFile)
      extends StreamCallback {
    override def process(in: InputStream, out: OutputStream): Unit = {
      //todo depends on how list processor stores fileId in flow file
      var temp = "t" //.get needs fileId string to download the file

      try {
        val apiWrapper = apiWrapperFactory.createApiWrapper(context)
        val driveService = apiWrapper.getDriveService

        //package file DL results and places in flowfile?
        driveService.files().get(temp).executeMediaAndDownloadTo(out);
      } catch {
        //this file is a failure, send to failure relationship
        case e: Exception =>
          getLogger.error(e.getMessage, e)
          session.transfer(session.create(flowFile), RelFailure)
      }
    }
  }
}

