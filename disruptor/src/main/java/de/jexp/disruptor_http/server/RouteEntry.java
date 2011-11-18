package de.jexp.disruptor_http.server;

import com.sun.jersey.server.impl.uri.PathPattern;

public class RouteEntry {

    protected PathPattern pattern;
    
    protected Endpoint getEndpoint;
    protected Endpoint putEndpoint;
    protected Endpoint postEndpoint;
    protected Endpoint deleteEndpoint;
    protected Endpoint headEndpoint;
    
    public void setEndpoint(HttpVerb verb, Endpoint endpoint) {
        System.out.println(this);
        switch(verb) {
        case GET:
            getEndpoint = endpoint;
            break;
        case PUT:
            putEndpoint = endpoint;
            break;
        case POST:
            postEndpoint = endpoint;
            break;
        case DELETE:
            deleteEndpoint = endpoint;
            break;
        case HEAD:
            headEndpoint = endpoint;
            break;
        }
    }
    
    public Endpoint getEndpoint(HttpVerb verb) {
        switch(verb) {
        case GET:
            return getEndpoint;
        case PUT:
            return putEndpoint;
        case POST:
            return postEndpoint;
        case DELETE:
            return deleteEndpoint;
        case HEAD:
            return headEndpoint;
        }
        return null;
    }
    
    public String toString() {
        return "Route ["+pattern+"] {GET:"+getEndpoint+", PUT:"+putEndpoint+", POST:"+postEndpoint+", DELETE:"+deleteEndpoint+", HEAD:"+headEndpoint+"}";
    }
}
