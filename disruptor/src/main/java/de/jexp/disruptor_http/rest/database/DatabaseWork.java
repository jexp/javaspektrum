package de.jexp.disruptor_http.rest.database;

import de.jexp.disruptor_http.server.Endpoint;
import de.jexp.disruptor_http.server.InvocationRequest;
import de.jexp.disruptor_http.server.InvocationResponse;

import com.lmax.disruptor.EventFactory;

public class DatabaseWork {
    
    public static EventFactory<DatabaseWork> FACTORY = new EventFactory<DatabaseWork>() {
        public DatabaseWork newInstance() {
            return new DatabaseWork();
        }
    };
    
    public boolean isTransactional = false;
    public long txId = -1l;
    
    public InvocationRequest request = new InvocationRequest();
    public InvocationResponse response = new InvocationResponse();

    public Endpoint endpoint;

    /**
     * True if this unit of work is expected to be performed within
     * an already existing and ongoing transaction.
     */
    public boolean usesTxAPI;

}
