package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisNetworks extends Networks {
   private static final long b = 109744082368762L;

   public static boolean updateNetworkStats(NetworkIF var0) {
      try (KstatUtil.KstatChain var4 = KstatUtil.openChain()) {
         Kstat var6 = var4.lookup("link", -1, var0.getName());
         if (var6 == null) {
            var6 = var4.lookup(null, -1, var0.getName());
         }

         if (var6 != null && var4.read(var6)) {
            var0.setBytesSent(KstatUtil.dataLookupLong(var6, "obytes64"));
            var0.setBytesRecv(KstatUtil.dataLookupLong(var6, "rbytes64"));
            var0.setPacketsSent(KstatUtil.dataLookupLong(var6, "opackets64"));
            var0.setPacketsRecv(KstatUtil.dataLookupLong(var6, "ipackets64"));
            var0.setOutErrors(KstatUtil.dataLookupLong(var6, "oerrors"));
            var0.setInErrors(KstatUtil.dataLookupLong(var6, "ierrors"));
            var0.setCollisions(KstatUtil.dataLookupLong(var6, "collisions"));
            var0.setInDrops(KstatUtil.dataLookupLong(var6, "dl_idrops"));
            var0.setSpeed(KstatUtil.dataLookupLong(var6, "ifspeed"));
            var0.setTimeStamp(var6.ks_snaptime / 1000000L);
            return true;
         }
      }

      return false;
   }
}
