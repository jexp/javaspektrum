package demo;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.SubmissionPublisher;

public class Server3 {
	public static void main(String...a) {
		var handler = new RSocket() {

			SubmissionPublisher<Payload> eventPublisher = new SubmissionPublisher<>();

			@Override
			public Mono<Void> fireAndForget(Payload payload) {
				System.err.printf("Received fire-and-forget %d%n",payload.getData().getInt());
				// weiterleiten, z.B. zu Event Benachrichtigungen
				eventPublisher.submit(payload);
				return Mono.empty();
			}
		};

		RSocketServer.create(SocketAcceptor.with(handler))
			.bind(TcpServerTransport.create("localhost", 7000)).block() // Binde an Port 7000
			.onClose().block(); // Starte den Server
	}
}
