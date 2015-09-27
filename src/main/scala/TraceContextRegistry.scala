package com.osinka.camel.kamon

import java.util.concurrent.ConcurrentHashMap
import org.apache.camel.{Exchange, Route}
import kamon.Kamon
import kamon.trace.TraceContext

class TraceContextRegistry {
  case class ContextId(routeId: String, exchangeId: String)
  private val contexts = new ConcurrentHashMap[ContextId, TraceContext]()

  Kamon.metrics.gauge("camel-contexts-registry-size") { size.toLong }

  def put(route: Route, exchange: Exchange, traceContext: TraceContext): Unit = {
    put(route.getRouteContext.getRoute.getId, exchange.getExchangeId, traceContext)
  }

  def put(routeId: String, exchangeId: String, traceContext: TraceContext): Unit = {
    contexts.put(ContextId(routeId, exchangeId), traceContext)
  }

  def get(route: Route, exchange: Exchange): TraceContext =
    get(route.getRouteContext.getRoute.getId, exchange.getExchangeId)

  def get(routeId: String, exchangeId: String) =
    contexts.get(ContextId(routeId, exchangeId))

  def pull(route: Route, exchange: Exchange): TraceContext =
    pull(route.getRouteContext.getRoute.getId, exchange.getExchangeId)

  def pull(routeId: String, exchangeId: String) =
    contexts.remove(ContextId(routeId, exchangeId))

  def size = contexts.size
}
