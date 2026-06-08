# camel-event-notifier-reproducer

The goal of this repository is to be a reproducer for an issue with the [camel-micrometer](https://camel.apache.org/components/4.18.x/micrometer-component.html) component when registering Prometheus metrics for a route, serving as support for the [Pull Request #23864](https://github.com/apache/camel/pull/23864), submitted to [Apache Camel project](https://github.com/apache/camel).

The following warning message is logged when a message is sent to a `SEDA` endpoint using Camel's `ProducerTemplate`:
```
2026-06-08 14:32:45,577 WARN  [org.apache.camel.support.EventHelper] (Camel (camel-6) thread #21 - seda://producer) Error notifying event C99DFEF70A95789-0000000000000001 exchange completed took: 3ms. This exception will be ignored.: java.lang.IllegalArgumentException: Prometheus requires that all meters with the same name have the same set of tag keys. There is already an existing meter named 'camel_exchange_event_notifier_seconds' containing tag keys [camelContext, endpointName, eventType, failed, kind]. The meter you are attempting to register has keys [camelContext, endpointName, eventType, failed, kind, routeId].
        at io.micrometer.prometheus.PrometheusMeterRegistry.lambda$throwExceptionOnRegistrationFailure$19(PrometheusMeterRegistry.java:619)
        at io.micrometer.core.instrument.MeterRegistry.meterRegistrationFailed(MeterRegistry.java:1291)
        at io.micrometer.prometheus.PrometheusMeterRegistry.lambda$applyToCollector$18(PrometheusMeterRegistry.java:593)
        at java.base/java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1956)
        at io.micrometer.prometheus.PrometheusMeterRegistry.applyToCollector(PrometheusMeterRegistry.java:579)
        at io.micrometer.prometheus.PrometheusMeterRegistry.newTimer(PrometheusMeterRegistry.java:328)
        at io.micrometer.core.instrument.MeterRegistry.lambda$timer$6(MeterRegistry.java:379)
        at io.micrometer.core.instrument.MeterRegistry.getOrCreateMeter(MeterRegistry.java:725)
        at io.micrometer.core.instrument.MeterRegistry.registerMeterIfNecessary(MeterRegistry.java:652)
        at io.micrometer.core.instrument.MeterRegistry.timer(MeterRegistry.java:377)
        at io.micrometer.core.instrument.Timer$Builder.register(Timer.java:471)
        at io.micrometer.core.instrument.Timer$Builder.register(Timer.java:465)
        at io.micrometer.core.instrument.composite.CompositeTimer.registerNewMeter(CompositeTimer.java:205)
        at io.micrometer.core.instrument.composite.CompositeTimer.registerNewMeter(CompositeTimer.java:35)
        at io.micrometer.core.instrument.composite.AbstractCompositeMeter.add(AbstractCompositeMeter.java:67)
        at java.base/java.lang.Iterable.forEach(Iterable.java:75)
        at java.base/java.util.Collections$SetFromMap.forEach(Collections.java:6060)
        at io.micrometer.core.instrument.composite.CompositeMeterRegistry.lambda$new$0(CompositeMeterRegistry.java:67)
        at io.micrometer.core.instrument.composite.CompositeMeterRegistry.lock(CompositeMeterRegistry.java:189)
        at io.micrometer.core.instrument.composite.CompositeMeterRegistry.lambda$new$1(CompositeMeterRegistry.java:67)
        at io.micrometer.core.instrument.MeterRegistry.getOrCreateMeter(MeterRegistry.java:735)
        at io.micrometer.core.instrument.MeterRegistry.registerMeterIfNecessary(MeterRegistry.java:652)
        at io.micrometer.core.instrument.MeterRegistry.timer(MeterRegistry.java:377)
        at io.micrometer.core.instrument.MeterRegistry.timer(MeterRegistry.java:534)
        at org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifier.handleDoneEvent(MicrometerExchangeEventNotifier.java:204)
        at org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifier.notify(MicrometerExchangeEventNotifier.java:179)
        at org.apache.camel.support.EventHelper.doNotifyEvent(EventHelper.java:1575)
        at org.apache.camel.support.EventHelper.notifyExchangeDone(EventHelper.java:783)
        at org.apache.camel.impl.engine.DefaultUnitOfWork.done(DefaultUnitOfWork.java:280)
        at org.apache.camel.support.UnitOfWorkHelper.doneUow(UnitOfWorkHelper.java:53)
        at org.apache.camel.impl.engine.CamelInternalProcessor$UnitOfWorkProcessorAdvice.after(CamelInternalProcessor.java:1178)
        at org.apache.camel.impl.engine.CamelInternalProcessor$UnitOfWorkProcessorAdvice.after(CamelInternalProcessor.java:1115)
        at org.apache.camel.impl.engine.AdviceIterator.runAfterTask(AdviceIterator.java:45)
        at org.apache.camel.impl.engine.AdviceIterator.runAfterTasks(AdviceIterator.java:39)
        at org.apache.camel.impl.engine.CamelInternalProcessor$AsyncAfterTask.done(CamelInternalProcessor.java:263)
        at org.apache.camel.AsyncCallback.run(AsyncCallback.java:44)
        at org.apache.camel.impl.engine.DefaultReactiveExecutor$Worker.doRun(DefaultReactiveExecutor.java:202)
        at org.apache.camel.impl.engine.DefaultReactiveExecutor$Worker.executeReactiveWork(DefaultReactiveExecutor.java:192)
        at org.apache.camel.impl.engine.DefaultReactiveExecutor$Worker.tryExecuteReactiveWork(DefaultReactiveExecutor.java:169)
        at org.apache.camel.impl.engine.DefaultReactiveExecutor$Worker.schedule(DefaultReactiveExecutor.java:143)
        at org.apache.camel.impl.engine.DefaultReactiveExecutor.scheduleMain(DefaultReactiveExecutor.java:59)
        at org.apache.camel.processor.Pipeline.process(Pipeline.java:162)
        at org.apache.camel.impl.engine.CamelInternalProcessor.processNonTransacted(CamelInternalProcessor.java:385)
        at org.apache.camel.impl.engine.CamelInternalProcessor.process(CamelInternalProcessor.java:361)
        at org.apache.camel.component.seda.SedaConsumer.sendToConsumers(SedaConsumer.java:351)
        at org.apache.camel.component.seda.SedaConsumer.processPolledExchange(SedaConsumer.java:267)
        at org.apache.camel.component.seda.SedaConsumer.doRun(SedaConsumer.java:210)
        at org.apache.camel.component.seda.SedaConsumer.run(SedaConsumer.java:146)
        at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
        at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
        at java.base/java.lang.Thread.run(Thread.java:1474)
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/camel-event-notifier-reproducer-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides


## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
