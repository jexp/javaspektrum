package de.jexp.disruptor_http.server;

import de.jexp.disruptor_http.server.core.RequestEvent;
import de.jexp.disruptor_http.server.core.ResponseEvent;

import com.lmax.disruptor.RingBuffer;

public interface EndpointExecutor {

    void setOutput(RingBuffer<ResponseEvent> ringBuffer);

    void handle(RequestEvent event);

}
