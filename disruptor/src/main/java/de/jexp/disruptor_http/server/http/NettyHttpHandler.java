package de.jexp.disruptor_http.server.http;

import com.lmax.disruptor.RingBuffer;
import de.jexp.disruptor_http.server.HttpVerb;
import de.jexp.disruptor_http.server.core.RequestEvent;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpHandler extends SimpleChannelHandler {

    private RingBuffer<RequestEvent> workBuffer;

    public NettyHttpHandler(RingBuffer<RequestEvent> workBuffer) {
        this.workBuffer = workBuffer;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpRequest httpRequest = (HttpRequest) e.getMessage();

        long sequenceNo = workBuffer.next();
        RequestEvent event = workBuffer.get(sequenceNo);
        // copy necessary data into event object in ringbuffer

        event.setVerb(HttpVerb.from(httpRequest));
        event.setPath(httpRequest.getUri());
        event.setIsPersistentConnection(isKeepAlive(httpRequest));
        event.setContent(httpRequest.getContent());
        event.setOutputChannel(e.getChannel());

        workBuffer.publish(sequenceNo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        if (cause instanceof TooLongFrameException) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        cause.printStackTrace();
        if (ch.isConnected()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

}
