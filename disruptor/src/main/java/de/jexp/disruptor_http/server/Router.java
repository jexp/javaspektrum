package de.jexp.disruptor_http.server;

import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.PathTemplate;
import de.jexp.disruptor_http.server.core.RequestEvent;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

public class Router extends RoutingDefinition {

    private RouteEntry [] routes;
    private static final Logger logger=Logger.getLogger(Router.class);

    public void setupRoutes() { }
    
    public Endpoint route(RequestEvent event) throws Exception
    {
        String path = event.getPath();
        for(RouteEntry route : routes)
        {
            MatchResult matchResult = route.pattern.match(path);
            if(matchResult != null)
            {
                event.setPathVariables(new PathVariables(matchResult, route.pattern));
                Endpoint endpoint = route.getEndpoint(event.getVerb());
                if(endpoint != null) {
                    return endpoint;   
                }
                throw new ResourceNotFoundException("Path '" + path + "' does not support '"+event.getVerb()+"'." );
            }
        }
        throw new ResourceNotFoundException("Path '" + path + "' not found." );
    }
    
    public void compileRoutes() {
        Map<String, RouteEntry> routeMap = new LinkedHashMap<String, RouteEntry>();

        List<RouteDefinitionEntry> flatRoutes = getFlattenedRouteDefinition();
        for(RouteDefinitionEntry routeDef : flatRoutes)
        {
            if(!routeMap.containsKey(routeDef.getPath()))
            {
                RouteEntry route = new RouteEntry();
                final PathTemplate template = new PathTemplate(routeDef.getPath());
                route.pattern = new PathPattern(template,"");
                routeMap.put(routeDef.getPath(), route);
            }
            logger.debug("Adding Route: "+routeDef.getEndpoint().getVerb() +" to: "+ routeDef.getPath());
            
            RouteEntry route = routeMap.get(routeDef.getPath());
            route.setEndpoint(routeDef.getEndpoint().getVerb(), routeDef.getEndpoint());
        }
       
        routes = new RouteEntry[routeMap.size()];
        int i = 0;
        for(String path : routeMap.keySet()) {
            routes[i] = routeMap.get(path);
            i++;
        }
    }
}
