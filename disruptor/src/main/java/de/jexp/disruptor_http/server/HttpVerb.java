package de.jexp.disruptor_http.server;

import org.jboss.netty.handler.codec.http.HttpRequest;

public enum HttpVerb {
    GET,
    PUT,
    POST,
    DELETE,
    HEAD;

    public static HttpVerb from(HttpRequest httpRequest) {
        return valueOf(httpRequest.getMethod().getName().toUpperCase());
    }
}
