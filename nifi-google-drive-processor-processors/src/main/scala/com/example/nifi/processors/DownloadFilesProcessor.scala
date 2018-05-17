package com.example.nifi.processors

import org.apache.nifi.annotation.behavior.{ ReadsAttribute, ReadsAttributes, WritesAttribute, WritesAttributes }
import org.apache.nifi.annotation.documentation.{ CapabilityDescription, SeeAlso, Tags }
import org.apache.nifi.components.PropertyDescriptor
import org.apache.nifi.processor._

@Tags(Array("please work"))
@CapabilityDescription("A download processor")
@SeeAlso(Array())
@ReadsAttributes(Array(new ReadsAttribute(attribute = "", description = "")))
@WritesAttributes(Array(new WritesAttribute(attribute = "", description = "")))
class DownloadFilesProcessor
    extends AbstractProcessor
    with DownloadFilesProcessorProperties
    with DownloadFilesProcessorRelationships {

  import scala.collection.JavaConverters._

  override def getSupportedPropertyDescriptors(): java.util.List[PropertyDescriptor] = {
    properties.asJava
  }

  override def getRelationships(): java.util.Set[Relationship] = {
    relationships.asJava
  }

  override def onTrigger(context: ProcessContext, session: ProcessSession): Unit = {
    val flowFile = Option(session.get())

    for (file <- flowFile) {
      val fileId = file.getAttribute("ID")
      val fileName = file.getAttribute("name")
      val filePath = file.getAttribute("path")
      val mime = file.getAttribute("mime")

      DriveAuth.downloadDriveFile(fileId, fileName, filePath, mime)
      session.transfer(file, RelSuccess)
    }
  }

  protected[this] override def init(context: ProcessorInitializationContext): Unit = {
  }
}

