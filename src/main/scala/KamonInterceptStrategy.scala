package com.osinka.camel.kamon

import org.apache.camel._
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.processor.DelegateAsyncProcessor
import org.apache.camel.spi.InterceptStrategy
import kamon.Kamon

class KamonInterceptStrategy(routeId: String, traceContexts: TraceContextRegistry) extends InterceptStrategy {
  val category = "route"

  override def wrapProcessorInInterceptors(context: CamelContext, definition: ProcessorDefinition[_], target: Processor, nextTarget: Processor): Processor =
    if (target.isInstanceOf[KamonSegmentWrapperProcessor])
    // dont double wrap
      target
    else if (definition.hasCustomIdAssigned) {
      val wrapper = new KamonSegmentWrapperProcessor(definition.getId)
      wrapper.setProcessor(target)
      wrapper
    } else
      target

  class KamonSegmentWrapperProcessor(processorId: String) extends DelegateAsyncProcessor {
    val segmentName = s"processor:${KamonCamel.hyphenateName(processorId)}"
    lazy val metrics = Kamon.metrics.entity(ProcessorEntity, KamonCamel.hyphenateName(processorId))

    override def process(exchange: Exchange, callback: AsyncCallback): Boolean = {
      val traceContext = traceContexts.get(routeId, exchange.getExchangeId)
      val segment = traceContext.startSegment(segmentName, category, "camel")

      processor.process(exchange, new AsyncCallback {
        override def done(doneSync: Boolean) = {
          segment.finish()

          metrics.total.increment()
          if (exchange.isFailed)
            metrics.failed.increment()
          else
            metrics.completed.increment()

          callback.done(doneSync)
        }
      })
    }
  }

}