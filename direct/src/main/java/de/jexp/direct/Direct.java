package de.jexp.direct;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author mh
 * @since 23.11.11
 */
public class Direct {

    static final int LONG_SIZE = Long.SIZE / 8;
    static final int INT_SIZE = Integer.SIZE / 8;

    public static Unsafe getUnsafe() {
//        return Unsafe.getUnsafe();
    try {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        final Unsafe unsafe = (Unsafe) field.get(null);
        logInfo(unsafe);
        return unsafe;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    private static void logInfo(Unsafe unsafe) {
        System.out.println("  addressSize() = " + unsafe.addressSize());
        System.out.println("  pageSize() = " + unsafe.pageSize());
    }

    public static long sizeOf32(Object object) {
        Unsafe unsafe = getUnsafe();
        final long addressSize = 4L;
        final long classAddress = normalize(unsafe.getInt(object, addressSize));
        System.out.println("classAddress = " + classAddress);
        final int layoutHelperPosition = 3;
        final long layoutHelperOffset = classAddress + layoutHelperPosition * addressSize;
        System.out.println("layoutHelperOffset = " + layoutHelperOffset);
        return unsafe.getAddress(layoutHelperOffset);
    }
    public static long sizeOf64(Object object) {
        Unsafe unsafe = getUnsafe();
        final long addressSize = unsafe.addressSize();
        final long classAddress = unsafe.getLong(object, addressSize);
        System.out.println("classAddress = " + classAddress);
        final int layoutHelperPosition = 3;
        final long layoutHelperOffset = classAddress + layoutHelperPosition * addressSize;
        System.out.println("layoutHelperOffset = " + layoutHelperOffset);
        return unsafe.getAddress(layoutHelperOffset);
    }

    public static long normalize(int value) {
        if (value >= 0) return value;
        return (~0L >>> 32) & value;
    }

    public static void main(String... args) {
        writeReadValue(0x0A);
        System.out.println(sizeOf64("abc"));
    }

    private static void writeReadValue(final int value) {
        Unsafe unsafe = getUnsafe();
        byte size = 1;
        long address = unsafe.allocateMemory(size);
        try {
            unsafe.putAddress(address, value);
            long readValue = unsafe.getAddress(address);
            System.out.println("read value : " + readValue);
        } finally {
            unsafe.freeMemory(address);
        }
    }

}
