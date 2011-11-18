package de.jexp.disruptor_http.server.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;

public class NettyChannelTrackingHandler extends SimpleChannelHandler {

    private final ChannelGroup openChannels;

    public NettyChannelTrackingHandler(ChannelGroup openChannels) {
        this.openChannels = openChannels;
    }

    @Override
    public void channelOpen(
            ChannelHandlerContext ctx, ChannelStateEvent e) {
        openChannels.add(ctx.getChannel());
    }
    
}
