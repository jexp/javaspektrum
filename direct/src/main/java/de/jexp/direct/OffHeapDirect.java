package de.jexp.direct;

import sun.misc.Unsafe;

import static de.jexp.direct.Direct.getUnsafe;

/**
 * @author mh
 * @since 26.11.11
 */
public class OffHeapDirect {

    private static final int UPTIME_OFFSET = Direct.LONG_SIZE;

    public static void main(String[] args) {
        final Unsafe unsafe = getUnsafe();
        final long address = unsafe.allocateMemory(Direct.LONG_SIZE + Direct.INT_SIZE);
        unsafe.putLong(address, 0xABCDEF);
        unsafe.putInt(address+ UPTIME_OFFSET, 10000);
        assert 0xABCDEF == unsafe.getLong(address);
        assert 10000 == unsafe.getInt(address+UPTIME_OFFSET);
        System.out.println("ports = " + unsafe.getLong(address));
        System.out.println("uptime = " + unsafe.getInt(address + UPTIME_OFFSET));
        unsafe.freeMemory(address);
    }
}
