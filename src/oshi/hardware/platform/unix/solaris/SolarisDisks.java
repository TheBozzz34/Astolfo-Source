package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import com.sun.jna.platform.unix.solaris.LibKstat.KstatIO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisDisks {
   private static final long a = 71676118371293L;

   private SolarisDisks() {
   }

   public static boolean updateDiskStats(HWDiskStore var0) {
      try (KstatUtil.KstatChain var4 = KstatUtil.openChain()) {
         Kstat var6 = var4.lookup(null, 0, var0.getName());
         if (var6 != null && var4.read(var6)) {
            KstatIO var7 = new KstatIO(var6.ks_data);
            var0.setReads(var7.reads);
            var0.setWrites(var7.writes);
            var0.setReadBytes(var7.nread);
            var0.setWriteBytes(var7.nwritten);
            var0.setCurrentQueueLength((long)var7.wcnt + var7.rcnt);
            var0.setTransferTime(var7.rtime / 1000000L);
            var0.setTimeStamp(var6.ks_snaptime / 1000000L);
            return true;
         }
      }

      return false;
   }

   public static HWDiskStore[] getDisks() {
      HashMap var4 = new HashMap();
      List var5 = ExecutingCommand.runNative("iostat -er");
      HashMap var6 = new HashMap();
      List var7 = ExecutingCommand.runNative("iostat -ern");

      for (int var9 = 0; var9 < var5.size() && var9 < var7.size(); var9++) {
         String var8 = (String)var5.get(var9);
         String[] var10 = var8.split(",");
         if (var10.length >= 5 && !"device".equals(var10[0])) {
            HWDiskStore var11 = new HWDiskStore();
            var11.setName(var10[0]);
            var4.put(var10[0], var11);
            String var12 = (String)var7.get(var9);
            String[] var13 = var12.split(",");
            if (var13.length >= 5 && !"device".equals(var13[4])) {
               var6.put(var10[0], var13[4]);
            }
         }
      }

      HashMap var28 = new HashMap();
      List var29 = ExecutingCommand.runNative("lshal");
      String var26 = "";

      for (Object var32$raw : var29) {
         String var32 = (String)var32$raw;
         if (var32.startsWith("udi ")) {
            String var35 = ParseUtil.getSingleQuoteStringValue(var32);
            var26 = var35.substring(var35.lastIndexOf(47) + 1);
         } else {
            var32 = var32.trim();
            if (var32.startsWith("block.major")) {
               var28.put(var26, ParseUtil.getFirstIntValue(var32));
            }
         }
      }

      var5 = ExecutingCommand.runNative("iostat -Er");
      var26 = "";
      String var31 = "";
      String var34 = "";
      String var36 = "";
      String var14 = "";
      long var15 = 0L;

      for (Object var18$raw : var5) {
         String var18 = (String)var18$raw;
         String[] var19 = var18.split(",");

         for (String var23 : var19) {
            var23 = var23.trim();
            if (var4.keySet().contains(var23)) {
               if (!var26.isEmpty()) {
                  updateStore((HWDiskStore)var4.get(var26), var31, var34, var36, var14, var15, (String)var6.get(var26), (int)(var28.getOrDefault(var26, 0)));
               }

               var26 = var23;
               var31 = "";
               var34 = "";
               var36 = "";
               var14 = "";
               var15 = 0L;
            } else if (var23.startsWith("Model:")) {
               var31 = var23.replace("Model:", "").trim();
            } else if (var23.startsWith("Serial No:")) {
               var14 = var23.replace("Serial No:", "").trim();
            } else if (var23.startsWith("Vendor:")) {
               var34 = var23.replace("Vendor:", "").trim();
            } else if (var23.startsWith("Product:")) {
               var36 = var23.replace("Product:", "").trim();
            } else if (var23.startsWith("Size:")) {
               String[] var24 = var23.split("<");
               if (var24.length > 1) {
                  var24 = ParseUtil.Q.split(var24[1]);
                  var15 = ParseUtil.parseLongOrDefault(var24[0], 0L);
               }
            }
         }

         if (!var26.isEmpty()) {
            updateStore((HWDiskStore)var4.get(var26), var31, var34, var36, var14, var15, (String)var6.get(var26), (int)(var28.getOrDefault(var26, 0)));
         }
      }

      HWDiskStore[] var37 = new HWDiskStore[var4.keySet().size()];
      int var38 = 0;

      for (Object var40$raw : var4.entrySet()) {
         Entry var40 = (Entry)var40$raw;
         updateDiskStats((HWDiskStore)var40.getValue());
         var37[var38++] = (HWDiskStore)var40.getValue();
      }

      return var37;
   }

   private static void updateStore(HWDiskStore var0, String var1, String var2, String var3, String var4, long var5, String var7, int var8) {
      var0.setModel(var1.isEmpty() ? (var2 + " " + var3).trim() : var1);
      var0.setSerial(var4);
      var0.setSize(var5);
      ArrayList var12 = new ArrayList();
      List var13 = ExecutingCommand.runNative("prtvtoc /dev/dsk/" + var7);
      if (var13.size() > 1) {
         int var14 = 0;

         for (Object var17$raw : var13) {
            String var17 = (String)var17$raw;
            if (var17.startsWith("*")) {
               if (var17.endsWith("bytes/sector")) {
                  String[] var15 = ParseUtil.Q.split(var17);
                  if (var15.length > 0) {
                     var14 = ParseUtil.parseIntOrDefault(var15[1], 0);
                  }
               }
            } else if (var14 > 0) {
               String[] var21 = ParseUtil.Q.split(var17.trim());
               if (var21.length >= 6 && !"2".equals(var21[0])) {
                  HWPartition var18 = new HWPartition();
                  var18.setIdentification(var7 + "s" + var21[0]);
                  var18.setMajor(var8);
                  var18.setMinor(ParseUtil.parseIntOrDefault(var21[0], 0));
                  switch (ParseUtil.parseIntOrDefault(var21[1], 0)) {
                     case 1:
                     case 24:
                        var18.setName("boot");
                        break;
                     case 2:
                        var18.setName("root");
                        break;
                     case 3:
                        var18.setName("swap");
                        break;
                     case 4:
                        var18.setName("usr");
                        break;
                     case 5:
                        var18.setName("backup");
                        break;
                     case 6:
                        var18.setName("stand");
                        break;
                     case 7:
                        var18.setName("var");
                        break;
                     case 8:
                        var18.setName("home");
                        break;
                     case 9:
                        var18.setName("altsctr");
                        break;
                     case 10:
                        var18.setName("cache");
                        break;
                     case 11:
                        var18.setName("reserved");
                        break;
                     case 12:
                        var18.setName("system");
                        break;
                     case 13:
                     case 16:
                     case 17:
                     case 18:
                     case 19:
                     case 20:
                     case 21:
                     case 22:
                     case 23:
                     default:
                        var18.setName("unknown");
                        break;
                     case 14:
                        var18.setName("public region");
                        break;
                     case 15:
                        var18.setName("private region");
                  }

                  switch (var21[2]) {
                     case "00":
                        var18.setType("wm");
                        break;
                     case "10":
                        var18.setType("rm");
                        break;
                     case "01":
                        var18.setType("wu");
                        break;
                     default:
                        var18.setType("ru");
                  }

                  var18.setSize(var14 * ParseUtil.parseLongOrDefault(var21[4], 0L));
                  if (var21.length > 6) {
                     var18.setMountPoint(var21[6]);
                  }

                  var12.add(var18);
               }
            }
         }

         var0.setPartitions((oshi.hardware.HWPartition[])(var12.toArray(new HWPartition[0])));
      }
   }
}
