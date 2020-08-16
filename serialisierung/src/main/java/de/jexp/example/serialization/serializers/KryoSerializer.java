package de.jexp.example.serialization.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastOutput;
import com.esotericsoftware.kryo.io.Output;
import de.jexp.example.serialization.serializers.Serializer;

import java.io.OutputStream;

/**
 * @author mh
 * @since 19.11.13
 */
public class KryoSerializer implements Serializer {

    private final Kryo kryo = new Kryo();
    private Output output;

    @Override
    public void open(OutputStream os) {
        output = new FastOutput(os);
    }

    @Override
    public void serialize(Object object) {
        kryo.writeObject(output, object);
    }

    @Override
    public void close() {
        output.close();
    }
}
