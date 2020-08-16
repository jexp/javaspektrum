package de.jexp.example.serialization;

import de.jexp.example.serialization.serializers.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/**
 * @author mh
 * @since 17.11.13
 */
@RunWith(Parameterized.class)
public class SerializerTest {
    private final static int RUNS = 1000;
    public static final int SIZE = 1000;
    private final CountingOutputStream buffer = new CountingOutputStream();
    private Serializer serializer;

    @Parameterized.Parameters
    public static Iterable<Object[]> parameters() {
        return Arrays.<Object[]>asList(
                new Object[]{new NullSerializer()},
                new Object[]{new ByteBufferMappingSerializer()},
                new Object[]{new JavaSerializer()},
                new Object[]{new KryoSerializer()},
//                new Object[]{new MessagePackSerializer()},
                new Object[]{new JacksonSmileSerializer()},
                new Object[]{new JacksonSerializer()},
                new Object[]{new GsonSerializer()},
                new Object[]{new XStreamSerializer()}
        );
    }

    @BeforeClass
    public static void header() throws Exception {
        System.out.printf("%-20s %-30s %-10s %-10s %12s %10s%n", "message", "name", "runs", "object-size", "result-size", "time (ms)");
        System.out.flush();
    }

    public SerializerTest(Serializer serializer) {
        this.serializer = serializer;
    }

    @Test
    public void testWriteJavaSerialization() throws Exception {
        serializeObjects("",SIZE, serializer, buffer);
    }

    @Test
    public void testWriteCompressed() throws Exception {
        serializeObjects("compressed",SIZE, serializer, new GZIPOutputStream(buffer,1024*1024));
    }

    private long serializeObjects(String message, int size, Serializer serializer, OutputStream stream) throws Exception {
        serializer.open(stream);
        long time = System.currentTimeMillis();
        for (int i=0;i< RUNS;i++) {
            Root objects = Root.create(size);
            serializer.serialize(objects);
        }
        serializer.close();
        time = System.currentTimeMillis() - time;
        String name = serializer.getClass().getSimpleName();
        System.out.printf("%-20s %-30s %10d %10d %12d %10d%n", message, name, RUNS, size, this.buffer.getCount(), time);
        System.out.flush();
        return time;
    }

}
