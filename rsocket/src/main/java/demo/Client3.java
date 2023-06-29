package demo;

import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Client3 {
	public static void main(String... a) {
		var socket = RSocketConnector.create()
			.connect(TcpClientTransport.create("localhost", 7000))
			.block();

		var random = ThreadLocalRandom.current();
		var data = IntStream.generate(random::nextInt).boxed();
		ByteBuffer buffer = ByteBuffer.allocate(4);
		// Alle 50 millisekunden werden zufällige Daten geschickt
		Flux.fromStream(data)
			.delayElements(Duration.ofMillis(50))
			.take(25)
			.log()
			.map(d -> DefaultPayload.create(buffer.clear().putInt(d).rewind()))
			.flatMap(socket::fireAndForget)
			.as(StepVerifier::create)
			// Wir können nichts erwarten, da `fireAndForget` leere Monos zurück gibt.
			.verifyComplete();

		socket.dispose();
	}
}
