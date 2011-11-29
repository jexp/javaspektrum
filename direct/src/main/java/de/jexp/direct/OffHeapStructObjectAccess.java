package de.jexp.direct;

import sun.misc.Unsafe;

import static de.jexp.direct.Direct.getUnsafe;
import static de.jexp.direct.Direct.sizeOf32;

/**
 * @author mh
 * @since 26.11.11
 */
public class OffHeapStructObjectAccess {
    public static void main(String[] args) throws NoSuchFieldException {
        final Unsafe unsafe = getUnsafe();

        HostStruct struct = new HostStruct();
        struct.ports = 0xABCDEF;
        struct.uptime = 1000;

        long size = sizeOf32(struct);
        long address = unsafe.allocateMemory(size);
        unsafe.copyMemory(
                struct,      // Quell-Objekt
                0,           // Offset innerhalb des Quell-Objekts
                null,        // Ziel wird durch Zieladresse definiert, kein Ziel-Objekt
                address,     // Zieladresse
                size);
        long pointerOffset = unsafe.objectFieldOffset(Helper.class.getDeclaredField("struct"));
        Helper helper = new Helper();
        // pointer Instanzvariable auf off-heap-speicheradresse des objektes setzen
        unsafe.putLong(helper, pointerOffset, address);

        // zugriff auf struct-daten über pointer
        System.out.println(helper.struct.ports);
        assert helper.struct.ports == 0xABCDEF;
        assert helper.struct.uptime == 1000;
        unsafe.freeMemory(address);
    }
}

class HostStruct {
    long ports;
    int uptime;
}

// ein Hilfsobjekt mit nur einer Variablen um auf das gespeicherte Objekt zuzugreifen
class Helper {
    HostStruct struct;
}
