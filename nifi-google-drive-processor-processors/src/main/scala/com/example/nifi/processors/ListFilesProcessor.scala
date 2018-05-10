/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.nifi.processors

import java.io._

import org.apache.nifi.annotation.behavior.{ ReadsAttribute, ReadsAttributes, WritesAttribute, WritesAttributes }
import org.apache.nifi.annotation.documentation.{ CapabilityDescription, SeeAlso, Tags }
import org.apache.nifi.components.PropertyDescriptor
import org.apache.nifi.processor._
@Tags(Array("please work"))
@CapabilityDescription("A list processor")
@SeeAlso(Array())
@ReadsAttributes(Array(
  new ReadsAttribute(attribute = "", description = "")))
@WritesAttributes(Array(
  new WritesAttribute(attribute = "", description = "")))
class ListFilesProcessor extends AbstractProcessor with ListFilesProcessorProperties
    with ListFilesProcessorRelationships {

  import java.util.concurrent.atomic.AtomicReference

  import scala.collection.JavaConverters._

  private val data = new AtomicReference[Array[Byte]]

  override def getSupportedPropertyDescriptors(): java.util.List[PropertyDescriptor] = {
    properties.asJava
  }

  override def getRelationships(): java.util.Set[Relationship] = {
    relationships.asJava
  }

  //  override def onTrigger(context: ProcessContext, session: ProcessSession): Unit = {
  //    import java.io.IOException
  //
  //    import org.apache.nifi.processor.io.OutputStreamCallback
  //
  //    data.set(generateData(context))
  //    var flowFile = session.create //id, name, path
  //    if (data.get().length > 0) flowFile = session.write(flowFile, new OutputStreamCallback() {
  //      @throws[IOException]
  //      def process(out: OutputStream): Unit = {
  //        out.write(data.get())
  //      }
  //    })
  //
  //    session.putAttribute(flowFile, "id", "value")
  //    session.putAttribute(flowFile, "test", "work")
  //    session.getProvenanceReporter.create(flowFile)
  //    session.transfer(flowFile, RelSuccess)
  //  }

  override def onTrigger(context: ProcessContext, session: ProcessSession): Unit = {
    import java.io.IOException

    import org.apache.nifi.processor.io.OutputStreamCallback

    val files = getFileList(context)
    for (file <- files) {
      var flowFile = session.create
      session.putAttribute(flowFile, "ID", file._1.getId)
      session.putAttribute(flowFile, "name", file._1.getName)
      session.putAttribute(flowFile, "path", file._2)
      session.getProvenanceReporter.create(flowFile)
      session.transfer(flowFile, RelSuccess)
    }
  }
  def getFileList(context: ProcessContext) = {
    val id = context.getProperty("ID").evaluateAttributeExpressions().getValue
    val service = Drive_r.getDriveService
    Drive_r.listFiles(service, id)
  }
  protected[this] override def init(context: ProcessorInitializationContext): Unit = {
  }
}
