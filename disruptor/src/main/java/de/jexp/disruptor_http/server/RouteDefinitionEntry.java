package de.jexp.disruptor_http.server;

public class RouteDefinitionEntry {

    private final String path;
    private final Endpoint endpoint;

    public RouteDefinitionEntry(String path, Endpoint endpoint) {
        this.path = path;
        this.endpoint = endpoint;
    }

    public String getPath() {
        return path;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return String.format("RouteDefinitionEntry{path='%s', endpoint=%s}", path, endpoint);
    }
}
