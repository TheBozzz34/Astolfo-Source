package oshi.hardware.platform.mac;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.IFmsgHdr;
import com.sun.jna.platform.mac.SystemB.IFmsgHdr2;
import com.sun.jna.ptr.IntByReference;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.Networks;

@ThreadSafe
public final class MacNetworks extends Networks {
   private static final int CTL_NET = 4;
   private static final int PF_ROUTE = 17;
   private static final int NET_RT_IFLIST2 = 6;
   private static final int RTM_IFINFO2 = 18;

   @Override
   public NetworkIF[] getNetworks() {
      ArrayList arrayList = new ArrayList();
      Map map = queryIFdata(-1);

      for (NetworkInterface networkInterface : this.getNetworkInterfaces()) {
         NetworkIF networkIF = new NetworkIF();
         networkIF.setNetworkInterface(networkInterface);
         updateNetworkStats(networkIF, map);
         arrayList.add(networkIF);
      }

      return (oshi.hardware.NetworkIF[])(arrayList.toArray(new NetworkIF[0]));
   }

   private static Map<Integer, MacNetworks.IFdata> queryIFdata(int index) {
      HashMap hashMap = new HashMap();
      int[] ints = new int[]{4, 17, 0, 0, 6, 0};
      IntByReference intByReference = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctl(ints, 6, null, intByReference, null, 0)) {
         return hashMap;
      }

      Memory memory = new Memory(intByReference.getValue());
      if (0 != SystemB.INSTANCE.sysctl(ints, 6, memory, intByReference, null, 0)) {
         return hashMap;
      }

      int i = (int)(memory.size() - new IFmsgHdr().size());
      short s = 0;

      while (s < i) {
         Pointer pointer = memory.share(s);
         IFmsgHdr iFmsgHdr = new IFmsgHdr(pointer);
         iFmsgHdr.read();
         s += iFmsgHdr.ifm_msglen;
         if (iFmsgHdr.ifm_type == 18) {
            IFmsgHdr2 iFmsgHdr2 = new IFmsgHdr2(pointer);
            iFmsgHdr2.read();
            if (index < 0 || index == iFmsgHdr2.ifm_index) {
               hashMap.put(
                  Integer.valueOf(iFmsgHdr2.ifm_index),
                  new MacNetworks.IFdata(
                     iFmsgHdr2.ifm_data.ifi_type,
                     iFmsgHdr2.ifm_data.ifi_opackets,
                     iFmsgHdr2.ifm_data.ifi_ipackets,
                     iFmsgHdr2.ifm_data.ifi_obytes,
                     iFmsgHdr2.ifm_data.ifi_ibytes,
                     iFmsgHdr2.ifm_data.ifi_oerrors,
                     iFmsgHdr2.ifm_data.ifi_ierrors,
                     iFmsgHdr2.ifm_data.ifi_collisions,
                     iFmsgHdr2.ifm_data.ifi_iqdrops,
                     iFmsgHdr2.ifm_data.ifi_baudrate,
                     System.currentTimeMillis()
                  )
               );
            }
         }
      }

      return hashMap;
   }

   public static boolean updateNetworkStats(NetworkIF netIF) {
      int index = netIF.queryNetworkInterface().getIndex();
      return updateNetworkStats(netIF, queryIFdata(index));
   }

   private static boolean updateNetworkStats(NetworkIF netIF, Map<Integer, MacNetworks.IFdata> data) {
      int i = netIF.queryNetworkInterface().getIndex();
      if (data.containsKey(i)) {
         MacNetworks.IFdata iFdata = (MacNetworks.IFdata)data.get(i);
         netIF.setIfType(MacNetworks.IFdata.Z(iFdata));
         netIF.setBytesSent(MacNetworks.IFdata.f(iFdata));
         netIF.setBytesRecv(MacNetworks.IFdata.l(iFdata));
         netIF.setPacketsSent(MacNetworks.IFdata.w(iFdata));
         netIF.setPacketsRecv(MacNetworks.IFdata.n(iFdata));
         netIF.setOutErrors(MacNetworks.IFdata.a(iFdata));
         netIF.setInErrors(MacNetworks.IFdata.p(iFdata));
         netIF.setCollisions(MacNetworks.IFdata.P(iFdata));
         netIF.setInDrops(MacNetworks.IFdata.K(iFdata));
         netIF.setSpeed(MacNetworks.IFdata.L(iFdata));
         netIF.setTimeStamp(MacNetworks.IFdata.e(iFdata));
         return true;
      } else {
         return false;
      }
   }

   private static class IFdata {
      private int ifType;
      private long oPackets;
      private long iPackets;
      private long oBytes;
      private long iBytes;
      private long oErrors;
      private long iErrors;
      private long collisions;
      private long iDrops;
      private long speed;
      private long timeStamp;

      public IFdata(
         int ifType,
         long oPackets,
         long iPackets,
         long oBytes,
         long iBytes,
         long oErrors,
         long iErrors,
         long collisions,
         long iDrops,
         long speed,
         long timeStamp
      ) {
         this.ifType = ifType;
         this.oPackets = oPackets;
         this.iPackets = iPackets;
         this.oBytes = oBytes;
         this.iBytes = iBytes;
         this.oErrors = oErrors;
         this.iErrors = iErrors;
         this.collisions = collisions;
         this.iDrops = iDrops;
         this.speed = speed;
         this.timeStamp = timeStamp;
      }

      public static int Z(MacNetworks.IFdata iFdata) {
         return iFdata.ifType;
      }

      public static long f(MacNetworks.IFdata iFdata) {
         return iFdata.oBytes;
      }

      public static long l(MacNetworks.IFdata iFdata) {
         return iFdata.iBytes;
      }

      public static long w(MacNetworks.IFdata iFdata) {
         return iFdata.oPackets;
      }

      public static long n(MacNetworks.IFdata iFdata) {
         return iFdata.iPackets;
      }

      public static long a(MacNetworks.IFdata iFdata) {
         return iFdata.oErrors;
      }

      public static long p(MacNetworks.IFdata iFdata) {
         return iFdata.iErrors;
      }

      public static long P(MacNetworks.IFdata iFdata) {
         return iFdata.collisions;
      }

      public static long K(MacNetworks.IFdata iFdata) {
         return iFdata.iDrops;
      }

      public static long L(MacNetworks.IFdata iFdata) {
         return iFdata.speed;
      }

      public static long e(MacNetworks.IFdata iFdata) {
         return iFdata.timeStamp;
      }
   }
}
