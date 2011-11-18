package de.jexp.disruptor_http.server;

import com.sun.jersey.server.impl.uri.PathPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

public class PathVariables {
    
    private Map<String,String> pathVariables = new HashMap<String, String>();
    
    public PathVariables(MatchResult matched, PathPattern routePattern) {
        List<String> vars = routePattern.getTemplate().getTemplateVariables();
        for(int i=0,l=vars.size();i<l;i++) {
            pathVariables.put(vars.get(i), matched.group(i+1));
        }
    }

    public Long getParamAsLong(String key) {
        if (getParam(key)==null) {
            return null;
        }
        return Long.valueOf(getParam(key));
    }

    private String getParam(String key) {
        return pathVariables.get(key);
    }
}
