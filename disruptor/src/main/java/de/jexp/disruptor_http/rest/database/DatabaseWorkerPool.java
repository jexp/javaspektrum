package de.jexp.disruptor_http.rest.database;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import de.jexp.disruptor_http.database.Database;
import de.jexp.disruptor_http.server.core.ExecutionHandler;
import de.jexp.disruptor_http.server.core.RequestEvent;
import de.jexp.disruptor_http.server.core.ResponseEvent;
import de.jexp.disruptor_http.server.core.WorkExceptionHandler;
import org.apache.log4j.Logger;

public class DatabaseWorkerPool implements ExecutionHandler {

    private final static Logger logger = Logger.getLogger(DatabaseWorkerPool.class);

    private static final int NUM_DATABASE_WORK_EXECUTORS = 4;
    
    private RingBuffer<ResponseEvent> output;
    private Database database;
    
    private DatabaseWorkerThread [] workers = new DatabaseWorkerThread[NUM_DATABASE_WORK_EXECUTORS];

    private ExceptionHandler exceptionHandler;

    public DatabaseWorkerPool(Database database) {
        this.database = database;
        this.exceptionHandler = new WorkExceptionHandler();
    }
    
    @Override
    public void start() {
        // Set up workers
        for(int i=0; i<NUM_DATABASE_WORK_EXECUTORS;i++) {
            DatabaseWorkerThread worker = new DatabaseWorkerThread(database, output, exceptionHandler);
            workers[i] = worker;
            worker.start();
        }
    }
    
    @Override
    public void stop() {
        for(DatabaseWorkerThread worker : workers) {
            stopWorker(worker);
        }
    }

    private void stopWorker(DatabaseWorkerThread worker) {
        try {
            if (worker==null) {
                logger.warn("Worker is null");
                return;
            }
            worker.stop();
        } catch(Exception e) {
            logger.error("Error stopping worker", e);
        }
    }

    @Override
    public void setOutputBuffer(RingBuffer<ResponseEvent> output) {
        this.output = output;
    }

    @Override
    public void onEvent(RequestEvent event) throws Exception {
        DatabaseWorkerThread worker = randomWorker();
            worker.addWork(event);
    }

    private DatabaseWorkerThread randomWorker() {
        int workerId = (int) (Math.random() * NUM_DATABASE_WORK_EXECUTORS);
        return workers[workerId];
    }

}
