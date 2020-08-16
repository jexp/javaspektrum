package de.jexp.idcompression.bytes;

import java.nio.ByteBuffer;

/**
 * @author mh
 * @since 17.03.13
 */
public interface LongEncoderBytes
{
    int encode(byte[] target, int offset, long value);

    long decode(byte[] source, int offset);
}
