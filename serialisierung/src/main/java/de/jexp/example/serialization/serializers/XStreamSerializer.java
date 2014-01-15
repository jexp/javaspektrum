package de.jexp.example.serialization.serializers;

import com.thoughtworks.xstream.XStream;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author mh
 * @since 19.11.13
 */
public class XStreamSerializer implements Serializer {

    private ObjectOutputStream os;

    @Override
    public void open(OutputStream os) throws Exception {
        this.os = new XStream().createObjectOutputStream(os);
    }

    @Override
    public void serialize(Object object) throws Exception {
        os.writeObject(object);
    }

    @Override
    public void close() throws Exception {
        os.close();
    }
}
