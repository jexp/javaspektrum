package rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.Disposable;

import java.io.IOException;
import java.sql.SQLOutput;

public class ReqStreamClient {

    private final RSocket socket;

    public ReqStreamClient() {
        this.socket =
                RSocketConnector.create()
                        .connect(TcpClientTransport.create("localhost", 7000))
                        .block();

    }

    public String streamText(String text) {
        return socket
                .requestStream(DefaultPayload.create(text))
                .map(Payload::getDataUtf8)
                .log()
                .collectList()
                .map(chars -> String.join("", chars))
                .block();
    }

    public Disposable streamTextAsync(String text) {
        return socket
                .requestStream(DefaultPayload.create(text))
                .map(Payload::getDataUtf8)
                .subscribe( ch -> {
                    System.out.print(ch);
                });
    }

    public void dispose() {
        this.socket.dispose();
    }

    public static void main(String[] args) throws IOException {
        ReqStreamClient client = new ReqStreamClient();
        var result = client.streamText("Hello RSocket!");
        System.out.println("result = " + result);
        var subscription = client.streamTextAsync("JavaSpektrum");
        System.out.println("Hit return to finish.");
        System.in.read();
        subscription.dispose();
        client.dispose();
    }
}