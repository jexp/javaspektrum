package de.jexp.disruptor_http.rest.service;


import de.jexp.disruptor_http.database.Database;

import java.util.Map;

public class CoreServiceActions {

    public String addData(Database database, Map<String, String> data) {
        return database.storeData(data);
    }

    public String getData(Database database, String id) {
        return database.getData(id);
    }

}
