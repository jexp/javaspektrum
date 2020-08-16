package de.jexp.disruptor_http.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public abstract class RoutingDefinition {
    
    protected LinkedHashMap<String, Object> routeDefs = new LinkedHashMap<String, Object>();
    
    public abstract void setupRoutes();
    
    public void addRoute(String route, Endpoint target) {
        routeDefs.put(route, target);
    }
    
    public void addRoute(String route, RoutingDefinition target) {
        routeDefs.put(route, target);
    }
        
    public void addRoute(String route, Object target) {
        routeDefs.put(route, new AnnotationBasedRoutingDefinition(target));
    }
    
    /**
     * Traverses the tree of routes of this RoutingDefinition,
     * and returns a list of route -> pipeline objects, with all
     * nested RoutingDefinitions resolved into their respective routes.
     */
    public List<RouteDefinitionEntry> getFlattenedRouteDefinition() {
        List<RouteDefinitionEntry> compoundRoutes = new ArrayList<RouteDefinitionEntry>();
        
        for(String route : routeDefs.keySet()) {
            
            Object target = routeDefs.get(route);
            
            if(target instanceof RoutingDefinition) {
                
                RoutingDefinition subRouter = (RoutingDefinition)target;
                
                subRouter.clearRoutes();
                subRouter.setupRoutes();
                
                List<RouteDefinitionEntry> subRoutes = subRouter.getFlattenedRouteDefinition();
                for(RouteDefinitionEntry subRoute : subRoutes) {
                    compoundRoutes.add(new RouteDefinitionEntry(route + subRoute.getPath(), subRoute.getEndpoint()));
                }
                
            } else {
                compoundRoutes.add(new RouteDefinitionEntry(route, (Endpoint)target));
            }
        }
        
        return compoundRoutes;
    }
    
    protected void clearRoutes() {
        routeDefs = new LinkedHashMap<String, Object>();
    }
}
