package de.jexp.disruptor_http.server.core;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import de.jexp.disruptor_http.server.Endpoint;
import de.jexp.disruptor_http.server.HttpVerb;
import de.jexp.disruptor_http.server.PathVariables;

import com.lmax.disruptor.EventFactory;

public class RequestEvent {

    private HttpVerb verb;
    private String path;
    private ChannelBuffer content;
    private boolean isPersistentConnection;
    private PathVariables pathVariables;
    private Endpoint endpoint;
    private Channel outputChannel;
    private Object deserializedContent;
    
    public static EventFactory<RequestEvent> FACTORY = new EventFactory<RequestEvent>() {
        public RequestEvent newInstance() {
            return new RequestEvent();
        }
    };

    public void setVerb(HttpVerb verb) {
        this.verb = verb;
    }

    public HttpVerb getVerb() {
        return verb;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    } 

    public void setContent(ChannelBuffer content) {
        this.content = content;
    }

    public void setIsPersistentConnection(boolean isPersistentConnection) {
        this.isPersistentConnection = isPersistentConnection;
    }

    public void setPathVariables(PathVariables pathVariables) {
        this.pathVariables = pathVariables;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setOutputChannel(Channel channel) {
        this.outputChannel = channel;
    }

    public Channel getOutputChannel() {
        return outputChannel;
    }

    public PathVariables getPathVariables() {
        return pathVariables;
    }

    public ChannelBuffer getContent() {
        return content;
    }

    public void setDeserializedContent(Object deserialized) {
        this.deserializedContent = deserialized;
    }

    public Object getDeserializedContent() {
        return deserializedContent;
    }
    
    
}
