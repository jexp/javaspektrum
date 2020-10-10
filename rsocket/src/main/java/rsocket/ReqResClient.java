package rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

public class ReqResClient {

    private final RSocket socket;

    public ReqResClient() {
        this.socket =
                RSocketConnector.create()
                        .connect(TcpClientTransport.create("localhost", 7000))
                        .block();

    }

    public String callBlocking(String text) {
        return socket
                .requestResponse(DefaultPayload.create(text))
                .map(Payload::getDataUtf8)
                .log()
                .block();
    }

    public void dispose() {
        this.socket.dispose();
    }

    public static void main(String[] args) {
        ReqResClient client = new ReqResClient();
        client.callBlocking("Hello RSocket!");
        client.callBlocking("");
        client.dispose();
    }
}