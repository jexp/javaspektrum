package de.jexp.direct;

import sun.misc.Unsafe;

import static de.jexp.direct.Direct.INT_SIZE;
import static de.jexp.direct.Direct.LONG_SIZE;
import static de.jexp.direct.Direct.getUnsafe;

/**
 * @author mh
 * @since 26.11.11
 */
public class OffHeapFlyweight {

    public static void main(String[] args) {
        final Unsafe unsafe = getUnsafe();
        final long addresss = unsafe.allocateMemory(Host.size());
        final Host host = new Host(unsafe, addresss);
        host.setUptime(1000);
        assert 1000 == host.getUptime();
        host.setPorts(0xABCDEF);
        assert 0xABCDEF == host.getPorts();
        System.out.println("host.getUptime() = " + host.getUptime());
        System.out.println("host.getPorts() = " + host.getPorts());
        unsafe.freeMemory(addresss);
    }
}

class Host {

    private static final int PORTS_OFFSET = 0;
    private static final int UPTIME_OFFSET = LONG_SIZE;

    public static long size() {
       return LONG_SIZE + INT_SIZE;
   }
   private final Unsafe unsafe;
   private long address;
   private long ip;
   public Host(Unsafe unsafe, long address) {
      this.unsafe=unsafe;
       this.address = address;
   }
   public Host withAddress(long ip) {
      this.address = address + ip;
      return this;
   }
   public long getIp() {
      return ip;
   }
   public long getPorts() {
   	  return unsafe.getLong(address + PORTS_OFFSET);
   }
   public void setPorts(long ports) {
  	  unsafe.putLong(address + PORTS_OFFSET, ports);
   }
   public int getUptime() {
      return unsafe.getInt(address+ UPTIME_OFFSET);
   }
   public void setUptime(int uptime) {
      unsafe.putInt(address+ UPTIME_OFFSET,uptime);
   }
}
