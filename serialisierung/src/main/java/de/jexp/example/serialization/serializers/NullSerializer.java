package de.jexp.example.serialization.serializers;

import de.jexp.example.serialization.serializers.Serializer;

import java.io.OutputStream;

/**
 * @author mh
 * @since 19.11.13
 */
public class NullSerializer implements Serializer {
    @Override
    public void open(OutputStream os) throws Exception {
    }

    @Override
    public void serialize(Object object) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }
}
