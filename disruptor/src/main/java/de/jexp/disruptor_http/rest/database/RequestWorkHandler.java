package de.jexp.disruptor_http.rest.database;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import de.jexp.disruptor_http.server.core.ResponseEvent;

public class RequestWorkHandler implements WorkHandler<DatabaseWork> {
    
    private RingBuffer<ResponseEvent> output;
    

    public RequestWorkHandler(RingBuffer<ResponseEvent> output) {
        this.output = output;
    }

    @Override
    public void onEvent(DatabaseWork work) throws Exception {
        perform(work);
    }
    
    public void perform(DatabaseWork work) throws Exception {
        work.endpoint.invoke(work.request, work.response);
        long sequenceId = output.next();
        ResponseEvent ev = output.get(sequenceId);

        ev.setInvocationResponse(work.response);
        ev.setOutputChannel(work.request.getOutputChannel());
        output.publish(sequenceId);
    }

}
