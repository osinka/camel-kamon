package com.osinka.camel.kamon

import org.apache.camel.model.RouteDefinition
import org.apache.camel.spi.{RoutePolicy, RoutePolicyFactory}
import org.apache.camel.support.RoutePolicySupport
import org.apache.camel.{CamelContext, Exchange, Route}
import kamon.Kamon

/*
 * Route policy to create Kamon TraceContext for routes that have custom IDs
 *
 * <bean id="kamonRoutePolicyFactory" class="com.osinka.camel.kamon.KamonRoutePolicyFactory"/>
 *
 * OR
 *
 * camelContext.addRoutePolicyFactory(new KamonRoutePolicyFactory)
 *
 */
class KamonRoutePolicyFactory extends RoutePolicyFactory {
  lazy val traceContexts = new TraceContextRegistry

  override def createRoutePolicy(camelContext: CamelContext, routeId: String, routeDefinition: RouteDefinition): RoutePolicy =
    if (routeDefinition.hasCustomIdAssigned) {
      val policy = new TracingRoutePolicy(routeId, traceContexts)
      routeDefinition.addInterceptStrategy(new KamonInterceptStrategy(routeId, traceContexts))
      policy
    } else {
      null
    }
}

class TracingRoutePolicy(routeId: String, traceContexts: TraceContextRegistry) extends RoutePolicySupport {
  lazy val traceName = s"route:${KamonCamel.hyphenateName(routeId)}"
  lazy val metrics = Kamon.metrics.entity(RouteEntity, KamonCamel.hyphenateName(routeId))

  override def onExchangeBegin(route: Route, exchange: Exchange): Unit = {
    traceContexts.put(route, exchange, Kamon.tracer.newContext(traceName))
  }

  override def onExchangeDone(route: Route, exchange: Exchange): Unit = {
    traceContexts.pull(route, exchange).finish()

    metrics.total.increment()
    if (exchange.isFailed)
      metrics.failed.increment()
    else
      metrics.completed.increment()
  }

}
