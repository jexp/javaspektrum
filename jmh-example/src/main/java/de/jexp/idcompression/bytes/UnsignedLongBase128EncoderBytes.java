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
package de.jexp.idcompression.bytes;

import de.jexp.idcompression.LongEncoder;

import java.nio.ByteBuffer;

/**
 * Implements Base-128 Encoding for unsigned values, similar to: http://en.wikipedia.org/wiki/Variable-length_quantity
 * Encodes long values into a minimal sequence of blocks, at most 10 blocks.
 * Writes blocks of 7 bits of the original value starting from the LSB
 * Uses the most-significant-bit as identifier if there is another block following (0 if last block, 1 otherwise)
 * Uses the buffers, current position to read and write.
 */
public class UnsignedLongBase128EncoderBytes implements LongEncoderBytes
{
    /**
     * @param target
     * @param position
     * @param value
     * @return number of bytes used for the encoded value
     */
    @Override
    public int encode( byte[] target, int position, long value )
    {
        assert value >= 0 : "Invalid value " + value;

        int startPosition = position;
        while ( true )
        {
            if ( value <= 0x7F )
            {
                target[position++]= (byte) value;
                break;
            } else
            {
                byte thisByte = (byte) (0x80 | (byte) (value & 0x7F));
                target[position++]= thisByte;
                value >>>= 7;
            }
        }
        return position - startPosition;
    }

    @Override
    public long decode( byte[] source, int position )
    {
        long result = 0;
        int shiftCount = 0;
        while ( true )
        {
            long thisByte = source[position++];
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
