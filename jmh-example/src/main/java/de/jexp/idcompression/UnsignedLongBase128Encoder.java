/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jexp.idcompression;

import java.nio.ByteBuffer;

/**
 * Implements Base-128 Encoding for unsigned values, similar to: http://en.wikipedia.org/wiki/Variable-length_quantity
 * Encodes long values into a minimal sequence of blocks, at most 10 blocks.
 * Writes blocks of 7 bits of the original value starting from the LSB
 * Uses the most-significant-bit as identifier if there is another block following (0 if last block, 1 otherwise)
 * Uses the buffers, current position to read and write.
 */
public class UnsignedLongBase128Encoder implements LongEncoder
{
    public static final int MAX_SIZE = 9;

    /**
     * @param target
     * @param value
     * @return number of bytes used for the encoded value
     */
    @Override
    public int encode( ByteBuffer target, long value )
    {
        assert value >= 0 : "Invalid value " + value;

        int startPosition = target.position();
        while ( true )
        {
            if ( value <= 0x7F )
            {
                target.put( (byte) value );
                break;
            } else
            {
                byte thisByte = (byte) (0x80 | (byte) (value & 0x7F));
                target.put( thisByte );
                value >>>= 7;
            }
        }
        return target.position() - startPosition;
    }

    @Override
    public int size(long value) {
        assert value >= 0 : "Invalid value " + value;

        int size = 0;
        while ( true )
        {
            if ( value <= 0x7F )
            {
                size ++;
                break;
            } else
            {
                size ++;
                value >>>= 7;
            }
        }
        return size;
    }

    @Override
    public long decode( ByteBuffer source )
    {
        long result = 0;
        int shiftCount = 0;
        while ( true )
        {
            long thisByte = source.get();
            if ( (thisByte & 0x80) == 0 )
            {
                result |= (thisByte << shiftCount);
                return result;
            } else
            {
                result |= ((thisByte & 0x7F) << shiftCount);
                shiftCount += 7;
            }
        }
    }
}
