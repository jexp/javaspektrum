package de.jexp.disruptor_http.server;


import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;

public class InvocationResponse {
    
    private String location;
    private HttpResponseStatus status;
    private String data;

    public void setCreated(String location) {
        setStatus(CREATED);
        this.setLocation(location);
    }
    
    public void setOk(){
        setStatus(OK);
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setOk(String value) {
        setOk();
        this.data = value;
    }

    public String getData() {
        return data;
    }
}
