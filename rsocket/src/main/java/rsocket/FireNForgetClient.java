package rsocket;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class FireNForgetClient {
    private final RSocket socket;

    public FireNForgetClient() {
        this.socket = RSocketConnector
                .connectWith(TcpClientTransport.create("localhost", 7000))
                .block();
    }

    /** Send binary velocity (float) every 50ms */
    public void sendData() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        var data = IntStream.range(0,1000).boxed(); // .generate(rnd::nextInt)
        ByteBuffer buffer = ByteBuffer.allocate(4);
        Flux.fromStream(data)
                .delayElements(Duration.ofMillis(50))
                .take(25)
                .log()
                .map(d -> DefaultPayload.create(buffer.clear().putInt(d).rewind()))
                .flatMap(socket::fireAndForget)
                .blockLast();
    }

    public void dispose() {
        this.socket.dispose();
    }
    public static void main(String[] args) {
        FireNForgetClient client = new FireNForgetClient();
        client.sendData();
        client.dispose();
    }
}