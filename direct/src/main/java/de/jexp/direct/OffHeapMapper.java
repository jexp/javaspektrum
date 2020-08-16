package de.jexp.direct;

import sun.misc.Unsafe;

import static de.jexp.direct.Direct.INT_SIZE;
import static de.jexp.direct.Direct.LONG_SIZE;
import static de.jexp.direct.Direct.getUnsafe;

/**
 * @author mh
 * @since 26.11.11
 */
public class OffHeapMapper {
    public static void main(String[] args) {
        final Unsafe unsafe = getUnsafe();
        int uptime = 1000;
        long ports = 0xABCDEF;
        final Host host = new Host(ports, uptime);
        final long address = unsafe.allocateMemory(LONG_SIZE + INT_SIZE);
        final HostMapper mapper = new HostMapper(unsafe);
        mapper.writeHost(host,address);
        Host newHost = mapper.readHost(address);
        assert host.getPorts() == newHost.getPorts();
        assert host.getUptime() == newHost.getUptime();
        System.out.println("ports = " + newHost.getPorts());
        System.out.println("uptime = " + newHost.getUptime());
        unsafe.freeMemory(address);
    }

    static class HostMapper {
        private static final int PORTS_OFFSET = 0;
        private static final int UPTIME_OFFSET = LONG_SIZE;
        private final Unsafe unsafe;

        public HostMapper(Unsafe unsafe) {
            this.unsafe = unsafe;
        }

        public void writeHost(Host host, long address) {
            unsafe.putLong(address + PORTS_OFFSET, host.getPorts());
            unsafe.putInt(address + UPTIME_OFFSET, host.getUptime());
        }

        public Host readHost(long address) {
            return new Host(unsafe.getLong(address + PORTS_OFFSET), unsafe.getInt(address + UPTIME_OFFSET));
        }
    }

    static class Host {
        private final long ports;
        private final int uptime;

        public Host(long ports, int uptime) {
            this.ports = ports;
            this.uptime = uptime;
        }

        public long getPorts() {
            return ports;
        }

        public int getUptime() {
            return uptime;
        }
    }
}
