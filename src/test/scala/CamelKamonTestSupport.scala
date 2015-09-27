package com.osinka.camel.kamon

import scala.collection.convert.decorateAsScala._
import org.apache.camel.test.junit4.CamelTestSupport

abstract class CamelKamonTestSupport extends CamelTestSupport {
  override def createCamelContext = {
    val c = super.createCamelContext
    c.addService(new KamonService)
    c.addRoutePolicyFactory(new KamonRoutePolicyFactory)
    c
  }

  protected def traceContextRegistry =
    context.getRoutePolicyFactories.asScala.collect {
      case policyFactory: KamonRoutePolicyFactory => policyFactory.traceContexts
    }.head
}
