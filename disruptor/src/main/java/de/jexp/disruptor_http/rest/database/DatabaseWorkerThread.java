package de.jexp.disruptor_http.rest.database;

import de.jexp.disruptor_http.database.Database;
import de.jexp.disruptor_http.rest.service.BufferedWorkExecutor;
import de.jexp.disruptor_http.rest.service.ContextKeys;
import de.jexp.disruptor_http.server.InvocationRequest;
import de.jexp.disruptor_http.server.core.RequestEvent;
import de.jexp.disruptor_http.server.core.ResponseEvent;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;

public class DatabaseWorkerThread extends BufferedWorkExecutor<DatabaseWork> {

    private Database database;
    
    public DatabaseWorkerThread(Database database,
                                RingBuffer<ResponseEvent> output, ExceptionHandler exceptionHandler) {
        super(new RequestWorkHandler(output), DatabaseWork.FACTORY, exceptionHandler);
        this.database = database;
    }
    
    public void addWork(RequestEvent event) {
        long sequenceId = workBuffer.next();
        DatabaseWork work = workBuffer.get(sequenceId);
        
        work.endpoint = event.getEndpoint();

        InvocationRequest request = work.request;
        request.setPathVariables(event.getPathVariables());
        request.setDeserializedContent(event.getDeserializedContent());
        request.setOutputChannel(event.getOutputChannel());
        request.putCtx(ContextKeys.DATABASE, database);
        
        workBuffer.publish(sequenceId);
    }

}
