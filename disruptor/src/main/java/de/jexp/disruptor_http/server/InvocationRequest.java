package de.jexp.disruptor_http.server;

import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class InvocationRequest {

    private Map<String, Object> ctxObjects = new HashMap<String, Object>();
    private PathVariables pathVariables;
    private Object deserializedContent;
    private Channel outputChannel;

    public InvocationRequest() {
    }
    
    
    public void putCtx(String key, Object obj) {
        ctxObjects.put(key, obj);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getCtx(String key) {
        return (T)ctxObjects.get(key);
    }


    public PathVariables getPathVariables() {
        return pathVariables;
    }


    public void setPathVariables(PathVariables pathVariables) {
        this.pathVariables = pathVariables;
    }


    @SuppressWarnings("unchecked")
    public <T> T getDeserializedContent() {
        return (T)deserializedContent;
    }


    public void setDeserializedContent(Object deserializedContent) {
        this.deserializedContent = deserializedContent;
    }

    public void setOutputChannel(Channel outputChannel) {
        this.outputChannel = outputChannel;
    }

    public Channel getOutputChannel() {
        return outputChannel;
    }
}
