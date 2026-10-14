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

   public static boolean updateNetworkStats(NetworkIF netIF) {
      if (IS_VISTA_OR_GREATER) {
         MIB_IF_ROW2 miB_IF_ROW2 = new MIB_IF_ROW2();
         miB_IF_ROW2.InterfaceIndex = netIF.queryNetworkInterface().getIndex();
         if (0 != IPHlpAPI.INSTANCE.GetIfEntry2(miB_IF_ROW2)) {
            return false;
         }

         netIF.setIfType(miB_IF_ROW2.Type);
         netIF.d(miB_IF_ROW2.PhysicalMediumType);
         netIF.setConnectorPresent((miB_IF_ROW2.InterfaceAndOperStatusFlags & 32) > 0);
         netIF.setBytesSent(miB_IF_ROW2.OutOctets);
         netIF.setBytesRecv(miB_IF_ROW2.InOctets);
         netIF.setPacketsSent(miB_IF_ROW2.OutUcastPkts);
         netIF.setPacketsRecv(miB_IF_ROW2.InUcastPkts);
         netIF.setOutErrors(miB_IF_ROW2.OutErrors);
         netIF.setInErrors(miB_IF_ROW2.InErrors);
         netIF.setCollisions(miB_IF_ROW2.OutDiscards);
         netIF.setInDrops(miB_IF_ROW2.InDiscards);
         netIF.setSpeed(miB_IF_ROW2.ReceiveLinkSpeed);
      } else {
         MIB_IFROW miB_IFROW = new MIB_IFROW();
         miB_IFROW.dwIndex = netIF.queryNetworkInterface().getIndex();
         if (0 != IPHlpAPI.INSTANCE.GetIfEntry(miB_IFROW)) {
            return false;
         }

         netIF.setIfType(miB_IFROW.dwType);
         netIF.setBytesSent(ParseUtil.unsignedIntToLong(miB_IFROW.dwOutOctets));
         netIF.setBytesRecv(ParseUtil.unsignedIntToLong(miB_IFROW.dwInOctets));
         netIF.setPacketsSent(ParseUtil.unsignedIntToLong(miB_IFROW.dwOutUcastPkts));
         netIF.setPacketsRecv(ParseUtil.unsignedIntToLong(miB_IFROW.dwInUcastPkts));
         netIF.setOutErrors(ParseUtil.unsignedIntToLong(miB_IFROW.dwOutErrors));
         netIF.setInErrors(ParseUtil.unsignedIntToLong(miB_IFROW.dwInErrors));
         netIF.setCollisions(ParseUtil.unsignedIntToLong(miB_IFROW.dwOutDiscards));
         netIF.setInDrops(ParseUtil.unsignedIntToLong(miB_IFROW.dwInDiscards));
         netIF.setSpeed(ParseUtil.unsignedIntToLong(miB_IFROW.dwSpeed));
      }

      netIF.setTimeStamp(System.currentTimeMillis());
      return true;
   }
}
