package de.jexp.direct;

import java.nio.ByteBuffer;

/**
 * @author mh
 * @since 26.11.11
 */
public class OffHeapDirectByteBuffer {
    public static void main(String[] args) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(Direct.LONG_SIZE + Direct.INT_SIZE);
        buffer.putLong(0xABCDEF);
        buffer.put((byte)42);
        buffer.rewind();
        final long value = buffer.getLong();
        assert value == 0xABCDEF;
        System.out.println("value = " + value);
        final byte answer = buffer.get();
        assert answer == 42;
        System.out.println("answer = " + answer);

        /*
        buffer.duplicate();
        buffer.slice();
        buffer.clear();
        buffer.rewind();
        buffer.asIntBuffer();
        */
    }
}
