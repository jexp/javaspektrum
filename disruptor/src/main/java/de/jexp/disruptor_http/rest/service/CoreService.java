package de.jexp.disruptor_http.rest.service;

import de.jexp.disruptor_http.database.Database;
import de.jexp.disruptor_http.server.InvocationRequest;
import de.jexp.disruptor_http.server.InvocationResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Map;

public class CoreService {

    private CoreServiceActions actions = new CoreServiceActions();

    @Path("/get")
    @POST
    public void getData(InvocationRequest req, InvocationResponse res) throws Exception {
        String index = req.getDeserializedContent();
        Database database = req.getCtx(ContextKeys.DATABASE);
        final String value = actions.getData(database, index);
        res.setOk(value);
    }

    @POST
    @Path("/add")
    public void addData(InvocationRequest req, InvocationResponse res) throws Exception {
        Map<String, String> data = req.getDeserializedContent();
        Database database = req.getCtx(ContextKeys.DATABASE);
        final String first = actions.addData(database, data);

        res.setCreated("/data/"+first);
        //res.setOk();
    }
}
