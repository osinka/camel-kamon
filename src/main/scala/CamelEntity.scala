package com.osinka.camel.kamon

import kamon.metric.{EntityRecorderFactory, GenericEntityRecorder}
import kamon.metric.instrument.InstrumentFactory

class CamelEntity(instrumentFactory: InstrumentFactory) extends GenericEntityRecorder(instrumentFactory) {
  val total     = counter("total")
  val completed = counter("completed")
  val failed    = counter("failed")
}

object RouteEntity extends EntityRecorderFactory[CamelEntity] {
  override def category = "route"
  override def createRecorder(instrumentFactory: InstrumentFactory) = new CamelEntity(instrumentFactory)
}

object ProcessorEntity extends EntityRecorderFactory[CamelEntity] {
  override def category = "processor"
  override def createRecorder(instrumentFactory: InstrumentFactory) = new CamelEntity(instrumentFactory)
}