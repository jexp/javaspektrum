package de.jexp.direct;

import org.directmemory.cache.Cache;
import org.directmemory.measures.Monitor;
import org.directmemory.measures.Ram;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author mh
 * @since 29.11.11
 */
public class DirectMemoryLibary {
    public static class Host {
        private int uptime;
        private long ports;
        private int ip;

        // needed for deserializations
        public Host() {
        }

        Host(final InetAddress inetAddress, int uptime, long ports) throws UnknownHostException {
            this.ip = inetAddress.hashCode();
            this.uptime = uptime;
            this.ports = ports;
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        Cache.init(1, Ram.Mb(100));
        Cache.dump();
        final String hostname = "www.google.com";
        Host host = new Host(InetAddress.getByName(hostname), 1000, 0xABCDEF);
        Cache.put(hostname, host);
        Monitor.dump();
        Host host2 = (Host) Cache.retrieve(hostname);
        assert host.ip == host2.ip;
        assert host.ports == host2.ports;
        assert host.uptime == host2.uptime;
    }
}
