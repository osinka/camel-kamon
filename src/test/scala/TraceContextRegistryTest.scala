package com.osinka.camel.kamon

import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy
import org.junit.Test
import org.scalatest.Matchers
import org.scalatest.junit.AssertionsForJUnit
import org.apache.camel.test.junit4.CamelTestSupport

class TraceContextRegistryTest extends CamelKamonTestSupport with Matchers with AssertionsForJUnit {
  val startEndpt = "direct:start"

  @EndpointInject(uri = "mock:result")
  var resultMock: MockEndpoint = _

  override def createRouteBuilder = new RouteBuilder() {
    override def configure(): Unit = {
      val queue = "seda:queue?multipleConsumers=true&size=2&blockWhenFull=true"

      from(startEndpt).routeId("routeStart")
        .setBody(body().append("S"))
        .to(queue)

      from(queue).routeId("queueProcessor1")
        .setBody(body().append("P1"))
        .to("mock:result")

      from(queue).routeId("queueProcessor2")
        .setBody(body().append("P2"))
        .to("mock:result")
    }
  }

  @Test
  def noleaksTest(): Unit = {
    val Iterations = 200

    resultMock.expectedMessageCount(Iterations*2)

    (1 to Iterations).foreach { _ => template().sendBody(startEndpt, "body") }

//    resultMock.setAssertPeriod(1000)
    assertMockEndpointsSatisfied()

    traceContextRegistry.size should be (0)
  }
}
