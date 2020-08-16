package de.jexp.disruptor_http.rest.service;

import de.jexp.disruptor_http.server.InvocationRequest;
import de.jexp.disruptor_http.server.InvocationResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class EchoService {

    @Path("")
    @POST
    public void echo(InvocationRequest req, InvocationResponse res) throws Exception {
        final String input = (String) req.getDeserializedContent();
        res.setOk(input);
    }
}
