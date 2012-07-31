package de.jexp.disruptor_http.server.core;

import de.jexp.disruptor_http.server.Router;

import com.lmax.disruptor.WorkHandler;

public class RoutingHandler implements WorkHandler<RequestEvent> {

    private Router router;

    public RoutingHandler(Router router) {
        this.router = router;
    }

    public void onEvent(final RequestEvent event)
            throws Exception {
        event.setEndpoint(router.route(event));
    }

}
