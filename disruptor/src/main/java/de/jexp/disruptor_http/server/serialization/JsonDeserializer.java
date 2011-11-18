package de.jexp.disruptor_http.server.serialization;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

public class JsonDeserializer {

    private JsonParser parser;

    public JsonDeserializer(JsonFactory factory, ChannelBuffer stream) {
        try {
            final InputStreamWrappedChannelBuffer inputStream = new InputStreamWrappedChannelBuffer(stream);
            this.parser = factory.createJsonParser(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate JSON parser.", e);
        }
    }

    public Object readObject() {
        try {
            return parser.readValueAs(Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing value", e);
        }
    }
}
