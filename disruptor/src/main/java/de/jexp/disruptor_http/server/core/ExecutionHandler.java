package de.jexp.disruptor_http.server.core;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;

public interface ExecutionHandler extends WorkHandler<RequestEvent> {

   void setOutputBuffer(RingBuffer<ResponseEvent> outputBuffer);

    void start();

    void stop();
}
