## Kamon metrics for Apache Camel

## Installation

In SBT:

```
libraryDependencies += "com.osinka.camel" %% "camel-kamon" % "1.0.0-SNAPSHOT"
```

## Usage in Camel

Unlike Kamon modules, this is a library that needs explicit configuration.

In Spring configuration:

```xml
<bean id="kamonRoutePolicyFactory" class="com.osinka.camel.kamon.KamonRoutePolicyFactory"/>
```

or programmatically:

```scala
import com.osinka.camel.kamon.KamonRoutePolicyFactory

camelContext.addRoutePolicyFactory(new KamonRoutePolicyFactory)
```

## Metrics

Only routes and processors with custom ID will be measured. Routes provide completion ("total", "success", "failure") metrics and trace contexts as well. Processors provide completion metrics and segments within the route's trace context.

## Kamon startup

Kamon needs to be started before Camel creates its routes. Use the provided `KamonService` to make sure Kamon is up if you don't use other mechanisms.

In Spring configuration:

```xml
<bean id="kamonService" class="com.osinka.camel.kamon.KamonService"/>
```

or programmatically:

```scala
import com.osinka.camel.kamon.KamonService

camelContext.addService(new KamonService)
```