package de.jexp.disruptor_http.server.core;

import de.jexp.disruptor_http.server.InvocationResponse;
import org.jboss.netty.channel.Channel;

import com.lmax.disruptor.EventFactory;

public class ResponseEvent {
    
    public static EventFactory<ResponseEvent> FACTORY = new EventFactory<ResponseEvent>() {
        public ResponseEvent newInstance() {
            return new ResponseEvent();
        }
    };
    
    private Channel outputChannel;

    private InvocationResponse response;

    public void setInvocationResponse(InvocationResponse response) {
        this.response = response;
    }
    
    public InvocationResponse getInvocationResponse() {
        return response;
    }

    public void setOutputChannel(Channel outputChannel) {
        this.outputChannel = outputChannel;
    }

    public Channel getOutputChannel() {
        return outputChannel;
    }
}
