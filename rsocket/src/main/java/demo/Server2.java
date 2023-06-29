package demo;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Server2 {
	public static void main(String...a) {
		var handler = new RSocket() {
			@Override
			public Flux<Payload> requestStream(Payload payload) {
				return Flux.fromStream(
					payload.getDataUtf8().chars().sorted()
						.mapToObj(Character::toString))
					.map(DefaultPayload::create);
			}
		};

		RSocketServer.create(SocketAcceptor.with(handler))
			.bind(TcpServerTransport.create("localhost", 7000)).block() // Binde an Port 7000
			.onClose().block(); // Starte den Server
	}
}
