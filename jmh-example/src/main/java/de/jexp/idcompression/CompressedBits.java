package de.jexp.idcompression;

import de.jexp.idcompression.bytes.UnsignedLongBase128EncoderBytes;

import java.nio.ByteBuffer;

/**
 * @author mh
 * @since 15.12.13
 */
public class CompressedBits {
    public static final int GB = 1024 * 1024 * 1024;
    public static final int MB = 1024 * 1024;
    byte[] tx = new byte[MB]; // buffer
    byte[] bytes = new byte[GB]; // buffer
    int txPos = 0;
    long written = 0;
    private UnsignedLongBase128EncoderBytes encoder = new UnsignedLongBase128EncoderBytes();


    public CompressedBits() {
    }

    public void set(long position) {
        if (MB - txPos < UnsignedLongBase128Encoder.MAX_SIZE) {
            flushAndCompress();
        }
        int res = encoder.encode(tx, txPos, position);
        txPos += res;
    }

    private void flushAndCompress() {
        written += txPos;
        txPos = 0;
    }

    public long getWritten() {
        return written;
    }
}
