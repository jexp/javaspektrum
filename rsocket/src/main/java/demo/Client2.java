package demo;

import io.rsocket.Payload;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.test.StepVerifier;

public class Client2 {
	public static void main(String... a) {
		var socket = RSocketConnector
			.connectWith(TcpClientTransport.create("localhost", 7000))
			.block();

		var text = "Hello RSocket!";

		socket
			.requestStream(DefaultPayload.create(text))
			.map(Payload::getDataUtf8)
			.log()
			.collectList()
			.map(chars -> String.join("", chars))
			.as(StepVerifier::create)
			.expectNext(" !HRSceeklloot")
			.verifyComplete();

		socket.dispose();
	}
}
