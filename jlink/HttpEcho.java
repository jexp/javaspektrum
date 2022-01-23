package de.jexp.jlink;

import java.util.logging.Logger;
import java.net.http.*;
import java.net.URI;

public class HttpEcho {

    private static Logger LOG = Logger.getLogger("echo");

    public static void main(String...args) throws Exception {
        var request = HttpRequest.newBuilder()
        .uri(new URI("https://postman-echo.com/get"))
        .GET().build();

        var client = HttpClient.newHttpClient();

        var response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        LOG.info("status "+response.statusCode());
        LOG.info(response.body());
    }
}
