package de.jexp.idcompression;

import java.nio.ByteBuffer;

/**
 * @author mh
 * @since 17.03.13
 */
public interface LongEncoder
{
    int encode( ByteBuffer target, long value );

    int size( long value );

    long decode( ByteBuffer source );
}
