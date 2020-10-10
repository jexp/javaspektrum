package rsocket;

import io.rsocket.*;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class Server {
    private final Disposable server;

    public Server() {
        this.server =
                RSocketServer.create(SocketAcceptor.with(new RSocketImpl()))
                        .bind(TcpServerTransport.create("localhost", 7000))
                        .block();
    }

    public void dispose() {
        this.server.dispose();
    }

    private static class RSocketImpl implements RSocket {

        /*
        request -> response
        The requestResponse method returns a single result for each request, as we can see by the Mono<Payload> response type.

        Payload is the class that contains message content and metadata. It's used by all of the interaction models.
        The content of the payload is binary, but there are convenience methods that support String-based content.
         */
        public Mono<Payload> requestResponse(Payload payload) {
            try {
                var text = payload.getDataUtf8().substring(1).toUpperCase();
                return Mono.just(DefaultPayload.create(text)); // reflect the payload back to the sender
            } catch (Exception x) {
                return Mono.error(x);
            }
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return Flux.fromStream(
                    payload.getDataUtf8().chars().sorted().mapToObj(Character::toString))
                    .map(DefaultPayload::create);
        }

        /*
        forward data received to a publisher
        no response required
         */
        SubmissionPublisher<Payload> eventPublisher = null; // acquireEventPublisher();
        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            System.err.printf("Received fire-and-forget %d%n",payload.getData().getInt());
            // eventPublisher.submit(payload); // forward the payload
            return Mono.empty();
        }

        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            Flux.from(payloads)
                    .subscribe(channelController::processPayload);
            return Flux.from(channelController);
        }

        ChannelController channelController = new ChannelController();
    }

    static class ChannelController implements Publisher<Payload> {
        List<Subscriber<? super Payload>> clients = new ArrayList<>();
        @Override
        public void subscribe(Subscriber<? super Payload> subscriber) {
            clients.add(subscriber);
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long l) {
                    System.out.printf("Got request(%d)%n",l);
                }

                @Override
                public void cancel() {
                    subscriber.onComplete();
                    clients.remove(subscriber);
                    clients.forEach(s -> s.onNext(DefaultPayload.create("Subscriber removed "+subscriber)));
                }
            });
        }

        public void processPayload(Payload payload) {
            System.out.println("received payload = " + payload.getDataUtf8());
            clients.forEach(s -> s.onNext(payload));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // A single server instance can handle multiple connections.
        // As a result, just one server instance will support all of our examples.
        Server server = new Server();
        Thread.currentThread().join();
    }
}