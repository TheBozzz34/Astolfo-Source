package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisNetworks extends Networks {
   public static boolean updateNetworkStats(NetworkIF netIF) {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup("link", -1, netIF.getName());
         if (kstat == null) {
            kstat = kstatChain.lookup(null, -1, netIF.getName());
         }

         if (kstat != null && kstatChain.read(kstat)) {
            netIF.setBytesSent(KstatUtil.dataLookupLong(kstat, "obytes64"));
            netIF.setBytesRecv(KstatUtil.dataLookupLong(kstat, "rbytes64"));
            netIF.setPacketsSent(KstatUtil.dataLookupLong(kstat, "opackets64"));
            netIF.setPacketsRecv(KstatUtil.dataLookupLong(kstat, "ipackets64"));
            netIF.setOutErrors(KstatUtil.dataLookupLong(kstat, "oerrors"));
            netIF.setInErrors(KstatUtil.dataLookupLong(kstat, "ierrors"));
            netIF.setCollisions(KstatUtil.dataLookupLong(kstat, "collisions"));
            netIF.setInDrops(KstatUtil.dataLookupLong(kstat, "dl_idrops"));
            netIF.setSpeed(KstatUtil.dataLookupLong(kstat, "ifspeed"));
            netIF.setTimeStamp(kstat.ks_snaptime / 1000000L);
            return true;
         }
      }

      return false;
   }
}
