package rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.net.URI;

public class Client {

    public static void main(String[] args) {

        // Adresse für die Transportschicht
        var ws = WebsocketClientTransport.create(URI.create("wss://rsocket-demo.herokuapp.com/rsocket"));
        // Cient verbinden, hier blockierend, normalerweise asynchron
        var client = RSocketConnector.connectWith(ws).block();

        try {
            var payload = DefaultPayload.create("peace");
            // Stream anfordern
            Flux<Payload> s = client.requestStream(payload);

            // 10 Elemente empfangen und als UTF-8 Strings ausgeben
            s.take(10).doOnNext(p -> System.out.println(p.getDataUtf8()))
                    // terminale Operation triggered den eigentlichen Empfang,
                    // auch hier blockierend bis die 10 Nachrichten angekommen sind
                    .blockLast();
        } finally {
            client.dispose();
        }

    }
}
