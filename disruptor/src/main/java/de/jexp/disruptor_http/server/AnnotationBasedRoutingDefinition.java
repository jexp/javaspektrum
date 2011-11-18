package de.jexp.disruptor_http.server;

import javax.ws.rs.*;
import java.lang.reflect.Method;

public class AnnotationBasedRoutingDefinition extends RoutingDefinition {

    private final Object underlyingObject;

    private class MethodInvokingEndpoint implements Endpoint {

        private final Object underlyingObject;
        private final Method method;
        private final HttpVerb verb;

        public MethodInvokingEndpoint(HttpVerb verb, Method method, Object underlyingObject) {
            this.verb = verb;
            this.method = method;
            this.underlyingObject = underlyingObject;
        }

        public void invoke(InvocationRequest request,
                           InvocationResponse response) throws Exception {
            method.invoke(underlyingObject, request, response);
        }

        @Override
        public String toString() {
            return String.format("MethodInvokingEndpoint{method=%s, verb=%s}", method, verb);
        }

        @Override
        public HttpVerb getVerb() {
            return verb;
        }
    }

    public AnnotationBasedRoutingDefinition(Object obj) {
        this.underlyingObject = obj;
    }

    public void setupRoutes() {
        for (Method m : underlyingObject.getClass().getMethods()) {

            if (m.isAnnotationPresent(GET.class)) {
                addRoute(m, HttpVerb.GET);
            }

            if (m.isAnnotationPresent(PUT.class)) {
                addRoute(m, HttpVerb.PUT);
            }

            if (m.isAnnotationPresent(POST.class)) {
                addRoute(m, HttpVerb.POST);
            }

            if (m.isAnnotationPresent(DELETE.class)) {
                addRoute(m, HttpVerb.DELETE);
            }

        }
    }

    private void addRoute(final Method method, final HttpVerb verb) {
        String path = null;
        try {
            path = "";
            if (method.isAnnotationPresent(Path.class)) {
                path = method.getAnnotation(Path.class).value();
            }

            addRoute(path, new MethodInvokingEndpoint(verb, method, underlyingObject));

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create service from annotated class for path '"
                            + path + "'.", e);
        }
    }

}
