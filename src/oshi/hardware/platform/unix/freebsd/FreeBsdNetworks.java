package oshi.hardware.platform.unix.freebsd;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class FreeBsdNetworks extends Networks {
   public static boolean updateNetworkStats(NetworkIF netIF) {
      String string = ExecutingCommand.getAnswerAt("netstat -bI " + netIF.getName(), 1);
      netIF.setTimeStamp(System.currentTimeMillis());
      String[] strings = ParseUtil.whitespaces.split(string);
      if (strings.length < 12) {
         return false;
      }

      netIF.setBytesSent(ParseUtil.parseUnsignedLongOrDefault(strings[10], 0L));
      netIF.setBytesRecv(ParseUtil.parseUnsignedLongOrDefault(strings[7], 0L));
      netIF.setPacketsSent(ParseUtil.parseUnsignedLongOrDefault(strings[8], 0L));
      netIF.setPacketsRecv(ParseUtil.parseUnsignedLongOrDefault(strings[4], 0L));
      netIF.setOutErrors(ParseUtil.parseUnsignedLongOrDefault(strings[9], 0L));
      netIF.setInErrors(ParseUtil.parseUnsignedLongOrDefault(strings[5], 0L));
      netIF.setCollisions(ParseUtil.parseUnsignedLongOrDefault(strings[11], 0L));
      netIF.setInDrops(ParseUtil.parseUnsignedLongOrDefault(strings[6], 0L));
      return true;
   }
}
