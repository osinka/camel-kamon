package com.osinka.camel.kamon

import org.apache.camel.IsSingleton
import org.apache.camel.support.ServiceSupport
import kamon.Kamon

/*
 * A bean to start Kamon automatically on the Camel context startup
 *
 * <bean id="kamonService" class="com.osinka.camel.kamon.KamonService"/>
 *
 * OR
 *
 * camelContext.addService(new KamonService)
 *
 */
class KamonService extends ServiceSupport with IsSingleton {
  override val isSingleton = true

  Kamon.start()

  override def doStart(): Unit = {
  }

  override def doStop(): Unit = {
    Kamon.shutdown()
  }
}
