package de.jexp.disruptor_http.server;

import de.jexp.disruptor_http.database.Database;
import de.jexp.disruptor_http.rest.database.DataAPI;
import de.jexp.disruptor_http.rest.database.DatabaseWorkerPool;
import de.jexp.disruptor_http.server.core.*;
import de.jexp.disruptor_http.server.http.NettyHttpPipelineFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioServerSocketChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServer {

    private int port;
    private String host;
    private Router router = new Router();
    private ServerBootstrap netty;
    private PipelineBootstrap<ResponseEvent> outputPipeline;
    private PipelineBootstrap<RequestEvent> inputPipeline;
    private ExecutionHandler executionHandler;
    private ServerSocketChannelFactory channelFactory;
    private ChannelGroup openChannels = new DefaultChannelGroup("HttpServer");

    public static void main(String[] args) throws IOException {
        final HttpServer server = new HttpServer("localhost", 6666, new DatabaseWorkerPool(new Database()));
        server.addRoute("",new DataAPI());
        server.start();
        System.out.println("Started Server on port "+6666);
        System.in.read();
        server.stop();
    }
    public HttpServer(String host, int port, ExecutionHandler executionHandler) {
        this.host = host;
        this.port = port;
        this.executionHandler = executionHandler;
    }

    public void start() {
        router.compileRoutes();

        // OUTPUT PIPELINE

        CreateResponseHandler responseHandler = new CreateResponseHandler();
        outputPipeline = new PipelineBootstrap<ResponseEvent>(ResponseEvent.FACTORY, responseHandler);
        outputPipeline.start();

        executionHandler.setOutputBuffer(outputPipeline.getRingBuffer());
        executionHandler.start();

        // INPUT PIPELINE

        RoutingHandler routingHandler = new RoutingHandler(router);
        DeserializationHandler deserializationHandler = new DeserializationHandler();

        inputPipeline = new PipelineBootstrap<RequestEvent>(RequestEvent.FACTORY, routingHandler, deserializationHandler, executionHandler);
        inputPipeline.start();


        // NETTY 

        // Potential config setting: Pick between old-fashioned or async sockets
        // OioServerSocketChannelFactory vs NioServerSocketChannelFactory
        // Old sockets are superior when handling < 1000 clients
        channelFactory =
            new OioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());
        netty = new ServerBootstrap(channelFactory);

        // Set up the event pipeline factory.
        netty.setPipelineFactory(new NettyHttpPipelineFactory(inputPipeline.getRingBuffer(), openChannels));

        // Bind and start to accept incoming connections.
        openChannels.add(netty.bind(new InetSocketAddress(host, port)));

    }

    public void stop() {
        if (openChannels!=null) openChannels.close().awaitUninterruptibly();
        if (channelFactory!=null) channelFactory.releaseExternalResources();
        if (executionHandler!=null) executionHandler.stop();
        if (inputPipeline!=null) inputPipeline.stop();
        if (outputPipeline!=null) outputPipeline.stop();
    }

    public void addRoute(String route, RoutingDefinition target) {
        router.addRoute(route, target);
    }

    public void addRoute(String route, Endpoint target) {
        router.addRoute(route, target);
    }

    public void addRoute(String route, Object target) {
        router.addRoute(route, target);
    }
}
