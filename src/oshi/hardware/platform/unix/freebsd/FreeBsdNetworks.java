package oshi.hardware.platform.unix.freebsd;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class FreeBsdNetworks extends Networks {
   private static final long b = 76108154854552L;

   public static boolean updateNetworkStats(NetworkIF var0) {
      String var4 = ExecutingCommand.getAnswerAt("netstat -bI " + var0.getName(), 1);
      var0.setTimeStamp(System.currentTimeMillis());
      String[] var5 = ParseUtil.Q.split(var4);
      if (var5.length < 12) {
         return false;
      }

      var0.setBytesSent(ParseUtil.parseUnsignedLongOrDefault(var5[10], 0L));
      var0.setBytesRecv(ParseUtil.parseUnsignedLongOrDefault(var5[7], 0L));
      var0.setPacketsSent(ParseUtil.parseUnsignedLongOrDefault(var5[8], 0L));
      var0.setPacketsRecv(ParseUtil.parseUnsignedLongOrDefault(var5[4], 0L));
      var0.setOutErrors(ParseUtil.parseUnsignedLongOrDefault(var5[9], 0L));
      var0.setInErrors(ParseUtil.parseUnsignedLongOrDefault(var5[5], 0L));
      var0.setCollisions(ParseUtil.parseUnsignedLongOrDefault(var5[11], 0L));
      var0.setInDrops(ParseUtil.parseUnsignedLongOrDefault(var5[6], 0L));
      return true;
   }
}
