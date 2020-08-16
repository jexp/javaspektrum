package de.jexp.direct;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static de.jexp.direct.Direct.getUnsafe;

public class HelloWorld {

    private static final int SIZE = 12;

    public static void main(String[] args) throws Exception {
        Unsafe unsafe = getUnsafe();
        //long address = unsafe.allocateMemory(unsafe.pageSize());
        long address = unsafe.allocateMemory(SIZE);
        try {
            unsafe.putLong(address, 0x6f77206f6c6c6568L);
            unsafe.putInt(address + 8, 0x21646c72);
            byte[] bytes = new byte[SIZE];
            final int offset = unsafe.arrayBaseOffset(byte[].class);
            unsafe.copyMemory(null, address, bytes, offset, SIZE);
            System.out.println(new String(bytes));
        } finally {
            unsafe.freeMemory(address);
        }
    }
}
