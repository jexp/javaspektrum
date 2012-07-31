package de.jexp.disruptor_http.database;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mh
 * @since 16.11.11
 */
public class Database {
    private final Map<String,String> store =new HashMap<String, String>();


    public String storeData(Map<String, String> data) {
        this.store.putAll(data);
        final String first = data.keySet().iterator().next();
        return first;
    }

    public String getData(String id) {
        return store.get(id);
    }
}
