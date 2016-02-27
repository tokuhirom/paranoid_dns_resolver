package me.geso.paranoid_dns_resolver;

import org.apache.http.impl.conn.SystemDefaultDnsResolver;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;


/**
 * Reject DNS entries, that specified multicast, loopback address, etc.
 * <p>
 * This class protects you from connecting to internal IP ranges, hostnames/IPs that you
 * blacklist, and all combinations of redirects and DNS tricks to otherwise tarpit and/or connect to internal
 * resources.
 * </p>
 * <p>
 * See http://cpansearch.perl.org/src/SAXJAZMAN/LWPx-ParanoidAgent-1.10/lib/LWPx/ParanoidAgent.pm
 * </p>
 */
public class ParanoidDnsResolver extends SystemDefaultDnsResolver {
    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        InetAddress[] addresses = super.resolve(host);
        for (InetAddress address : addresses) {
            if (!isExternalAddress(address)) {
                throw new ParanoidAgentBlockedException(host);
            }
        }
        return addresses;
    }

    private static boolean isExternalAddress(final InetAddress addr) {
        Objects.requireNonNull(addr);

        // strict address validation for IPV4
        if (addr instanceof Inet4Address) {
            byte[] bytes = addr.getAddress();
            long haddr = ((bytes[0] & 0xff) << 24) + ((bytes[1] & 0xff) << 16) + ((bytes[2] & 0xff) << 8) +
                    (bytes[3] & 0xff);

            if (
                    (haddr & 0xFF000000) == 0x00000000 || // 0.0.0.0/8
                            (haddr & 0xFF000000) == 0x0A000000 || // 10.0.0.0/8
                            (haddr & 0xFF000000) == 0x7F000000 || // 127.0.0.0/8
                            (haddr & 0xFFF00000) == 0xAC100000 || // 172.16.0.0/12
                            (haddr & 0xFFFF0000) == 0xA9FE0000 || // 169.254.0.0/16
                            (haddr & 0xFFFF0000) == 0xC0A80000 || // 192.168.0.0/16
                            (haddr & 0xFFFFFF00) == 0xC0000200 || // 192.0.2.0/24 // "TEST-NET" docs / example code
                            (haddr & 0xFFFFFF00) == 0xC0586300 || // 192.88.99.0/24 6 to4 relay anycast addresses
                            haddr == 0xFFFFFFFF || // 255.255.255.255
                            (haddr & 0xF0000000) == 0xE0000000 // multicast addresses
                    ) {
                return false;
            }
        }

        // Use Java core methods for loopback/multicast address detection.
        return !(addr.isMulticastAddress()
                || addr.isAnyLocalAddress()
                || addr.isLoopbackAddress()
                || addr.isLinkLocalAddress()
                || addr.isSiteLocalAddress()
                || addr.isMCGlobal()
                || addr.isMCNodeLocal()
                || addr.isMCLinkLocal()
                || addr.isMCSiteLocal()
                || addr.isMCOrgLocal());
    }

}
