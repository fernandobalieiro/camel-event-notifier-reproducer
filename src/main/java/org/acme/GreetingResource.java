package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.apache.camel.ProducerTemplate;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/hello")
public class GreetingResource {

    @Inject
    ProducerTemplate producerTemplate;

    @GET
    @Produces(APPLICATION_JSON)
    public String hello() {
        producerTemplate.sendBody("seda://producer", "Hello from Camel");
        return "sent";
    }
}
