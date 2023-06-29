package demo;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class Server {
	public static void main(String...a) {
		var handler = new RSocket() {
			@Override
			// Mono statt Flux -> einzelne Antwort (Response)
			public Mono<Payload> requestResponse(Payload payload) {
				try {
					// Hilfsmethode um String aus Daten zu erzeugen
					var text = payload.getDataUtf8().substring(1).toUpperCase();
					// Und als Echo zurückgeben
					return Mono.just(DefaultPayload.create(text));
				} catch (Exception x) {
					// Fehler werden auch als Nachricht zurückgeschickt
					return Mono.error(x);
				}
			}
		};

		RSocketServer.create(SocketAcceptor.with(handler))
			.bind(TcpServerTransport.create("localhost", 7000)).block() // Binde an Port 7000
			.onClose().block(); // Starte den Server
	}
}
