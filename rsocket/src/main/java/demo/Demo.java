package demo;

import io.rsocket.Payload;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class Demo {
	public static void main(String...a) {
		var demoServer = URI.create("wss://rsocket-demo.herokuapp.com/rsocket");
		// Adresse für die Transportschicht
		var ws = WebsocketClientTransport.create(demoServer);
		// Cient verbinden, hier blockierend, normalerweise asynchron
		var client = RSocketConnector.connectWith(ws).block();

		try {
			var payload = DefaultPayload.create("peace");
			// Stream anfordern
			Flux<Payload> s = client.requestStream(payload);

			// Wir brauchen eine Barriere, die solange blockiert, bist
			// der Stream fertig ist.
			var latch = new CountDownLatch(1);

			// 10 Elemente empfangen und als UTF-8 Strings ausgeben
			s.take(10)
				.doOnComplete(latch::countDown) // Barriere öffnen, sobald der Stream "fertig" ist
				.subscribe(p -> System.out.println(p.getDataUtf8())); // Ohne subscription fließen keine Daten.

			// Ohne Barriere wäre das Programm vor dem Stream zu Ende.
			latch.await();

		} catch (InterruptedException e) {
		} finally {
			client.dispose();
		}
	}
}
