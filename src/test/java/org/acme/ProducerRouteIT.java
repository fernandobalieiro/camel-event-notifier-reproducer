package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.apache.camel.test.junit6.TestSupport.getMockEndpoint;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@QuarkusTest
@TestInstance(PER_CLASS)
public class ProducerRouteIT {

    @Inject
    CamelContext context;

    @Inject
    ProducerTemplate template;

    @BeforeAll
    void setup() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda://producer-test")//.routeId("producer-route")
                    .to("mock:result");
            }
        });
    }

    @Test
    void testWhenNoRouteIdIsSet() throws InterruptedException {
        getMockEndpoint(context, "mock:result", true).expectedBodiesReceived("Hello World");

        template.sendBody("seda://producer-test", "Hello World");

        MockEndpoint.assertIsSatisfied(context);
    }
}
