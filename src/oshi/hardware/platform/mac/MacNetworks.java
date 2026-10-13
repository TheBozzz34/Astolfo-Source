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
   private static final long b = 55078139080763L;

   @Override
   public NetworkIF[] getNetworks() {
      ArrayList var4 = new ArrayList();
      Map var5 = queryIFdata(-1);

      for (NetworkInterface var9 : this.getNetworkInterfaces()) {
         NetworkIF var10 = new NetworkIF();
         var10.setNetworkInterface(var9);
         updateNetworkStats(var10, var5);
         var4.add(var10);
      }

      return (oshi.hardware.NetworkIF[])(var4.toArray(new NetworkIF[0]));
   }

   private static Map<Integer, MacNetworks.IFdata> queryIFdata(int var0) {
      HashMap var4 = new HashMap();
      int[] var5 = new int[]{4, 17, 0, 0, 6, 0};
      IntByReference var6 = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctl(var5, 6, null, var6, null, 0)) {
         return var4;
      }

      Memory var7 = new Memory(var6.getValue());
      if (0 != SystemB.INSTANCE.sysctl(var5, 6, var7, var6, null, 0)) {
         return var4;
      }

      int var8 = (int)(var7.size() - new IFmsgHdr().size());
      short var9 = 0;

      while (var9 < var8) {
         Pointer var10 = var7.share(var9);
         IFmsgHdr var11 = new IFmsgHdr(var10);
         var11.read();
         var9 += var11.ifm_msglen;
         if (var11.ifm_type == 18) {
            IFmsgHdr2 var12 = new IFmsgHdr2(var10);
            var12.read();
            if (var0 < 0 || var0 == var12.ifm_index) {
               var4.put(
                  Integer.valueOf(var12.ifm_index),
                  new MacNetworks.IFdata(
                     var12.ifm_data.ifi_type,
                     var12.ifm_data.ifi_opackets,
                     var12.ifm_data.ifi_ipackets,
                     var12.ifm_data.ifi_obytes,
                     var12.ifm_data.ifi_ibytes,
                     var12.ifm_data.ifi_oerrors,
                     var12.ifm_data.ifi_ierrors,
                     var12.ifm_data.ifi_collisions,
                     var12.ifm_data.ifi_iqdrops,
                     var12.ifm_data.ifi_baudrate,
                     System.currentTimeMillis()
                  )
               );
            }
         }
      }

      return var4;
   }

   public static boolean updateNetworkStats(NetworkIF var0) {
      int var1 = var0.queryNetworkInterface().getIndex();
      return updateNetworkStats(var0, queryIFdata(var1));
   }

   private static boolean updateNetworkStats(NetworkIF var0, Map<Integer, MacNetworks.IFdata> var1) {
      int var5 = var0.queryNetworkInterface().getIndex();
      if (var1.containsKey(var5)) {
         MacNetworks.IFdata var6 = (MacNetworks.IFdata)var1.get(var5);
         var0.setIfType(MacNetworks.IFdata.Z(var6));
         var0.setBytesSent(MacNetworks.IFdata.f(var6));
         var0.setBytesRecv(MacNetworks.IFdata.l(var6));
         var0.setPacketsSent(MacNetworks.IFdata.w(var6));
         var0.setPacketsRecv(MacNetworks.IFdata.n(var6));
         var0.setOutErrors(MacNetworks.IFdata.a(var6));
         var0.setInErrors(MacNetworks.IFdata.p(var6));
         var0.setCollisions(MacNetworks.IFdata.P(var6));
         var0.setInDrops(MacNetworks.IFdata.K(var6));
         var0.setSpeed(MacNetworks.IFdata.L(var6));
         var0.setTimeStamp(MacNetworks.IFdata.e(var6));
         return true;
      } else {
         return false;
      }
   }

   static {
      long var11 = b ^ 74652082511814L;
      long var10000 = var11 ^ 7851762012635518982L;
      var10000 = var11 ^ 6730855356239594532L;
      var10000 = var11 ^ 6375911885522177253L;
      var10000 = var11 ^ 7471781183167827523L;
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

      public IFdata(int var1, long var2, long var4, long var6, long var8, long var10, long var12, long var14, long var16, long var18, long var20) {
         this.ifType = var1;
         this.oPackets = var2;
         this.iPackets = var4;
         this.oBytes = var6;
         this.iBytes = var8;
         this.oErrors = var10;
         this.iErrors = var12;
         this.collisions = var14;
         this.iDrops = var16;
         this.speed = var18;
         this.timeStamp = var20;
      }

      public static int Z(MacNetworks.IFdata var0) {
         return var0.ifType;
      }

      public static long f(MacNetworks.IFdata var0) {
         return var0.oBytes;
      }

      public static long l(MacNetworks.IFdata var0) {
         return var0.iBytes;
      }

      public static long w(MacNetworks.IFdata var0) {
         return var0.oPackets;
      }

      public static long n(MacNetworks.IFdata var0) {
         return var0.iPackets;
      }

      public static long a(MacNetworks.IFdata var0) {
         return var0.oErrors;
      }

      public static long p(MacNetworks.IFdata var0) {
         return var0.iErrors;
      }

      public static long P(MacNetworks.IFdata var0) {
         return var0.collisions;
      }

      public static long K(MacNetworks.IFdata var0) {
         return var0.iDrops;
      }

      public static long L(MacNetworks.IFdata var0) {
         return var0.speed;
      }

      public static long e(MacNetworks.IFdata var0) {
         return var0.timeStamp;
      }
   }
}
