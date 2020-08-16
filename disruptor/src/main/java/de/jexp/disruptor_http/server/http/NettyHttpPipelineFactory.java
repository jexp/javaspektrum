package de.jexp.disruptor_http.server.http;

import de.jexp.disruptor_http.server.core.RequestEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.lmax.disruptor.RingBuffer;

public class NettyHttpPipelineFactory implements ChannelPipelineFactory {

    private RingBuffer<RequestEvent> workBuffer;
    private ChannelGroup openChannels;

    public NettyHttpPipelineFactory(RingBuffer<RequestEvent> workBuffer, ChannelGroup openChannels) {
        this.workBuffer = workBuffer;
        this.openChannels = openChannels;
    }
    
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // Uncomment the following line if you want HTTPS
        // SSLEngine engine =
        // SecureChatSslContextFactory.getServerContext().createSSLEngine();
        // engine.setUseClientMode(false);
        // pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("channeltracker",new NettyChannelTrackingHandler(openChannels));
        pipeline.addLast("decoder",       new HttpRequestDecoder());
        pipeline.addLast("aggregator",    new HttpChunkAggregator(65536));
        pipeline.addLast("encoder",       new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("handler",       new NettyHttpHandler(workBuffer));
        return pipeline;
    }
    
}
