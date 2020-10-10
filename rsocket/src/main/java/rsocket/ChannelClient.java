package rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChannelClient {
    private final static AtomicInteger client = new AtomicInteger();

    static class ChannelController implements Publisher<Payload> {
        private final String name;
        private final Stream<Integer> stream = IntStream.range(0,10).boxed(); // .generate(() -> ThreadLocalRandom.current().nextInt(10)).boxed();

        public ChannelController(String name) {
            this.name = name;
        }

        // sending data to clients
        @Override
        public void subscribe(Subscriber<? super Payload> subscriber) {
            // finite stream
            Stream<Integer> stream = IntStream.range(0,10).boxed();
            Flux.fromStream(stream)
                    .delayElements(Duration.ofMillis(1000))
                    .map(i -> DefaultPayload.create(String.format("%s: %d", name, i)))
                    .doOnComplete(subscriber::onComplete) // doesn't help
                    .doOnTerminate(subscriber::onComplete) // doesn't help
                    .subscribe(subscriber);
        }

        // data from the other clients
        public void processPayload(Payload payload) {
            System.out.println("[" + name + "] received " + payload.getDataUtf8());
        }
    }

    private final RSocket socket;
    private final ChannelController channelController;

    public ChannelClient() {
        this.socket = RSocketConnector
                .connectWith(TcpClientTransport.create("localhost", 7000))
                .block();
        this.channelController = new ChannelController("Client " + Instant.now());
    }


    public void run() {
        socket.requestChannel(Flux.from(channelController))
                .doOnNext(channelController::processPayload)
                .blockLast();
    }

    public void dispose() {
        this.socket.dispose();
    }

    public static void main(String[] args) throws IOException {
        ChannelClient client = new ChannelClient();
        client.run();
        client.dispose();
    }
}