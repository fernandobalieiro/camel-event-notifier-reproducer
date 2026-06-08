package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class ProducerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("seda://producer").routeId("producer-route")
                .log("Received message: ${body}");
    }
}
