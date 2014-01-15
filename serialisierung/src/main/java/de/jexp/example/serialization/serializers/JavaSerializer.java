package de.jexp.example.serialization.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastOutput;
import com.esotericsoftware.kryo.io.Output;
import de.jexp.example.serialization.serializers.Serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author mh
 * @since 19.11.13
 */
public class JavaSerializer implements Serializer {


    private ObjectOutputStream os;

    @Override
    public void open(OutputStream os) throws IOException {
        this.os = new ObjectOutputStream(os); // todo BufferedOS
    }

    @Override
    public void serialize(Object object) throws IOException {
        os.writeObject(object);
    }

    @Override
    public void close() throws IOException {
        os.close();
    }
}
