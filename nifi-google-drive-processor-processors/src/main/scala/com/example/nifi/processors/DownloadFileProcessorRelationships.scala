package com.example.nifi.processors

import org.apache.nifi.processor.Relationship

trait DownloadFileProcessorRelationships {
  val RelSuccess: Relationship =
    new Relationship.Builder()
      .name("success")
      .description("""Any FlowFile that is successfully transferred is routed to this relationship""".trim)
      .build

  val RelFailure: Relationship =
    new Relationship.Builder()
      .name("failure")
      .description("""Any FlowFile that fails to be transferred is routed to this relationship""".trim)
      .build

  lazy val relationships = Set(RelSuccess, RelFailure)
}

object DownloadFileProcessorRelationships extends DownloadFileProcessorRelationships {
}
