package de.jexp.example.serialization.serializers;

import java.io.OutputStream;

public interface Serializer {

    void open(OutputStream os) throws Exception;
    void serialize(Object object) throws Exception;
    void close() throws Exception;
}
