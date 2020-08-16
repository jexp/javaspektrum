package de.jexp.example.serialization;

import de.jexp.example.serialization.Root;
import de.jexp.example.serialization.serializers.Serializer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author mh
 * @since 21.11.13
 */
public class ByteBufferMappingSerializer implements Serializer {

    public static final int LONG_SIZE = 8;
    private BufferedOutputStream bos;
    private ByteBuffer buffer;
    private byte[] bytes = new byte[1024*1024];

    @Override
    public void open(OutputStream os) throws Exception {
        bos = new BufferedOutputStream(os);
        buffer = ByteBuffer.wrap(bytes);
    }

    @Override
    public void serialize(Object object) throws Exception {
        if (object instanceof Root) {
            serializeRoot((Root) object);
        } else {
            throw new IllegalArgumentException("Cannot serialized objects of type "+(object==null ? null : object.getClass()));
        }
    }

    private void serializeRoot(Root root) throws IOException {
        flush(root._string.length() + 2, Character.SIZE);
        buffer.putInt(root._string.length());
        buffer.put(root._string.getBytes("UTF-8"));

        flush(4+4+8,Byte.SIZE);
        buffer.putInt(root._int);
        buffer.putInt(root._integer); // todo null
        buffer.putLong(root._long); // todo null
        serializeInts(root);
        serializeLongs(root);
        serializeChildren(root);
    }

    private void flush(int toBeWritten, int bitsPerEntry) throws IOException {
        int dataSize = toBeWritten * (bitsPerEntry >> 3);
        if (buffer.remaining() < dataSize) {
            bos.write(bytes,0,buffer.position());
            buffer.rewind();
        }
    }

    private void serializeChildren(Root root) throws IOException {
        int childSize = root.children.length;
        flush(1, Integer.SIZE);
        buffer.putInt(childSize);
        for (int i = 0; i < childSize; i++) {
            serializeChild(root.children[i]);
        }
    }

    private void serializeChild(Child child) throws IOException {
        flush(1, Double.SIZE);
        buffer.putDouble(child._double);
    }

    private void serializeInts(Root root) throws IOException {
        int intSize = root._int_array.length;
        flush(intSize + 1, 4);
        buffer.putInt(intSize);
        for (int i = 0; i < intSize; i++) {
            buffer.putInt(root._int_array[i]);
        }
    }

    private int serializeLongs(Root root) throws IOException {
        int longSize = root._long_array.length;
        flush(longSize + 1, Long.SIZE);
        buffer.putInt(longSize);
        for (int i = 0; i < longSize; i++) {
            buffer.putLong(root._long_array[i]);
        }
        return longSize;
    }

    @Override
    public void close() throws Exception {
        bos.write(bytes,0,buffer.position());
        bos.close();
    }
}
