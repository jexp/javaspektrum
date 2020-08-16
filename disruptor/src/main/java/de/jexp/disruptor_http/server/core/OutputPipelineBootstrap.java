package de.jexp.disruptor_http.server.core;

import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class OutputPipelineBootstrap {
    
    private static final int NUM_EVENT_PROCESSORS = 2;
    private static final int BUFFER_SIZE = 1024 * 2;

    private RingBuffer<ResponseEvent> ringBuffer;

    private CreateResponseHandler serializationHandler;

    private WorkProcessor<ResponseEvent> serializationProcessor;

    private ExecutorService workers;

    public OutputPipelineBootstrap(CreateResponseHandler serializationHandler) {
        this.serializationHandler = serializationHandler;
    }

    public void start() {
        ringBuffer = new RingBuffer<ResponseEvent>(
                ResponseEvent.FACTORY,
                BUFFER_SIZE,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.YIELDING);

        final SequenceBarrier serializationBarrier = ringBuffer.newBarrier();
        final AtomicLong workSequence = new AtomicLong(Sequencer.INITIAL_CURSOR_VALUE);

        serializationProcessor = new WorkProcessor<ResponseEvent>(ringBuffer, serializationBarrier, serializationHandler, new WorkExceptionHandler(), workSequence);
        ringBuffer.setGatingSequences(serializationProcessor.getSequence());

        workers = Executors.newFixedThreadPool(NUM_EVENT_PROCESSORS, new DaemonThreadFactory());
        workers.submit(serializationProcessor);
    }

    public void stop() {
        serializationProcessor.halt();
    }

    public RingBuffer<ResponseEvent> getRingBuffer() {
        return ringBuffer;
    }

}
