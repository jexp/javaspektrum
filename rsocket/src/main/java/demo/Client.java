package demo;

import io.rsocket.Payload;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.test.StepVerifier;

public class Client {
	public static void main(String... a) {
		var socket = RSocketConnector.create()
			.connect(TcpClientTransport.create("localhost", 7000))
			.block();

		var text = "Hello RSocket!";

		socket.requestResponse(DefaultPayload.create(text))
			.map(Payload::getDataUtf8)
			.log()
			.as(StepVerifier::create)
			.expectNextCount(1)
			.verifyComplete();

		socket.requestResponse(DefaultPayload.create(""))
			.doOnError(System.err::println)
			.as(StepVerifier::create)
			.expectError()
			.verify();

		socket.dispose();
	}
}
