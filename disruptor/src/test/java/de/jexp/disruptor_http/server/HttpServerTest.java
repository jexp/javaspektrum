package de.jexp.disruptor_http.server;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.jexp.disruptor_http.database.Database;
import de.jexp.disruptor_http.rest.database.DataAPI;
import de.jexp.disruptor_http.rest.database.DatabaseWorkerPool;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * @author mh
 * @since 14.11.11
 */
public class HttpServerTest {
    public static final String HOST = "localhost";
    public static final int PORT = 7476;
    public static final String BASE_URI = "http://" + HOST + ":" + PORT + "/";
    static HttpServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = new HttpServer(HOST, PORT,new DatabaseWorkerPool(new Database()));
        server.addRoute("",new DataAPI());
        server.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testGetEcho() throws Exception {
        final String uri = BASE_URI+ "echo";
        WebResource resource = Client.create().resource(uri);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("\"echo-test\"")
                .post(ClientResponse.class);

        final int status = response.getStatus();
        final String result = response.getEntity(String.class);
        System.out.printf("GET to [%s], status code [%d], returned data: %n%s",
                uri, status, result);
        response.close();
        assertEquals("echo returned 200", 200, status);
        assertEquals("echo returned test","echo-test",result);
    }

    @Test
    public void testCreateNode() throws Exception {
        final URI location = createData("{\"0\":\"john\",\"1\":\"michael\"}");
        assertEquals("info returned location for new node", "/data/0", location.toString());
    }

    private URI createData(String data) {
        final String uri = BASE_URI+"add";
        WebResource resource = Client.create().resource(uri);
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( data )
                .post( ClientResponse.class );

        final int status = response.getStatus();
        final URI location = response.getLocation();
        final String entity = response.getEntity(String.class);
        System.out.printf("POST [%s] to [%s], status code [%d] location: %s, returned data: %n%s",
                data, uri, status, location, entity);
        response.close();
        assertEquals("info returned 201", 201, status);
        return location;
    }

    @Test
    public void testGetNode() throws Exception {
        createData("{\"0\":\"john\"}");
        final String data = getData(0);
        assertEquals("info returned location for new node", "john", data);
    }

    private String getData(int id) {
        final String uri = BASE_URI+"get";
        WebResource resource = Client.create().resource(uri);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("\"0\"")
                .get(ClientResponse.class);

        final int status = response.getStatus();
        final URI location = response.getLocation();
        final String entity = response.getEntity(String.class);
        System.out.printf("GET from [%s], status code [%d] location: %s, returned data: %n%s",
                 uri, status, location, entity);
        response.close();
        assertEquals("info returned 201", 200, status);
        return entity;
    }
}
