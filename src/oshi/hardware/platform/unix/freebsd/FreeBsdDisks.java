package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdDisks {
   private static final Pattern MOUNT_PATTERN = Pattern.compile("/dev/(\\S+p\\d+) on (\\S+) .*");
   private static final long a = 137318154758226L;

   private FreeBsdDisks() {
   }

   public static boolean updateDiskStats(HWDiskStore var0) {
      List var4 = ExecutingCommand.runNative("iostat -Ix " + var0.getName());
      long var5 = System.currentTimeMillis();
      boolean var7 = false;

      for (Object var9$raw : var4) {
         String var9 = (String)var9$raw;
         String[] var10 = ParseUtil.Q.split(var9);
         if (var10.length >= 7 && var10[0].equals(var0.getName())) {
            var7 = true;
            var0.setReads((long)ParseUtil.parseDoubleOrDefault(var10[1], 0.0));
            var0.setWrites((long)ParseUtil.parseDoubleOrDefault(var10[2], 0.0));
            var0.setReadBytes((long)(ParseUtil.parseDoubleOrDefault(var10[3], 0.0) * 1024.0));
            var0.setWriteBytes((long)(ParseUtil.parseDoubleOrDefault(var10[4], 0.0) * 1024.0));
            var0.setCurrentQueueLength(ParseUtil.parseLongOrDefault(var10[5], 0L));
            var0.setTransferTime((long)(ParseUtil.parseDoubleOrDefault(var10[6], 0.0) * 1000.0));
            var0.setTimeStamp(var5);
         }
      }

      return var7;
   }

   public static HWDiskStore[] getDisks() {
      long var0 = a ^ 104737533163670L;
      HashMap var4 = new HashMap();

      for (String var6 : ExecutingCommand.runNative("mount")) {
         Matcher var7 = MOUNT_PATTERN.matcher(var6);
         if (var7.matches()) {
            var4.put(var7.group(1), var7.group(2));
         }
      }

      HashMap var17 = new HashMap();
      List var18 = Arrays.asList(ParseUtil.Q.split(BsdSysctlUtil.sysctl("kern.disks", "")));
      ArrayList var19 = new ArrayList();
      long var10000 = 7497337034854158552L ^ var0;
      List var8 = ExecutingCommand.runNative("iostat -Ix");
      long var9 = System.currentTimeMillis();

      for (Object var12$raw : var8) {
         String var12 = (String)var12$raw;
         String[] var13 = ParseUtil.Q.split(var12);
         if (var13.length >= 7 && var18.contains(var13[0])) {
            HWDiskStore var14 = new HWDiskStore();
            var14.setName(var13[0]);
            var14.setReads((long)ParseUtil.parseDoubleOrDefault(var13[1], 0.0));
            var14.setWrites((long)ParseUtil.parseDoubleOrDefault(var13[2], 0.0));
            var14.setReadBytes((long)(ParseUtil.parseDoubleOrDefault(var13[3], 0.0) * 1024.0));
            var14.setWriteBytes((long)(ParseUtil.parseDoubleOrDefault(var13[4], 0.0) * 1024.0));
            var14.setCurrentQueueLength(ParseUtil.parseLongOrDefault(var13[5], 0L));
            var14.setTransferTime((long)(ParseUtil.parseDoubleOrDefault(var13[6], 0.0) * 1000.0));
            var14.setTimeStamp(var9);
            var17.put(var13[0], var14);
         }
      }

      List var20 = ExecutingCommand.runNative("geom disk list");
      HWDiskStore var22 = null;

      for (Object var26$raw : var20) {
         String var26 = (String)var26$raw;
         if (var26.startsWith("Geom name:")) {
            if (var22 != null) {
               setPartitions(var22, var19);
            }

            String var15 = var26.substring(var26.lastIndexOf(32) + 1);
            if (var18.contains(var15)) {
               var22 = (HWDiskStore)var17.get(var15);
               if (var22 == null) {
                  var22 = new HWDiskStore();
                  var22.setName(var15);
               }
            }
         }

         if (var22 != null) {
            var26 = var26.trim();
            if (var26.startsWith("Mediasize:")) {
               String[] var30 = ParseUtil.Q.split(var26);
               if (var30.length > 1) {
                  var22.setSize(ParseUtil.parseLongOrDefault(var30[1], 0L));
               }
            }

            if (var26.startsWith("descr:")) {
               var22.setModel(var26.replace("descr:", "").trim());
            }

            if (var26.startsWith("ident:")) {
               var22.setSerial(var26.replace("ident:", "").replace("(null)", "").trim());
            }
         }
      }

      var20 = ExecutingCommand.runNative("geom part list");
      var22 = null;
      HWPartition var25 = null;

      for (Object var31$raw : var20) {
         String var31 = (String)var31$raw;
         var31 = var31.trim();
         if (var31.startsWith("Geom name:")) {
            String var16 = var31.substring(var31.lastIndexOf(32) + 1);
            if (var18.contains(var16)) {
               var22 = (HWDiskStore)var17.get(var16);
               if (var22 == null) {
                  var22 = new HWDiskStore();
                  var22.setName(var16);
               }
            }
         }

         if (var22 != null) {
            if (var31.contains("Name:")) {
               if (var25 != null) {
                  var19.add(var25);
                  var25 = null;
               }

               String var33 = var31.substring(var31.lastIndexOf(32) + 1);
               if (var33.startsWith(var22.getName())) {
                  var25 = new HWPartition();
                  var25.setIdentification(var33);
                  var25.setName(var33);
                  var25.setMountPoint((java.lang.String)(var4.getOrDefault(var33, "")));
               }
            }

            if (var25 != null) {
               String[] var34 = ParseUtil.Q.split(var31);
               if (var34.length >= 2) {
                  if (var31.startsWith("Mediasize:")) {
                     var25.setSize(ParseUtil.parseLongOrDefault(var34[1], 0L));
                  } else if (var31.startsWith("rawuuid:")) {
                     var25.setUuid(var34[1]);
                  } else if (var31.startsWith("type:")) {
                     var25.setType(var34[1]);
                  }
               }
            }
         }
      }

      if (var22 != null) {
         setPartitions(var22, var19);
      }

      ArrayList var29 = new ArrayList(var17.keySet().size());
      var29.addAll(var17.values());
      Collections.sort(var29);
      return (oshi.hardware.HWDiskStore[])(var29.toArray(new HWDiskStore[0]));
   }

   private static void setPartitions(HWDiskStore var0, List<HWPartition> var1) {
      HWPartition[] var5 = new HWPartition[var1.size()];
      int var6 = 0;
      Collections.sort(var1);

      for (HWPartition var8 : var1) {
         var8.setMinor(ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("stat -f %i /dev/" + var8.getName()), 0));
         var5[var6++] = var8;
      }

      var1.clear();
      var0.setPartitions(var5);
   }
}
