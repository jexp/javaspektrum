package de.jexp.disruptor_http.server;

public interface Endpoint {
    public void invoke(InvocationRequest ctx, InvocationResponse response) throws Exception;
    public HttpVerb getVerb();
}
