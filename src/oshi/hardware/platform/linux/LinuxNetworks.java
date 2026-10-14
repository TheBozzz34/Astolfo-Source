package oshi.hardware.platform.linux;

import java.io.File;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.FileUtil;

@ThreadSafe
public final class LinuxNetworks extends Networks {
   public static boolean updateNetworkStats(NetworkIF netIF) {
      try {
         File file = new File(String.format("/sys/class/net/%s/statistics", netIF.getName()));
         if (!file.isDirectory()) {
            return false;
         }
      } catch (SecurityException securityException) {
         return false;
      }

      String string = String.format("/sys/class/net/%s/type", netIF.getName());
      String string2 = String.format("/sys/class/net/%s/carrier", netIF.getName());
      String string3 = String.format("/sys/class/net/%s/statistics/tx_bytes", netIF.getName());
      String string4 = String.format("/sys/class/net/%s/statistics/rx_bytes", netIF.getName());
      String string5 = String.format("/sys/class/net/%s/statistics/tx_packets", netIF.getName());
      String string6 = String.format("/sys/class/net/%s/statistics/rx_packets", netIF.getName());
      String string7 = String.format("/sys/class/net/%s/statistics/tx_errors", netIF.getName());
      String string8 = String.format("/sys/class/net/%s/statistics/rx_errors", netIF.getName());
      String string9 = String.format("/sys/class/net/%s/statistics/collisions", netIF.getName());
      String string10 = String.format("/sys/class/net/%s/statistics/rx_dropped", netIF.getName());
      String string11 = String.format("/sys/class/net/%s/speed", netIF.getName());
      netIF.setTimeStamp(System.currentTimeMillis());
      netIF.setIfType(FileUtil.getIntFromFile(string));
      netIF.setConnectorPresent(FileUtil.getIntFromFile(string2) > 0);
      netIF.setBytesSent(FileUtil.getUnsignedLongFromFile(string3));
      netIF.setBytesRecv(FileUtil.getUnsignedLongFromFile(string4));
      netIF.setPacketsSent(FileUtil.getUnsignedLongFromFile(string5));
      netIF.setPacketsRecv(FileUtil.getUnsignedLongFromFile(string6));
      netIF.setOutErrors(FileUtil.getUnsignedLongFromFile(string7));
      netIF.setInErrors(FileUtil.getUnsignedLongFromFile(string8));
      netIF.setCollisions(FileUtil.getUnsignedLongFromFile(string9));
      netIF.setInDrops(FileUtil.getUnsignedLongFromFile(string10));
      long l = FileUtil.getUnsignedLongFromFile(string11) * 1024L * 1024L;
      netIF.setSpeed(l < 0L ? 0L : l);
      return true;
   }
}
