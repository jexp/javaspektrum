package de.jexp.disruptor_http.rest.database;

import de.jexp.disruptor_http.rest.service.CoreService;
import de.jexp.disruptor_http.rest.service.EchoService;
import de.jexp.disruptor_http.server.RoutingDefinition;

public class DataAPI extends RoutingDefinition {
    public void setupRoutes() {
        addRoute("", new CoreService());
        addRoute("/echo", new EchoService());
    }
}