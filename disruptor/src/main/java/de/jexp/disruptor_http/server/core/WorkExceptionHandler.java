package de.jexp.disruptor_http.server.core;

import com.lmax.disruptor.ExceptionHandler;
import org.apache.log4j.Logger;

public class WorkExceptionHandler implements ExceptionHandler {
    private final static Logger log=Logger.getLogger(ExceptionHandler.class);

    public void handle(Exception e, long sequence, Object data) {
        log.error(String.format("Error occured: %d %s",sequence,data),e);
    }
}
