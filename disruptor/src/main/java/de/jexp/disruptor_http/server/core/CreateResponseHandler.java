package de.jexp.disruptor_http.server.core;

import com.lmax.disruptor.WorkHandler;
import de.jexp.disruptor_http.server.InvocationResponse;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.io.UnsupportedEncodingException;

import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class CreateResponseHandler implements WorkHandler<ResponseEvent> {

    public void onEvent(final ResponseEvent event) throws Exception {

        HttpResponse response = buildHttpResponse(event.getInvocationResponse());

        event.getOutputChannel().write(response);
    }

    private HttpResponse buildHttpResponse(InvocationResponse invocationResponse) throws UnsupportedEncodingException {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, invocationResponse.getStatus());

        addLocation(invocationResponse, response);

        addPayload(response, invocationResponse.getData());

        return response;
    }

    private void addLocation(InvocationResponse invocationResponse, HttpResponse response) {
        if (invocationResponse.getStatus() == HttpResponseStatus.CREATED) {
            response.addHeader(HttpHeaders.Names.LOCATION, invocationResponse.getLocation());
        }
    }

    private void addPayload(HttpResponse response, String value) throws UnsupportedEncodingException {
        if (value == null) {
            response.addHeader(HttpHeaders.Names.CONTENT_LENGTH, 0);
        } else {
            final byte[] bytes = value.getBytes("UTF-8");
            response.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length);
            response.setContent(new BigEndianHeapChannelBuffer(bytes));
        }
    }
}
