package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_IFROW;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_IF_ROW2;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WindowsNetworks extends Networks {
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static final byte CONNECTOR_PRESENT_BIT = 32;
   private static final long b = 18008462853337L;

   public static boolean updateNetworkStats(NetworkIF var0) {
      if (IS_VISTA_OR_GREATER) {
         MIB_IF_ROW2 var4 = new MIB_IF_ROW2();
         var4.InterfaceIndex = var0.queryNetworkInterface().getIndex();
         if (0 != IPHlpAPI.INSTANCE.GetIfEntry2(var4)) {
            return false;
         }

         var0.setIfType(var4.Type);
         var0.d(var4.PhysicalMediumType);
         var0.setConnectorPresent((var4.InterfaceAndOperStatusFlags & 32) > 0);
         var0.setBytesSent(var4.OutOctets);
         var0.setBytesRecv(var4.InOctets);
         var0.setPacketsSent(var4.OutUcastPkts);
         var0.setPacketsRecv(var4.InUcastPkts);
         var0.setOutErrors(var4.OutErrors);
         var0.setInErrors(var4.InErrors);
         var0.setCollisions(var4.OutDiscards);
         var0.setInDrops(var4.InDiscards);
         var0.setSpeed(var4.ReceiveLinkSpeed);
      } else {
         MIB_IFROW var5 = new MIB_IFROW();
         var5.dwIndex = var0.queryNetworkInterface().getIndex();
         if (0 != IPHlpAPI.INSTANCE.GetIfEntry(var5)) {
            return false;
         }

         var0.setIfType(var5.dwType);
         var0.setBytesSent(ParseUtil.unsignedIntToLong(var5.dwOutOctets));
         var0.setBytesRecv(ParseUtil.unsignedIntToLong(var5.dwInOctets));
         var0.setPacketsSent(ParseUtil.unsignedIntToLong(var5.dwOutUcastPkts));
         var0.setPacketsRecv(ParseUtil.unsignedIntToLong(var5.dwInUcastPkts));
         var0.setOutErrors(ParseUtil.unsignedIntToLong(var5.dwOutErrors));
         var0.setInErrors(ParseUtil.unsignedIntToLong(var5.dwInErrors));
         var0.setCollisions(ParseUtil.unsignedIntToLong(var5.dwOutDiscards));
         var0.setInDrops(ParseUtil.unsignedIntToLong(var5.dwInDiscards));
         var0.setSpeed(ParseUtil.unsignedIntToLong(var5.dwSpeed));
      }

      var0.setTimeStamp(System.currentTimeMillis());
      return true;
   }

   static {
      long var0 = b ^ 38941009911255L;
      long var10000 = var0 ^ 8372325303697592095L;
   }
}
