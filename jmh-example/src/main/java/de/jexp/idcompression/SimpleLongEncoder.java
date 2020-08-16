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
 only store minimum bytes of each long
 store a prefix with the number of bytes and sign-indicator (needs 3+1 bit)  (high-bits)
 -> use pre-defined pattern constants for 16 versions (8 bytes + 2x sign)
 use rest of prefix byte for rll counter (low bits)
 */
public class SimpleLongEncoder implements de.jexp.idcompression.LongEncoder
{

    @Override
    public int encode( ByteBuffer target, long value )
    {
        if (value == 0) {
            target.put((byte)0);
            return 1;
        }
        byte blocks = 0;
        boolean negative = false;
        if ( value < 0 ) {
            value = -value;
            negative=true;
        }
        final byte[] bytes = new byte[9];
        while (true) {
            if ( value == 0) break;
            byte b = (byte) (value & 0xFF);
            blocks++;
            bytes[blocks]=b;
            value >>= 8;
        }

        bytes[0] = negative ? (byte) -blocks : blocks;
        target.put(bytes,0,blocks+1);
        return blocks+1;
    }
    @Override
    public int size(long value) {
        if ( value < 0 ) value = -value;
        byte b;
        for (int i=1;i != 9;i++) {
            b = (byte)(value & 0xFF);
            if ( b == 0) return i + 1;
            value >>= 8;
        }
        return 9;
    }

    @Override
    public long decode( ByteBuffer source )
    {
        byte count = source.get();
        if (count == 0) return 0;
        int bytes = count > 0 ? count : -count;
        long result = 0;
        for (int i=0; i != bytes; i++) {
            byte b = source.get();
            result |= (b & 0xFFL) << (i << 3);
        }
        return count > 0 ? result : -result;
    }
}
