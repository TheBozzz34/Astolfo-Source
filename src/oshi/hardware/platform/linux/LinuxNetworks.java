package oshi.hardware.platform.linux;

import java.io.File;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.FileUtil;

@ThreadSafe
public final class LinuxNetworks extends Networks {
   private static final long b = 92511420692807L;

   public static boolean updateNetworkStats(NetworkIF var0) {
      try {
         File var4 = new File(String.format("/sys/class/net/%s/statistics", var0.getName()));
         if (!var4.isDirectory()) {
            return false;
         }
      } catch (SecurityException var17) {
         return false;
      }

      String var18 = String.format("/sys/class/net/%s/type", var0.getName());
      String var5 = String.format("/sys/class/net/%s/carrier", var0.getName());
      String var6 = String.format("/sys/class/net/%s/statistics/tx_bytes", var0.getName());
      String var7 = String.format("/sys/class/net/%s/statistics/rx_bytes", var0.getName());
      String var8 = String.format("/sys/class/net/%s/statistics/tx_packets", var0.getName());
      String var9 = String.format("/sys/class/net/%s/statistics/rx_packets", var0.getName());
      String var10 = String.format("/sys/class/net/%s/statistics/tx_errors", var0.getName());
      String var11 = String.format("/sys/class/net/%s/statistics/rx_errors", var0.getName());
      String var12 = String.format("/sys/class/net/%s/statistics/collisions", var0.getName());
      String var13 = String.format("/sys/class/net/%s/statistics/rx_dropped", var0.getName());
      String var14 = String.format("/sys/class/net/%s/speed", var0.getName());
      var0.setTimeStamp(System.currentTimeMillis());
      var0.setIfType(FileUtil.getIntFromFile(var18));
      var0.setConnectorPresent(FileUtil.getIntFromFile(var5) > 0);
      var0.setBytesSent(FileUtil.getUnsignedLongFromFile(var6));
      var0.setBytesRecv(FileUtil.getUnsignedLongFromFile(var7));
      var0.setPacketsSent(FileUtil.getUnsignedLongFromFile(var8));
      var0.setPacketsRecv(FileUtil.getUnsignedLongFromFile(var9));
      var0.setOutErrors(FileUtil.getUnsignedLongFromFile(var10));
      var0.setInErrors(FileUtil.getUnsignedLongFromFile(var11));
      var0.setCollisions(FileUtil.getUnsignedLongFromFile(var12));
      var0.setInDrops(FileUtil.getUnsignedLongFromFile(var13));
      long var15 = FileUtil.getUnsignedLongFromFile(var14) * 1024L * 1024L;
      var0.setSpeed(var15 < 0L ? 0L : var15);
      return true;
   }
}
