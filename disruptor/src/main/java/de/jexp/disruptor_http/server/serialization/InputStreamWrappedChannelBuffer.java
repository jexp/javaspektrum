package de.jexp.disruptor_http.server.serialization;

import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.io.InputStream;


public class InputStreamWrappedChannelBuffer extends InputStream {

    ChannelBuffer buf;
    
    public InputStreamWrappedChannelBuffer(ChannelBuffer buf) {
        this.buf = buf;
    }
    
    public int read() throws IOException {
        try {
            return buf.readByte();
        } catch(IndexOutOfBoundsException e) {
            return -1;
        }
    }
}
