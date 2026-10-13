package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.PhysicalDisk;
import oshi.driver.windows.wmi.Win32DiskDrive;
import oshi.driver.windows.wmi.Win32DiskDriveToDiskPartition;
import oshi.driver.windows.wmi.Win32DiskPartition;
import oshi.driver.windows.wmi.Win32LogicalDiskToPartition;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class WindowsDisks {
   private static final Pattern DEVICE_ID = Pattern.compile(".*\\.DeviceID=\"(.*)\"");
   private static final int BUFSIZE = 255;
   private static final long a = 9831591367270L;

   private WindowsDisks() {
   }

   public static boolean updateDiskStats(HWDiskStore var0) {
      String var4 = null;
      HWPartition[] var5 = var0.getPartitions();
      if (var5.length > 0) {
         var4 = Integer.toString(var5[0].getMajor());
      } else {
         if (!var0.getName().startsWith("\\\\.\\PHYSICALDRIVE")) {
            return false;
         }

         var4 = var0.getName().substring("\\\\.\\PHYSICALDRIVE".length(), var0.getName().length());
      }

      WindowsDisks.DiskStats var6 = queryReadWriteStats(var4);
      if (WindowsDisks.DiskStats.v(var6).containsKey(var4)) {
         var0.setReads((long)(WindowsDisks.DiskStats.v(var6).getOrDefault(var4, 0L)));
         var0.setReadBytes((long)(WindowsDisks.DiskStats.N(var6).getOrDefault(var4, 0L)));
         var0.setWrites((long)(WindowsDisks.DiskStats.o(var6).getOrDefault(var4, 0L)));
         var0.setWriteBytes((long)(WindowsDisks.DiskStats.T(var6).getOrDefault(var4, 0L)));
         var0.setCurrentQueueLength((long)(WindowsDisks.DiskStats.u(var6).getOrDefault(var4, 0L)));
         var0.setTransferTime(WindowsDisks.DiskStats.Y(var6) - ((java.lang.Long)(WindowsDisks.DiskStats.I(var6).getOrDefault(var4, WindowsDisks.DiskStats.Y(var6)))));
         var0.setTimeStamp(WindowsDisks.DiskStats.Y(var6));
         return true;
      } else {
         return false;
      }
   }

   public static HWDiskStore[] getDisks() {
      ArrayList var3 = new ArrayList();
      WindowsDisks.DiskStats var4 = queryReadWriteStats(null);
      WindowsDisks.PartitionMaps var5 = queryPartitionMaps();
      WmiResult var6 = Win32DiskDrive.queryDiskDrive();

      for (int var7 = 0; var7 < var6.getResultCount(); var7++) {
         HWDiskStore var8 = new HWDiskStore();
         var8.setName(WmiUtil.getString(var6, Win32DiskDrive.DiskDriveProperty.NAME, var7));
         var8.setModel(
            String.format(
                  "%s %s",
                  WmiUtil.getString(var6, Win32DiskDrive.DiskDriveProperty.MODEL, var7),
                  WmiUtil.getString(var6, Win32DiskDrive.DiskDriveProperty.MANUFACTURER, var7)
               )
               .trim()
         );
         var8.setSerial(ParseUtil.hexStringToString(WmiUtil.getString(var6, Win32DiskDrive.DiskDriveProperty.SERIALNUMBER, var7)));
         String var9 = Integer.toString(WmiUtil.getUint32(var6, Win32DiskDrive.DiskDriveProperty.INDEX, var7));
         var8.setReads((long)(WindowsDisks.DiskStats.v(var4).getOrDefault(var9, 0L)));
         var8.setReadBytes((long)(WindowsDisks.DiskStats.N(var4).getOrDefault(var9, 0L)));
         var8.setWrites((long)(WindowsDisks.DiskStats.o(var4).getOrDefault(var9, 0L)));
         var8.setWriteBytes((long)(WindowsDisks.DiskStats.T(var4).getOrDefault(var9, 0L)));
         var8.setCurrentQueueLength((long)(WindowsDisks.DiskStats.u(var4).getOrDefault(var9, 0L)));
         var8.setTransferTime(WindowsDisks.DiskStats.Y(var4) - ((java.lang.Long)(WindowsDisks.DiskStats.I(var4).getOrDefault(var9, WindowsDisks.DiskStats.Y(var4)))));
         var8.setTimeStamp(WindowsDisks.DiskStats.Y(var4));
         var8.setSize(WmiUtil.getUint64(var6, Win32DiskDrive.DiskDriveProperty.SIZE, var7));
         ArrayList var10 = new ArrayList();
         List var11 = (List)WindowsDisks.PartitionMaps.y(var5).get(var8.getName());
         if (var11 != null && !var11.isEmpty()) {
            for (Object var13$raw : var11) {
               String var13 = (String)var13$raw;
               if (WindowsDisks.PartitionMaps.a(var5).containsKey(var13)) {
                  var10.add(WindowsDisks.PartitionMaps.a(var5).get(var13));
               }
            }
         }

         var8.setPartitions((oshi.hardware.HWPartition[])(var10.toArray(new HWPartition[0])));
         var3.add(var8);
      }

      return (oshi.hardware.HWDiskStore[])(var3.toArray(new HWDiskStore[0]));
   }

   private static WindowsDisks.DiskStats queryReadWriteStats(String var0) {
      WindowsDisks.DiskStats var4 = new WindowsDisks.DiskStats();
      Pair var5 = PhysicalDisk.queryDiskCounters();
      List var6 = (List)var5.getA();
      Map var7 = (Map)var5.getB();
      WindowsDisks.DiskStats.l(var4, System.currentTimeMillis());
      List var8 = (List)var7.get(PhysicalDisk.PhysicalDiskProperty.DISKREADSPERSEC);
      List var9 = (List)var7.get(PhysicalDisk.PhysicalDiskProperty.DISKREADBYTESPERSEC);
      List var10 = (List)var7.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITESPERSEC);
      List var11 = (List)var7.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITEBYTESPERSEC);
      List var12 = (List)var7.get(PhysicalDisk.PhysicalDiskProperty.CURRENTDISKQUEUELENGTH);
      List var13 = (List)var7.get(PhysicalDisk.PhysicalDiskProperty.PERCENTIDLETIME);
      if (!var6.isEmpty() && var8 != null && var9 != null && var10 != null && var11 != null && var12 != null && var13 != null) {
         for (int var14 = 0; var14 < var6.size(); var14++) {
            String var15 = getIndexFromName((String)var6.get(var14));
            if (var0 == null || var0.equals(var15)) {
               WindowsDisks.DiskStats.v(var4).put(var15, var8.get(var14));
               WindowsDisks.DiskStats.N(var4).put(var15, var9.get(var14));
               WindowsDisks.DiskStats.o(var4).put(var15, var10.get(var14));
               WindowsDisks.DiskStats.T(var4).put(var15, var11.get(var14));
               WindowsDisks.DiskStats.u(var4).put(var15, var12.get(var14));
               WindowsDisks.DiskStats.I(var4).put(var15, (Long)var13.get(var14) / 10000L);
            }
         }

         return var4;
      } else {
         return var4;
      }
   }

   private static WindowsDisks.PartitionMaps queryPartitionMaps() {
      WindowsDisks.PartitionMaps var3 = new WindowsDisks.PartitionMaps();
      WmiResult var6 = Win32DiskDriveToDiskPartition.queryDriveToPartition();

      for (int var7 = 0; var7 < var6.getResultCount(); var7++) {
         Matcher var4 = DEVICE_ID.matcher(WmiUtil.getRefString(var6, Win32DiskDriveToDiskPartition.DriveToPartitionProperty.ANTECEDENT, var7));
         Matcher var5 = DEVICE_ID.matcher(WmiUtil.getRefString(var6, Win32DiskDriveToDiskPartition.DriveToPartitionProperty.DEPENDENT, var7));
         if (var4.matches() && var5.matches()) {
            ((java.util.List)(WindowsDisks.PartitionMaps.y(var3).computeIfAbsent(var4.group(1).replaceAll("\\\\\\\\", "\\\\"), var0 -> new ArrayList<>()))).add(var5.group(1));
         }
      }

      WmiResult var16 = Win32LogicalDiskToPartition.queryDiskToPartition();

      for (int var8 = 0; var8 < var16.getResultCount(); var8++) {
         Matcher var14 = DEVICE_ID.matcher(WmiUtil.getRefString(var16, Win32LogicalDiskToPartition.DiskToPartitionProperty.ANTECEDENT, var8));
         Matcher var15 = DEVICE_ID.matcher(WmiUtil.getRefString(var16, Win32LogicalDiskToPartition.DiskToPartitionProperty.DEPENDENT, var8));
         if (var14.matches() && var15.matches()) {
            WindowsDisks.PartitionMaps.E(var3).put(var14.group(1), var15.group(1) + "\\");
         }
      }

      WmiResult var17 = Win32DiskPartition.queryPartition();

      for (int var9 = 0; var9 < var17.getResultCount(); var9++) {
         String var10 = WmiUtil.getString(var17, Win32DiskPartition.DiskPartitionProperty.DEVICEID, var9);
         String var11 = (java.lang.String)(WindowsDisks.PartitionMaps.E(var3).getOrDefault(var10, ""));
         String var12 = "";
         if (!var11.isEmpty()) {
            char[] var13 = new char[255];
            Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(var11, var13, 255);
            var12 = ParseUtil.parseUuidOrDefault(new String(var13).trim(), "");
         }

         WindowsDisks.PartitionMaps.a(var3)
            .put(
               var10,
               new HWPartition(
                  WmiUtil.getString(var17, Win32DiskPartition.DiskPartitionProperty.NAME, var9),
                  WmiUtil.getString(var17, Win32DiskPartition.DiskPartitionProperty.TYPE, var9),
                  WmiUtil.getString(var17, Win32DiskPartition.DiskPartitionProperty.DESCRIPTION, var9),
                  var12,
                  WmiUtil.getUint64(var17, Win32DiskPartition.DiskPartitionProperty.SIZE, var9),
                  WmiUtil.getUint32(var17, Win32DiskPartition.DiskPartitionProperty.DISKINDEX, var9),
                  WmiUtil.getUint32(var17, Win32DiskPartition.DiskPartitionProperty.INDEX, var9),
                  var11
               )
            );
      }

      return var3;
   }

   private static String getIndexFromName(String var0) {
      return var0.isEmpty() ? var0 : var0.split("\\s")[0];
   }

   static {
      long var20 = a ^ 8664443112264L;
      long var10000 = var20 ^ 169967579038623761L;
   }

   private static final class DiskStats {
      private final Map<String, Long> readMap = new HashMap<>();
      private final Map<String, Long> readByteMap = new HashMap<>();
      private final Map<String, Long> writeMap = new HashMap<>();
      private final Map<String, Long> writeByteMap = new HashMap<>();
      private final Map<String, Long> queueLengthMap = new HashMap<>();
      private final Map<String, Long> idleTimeMap = new HashMap<>();
      private long timeStamp;

      public DiskStats() {
      }

      public static Map v(WindowsDisks.DiskStats var0) {
         return var0.readMap;
      }

      public static Map N(WindowsDisks.DiskStats var0) {
         return var0.readByteMap;
      }

      public static Map o(WindowsDisks.DiskStats var0) {
         return var0.writeMap;
      }

      public static Map T(WindowsDisks.DiskStats var0) {
         return var0.writeByteMap;
      }

      public static Map u(WindowsDisks.DiskStats var0) {
         return var0.queueLengthMap;
      }

      public static long Y(WindowsDisks.DiskStats var0) {
         return var0.timeStamp;
      }

      public static Map I(WindowsDisks.DiskStats var0) {
         return var0.idleTimeMap;
      }

      public static long l(WindowsDisks.DiskStats var0, long var1) {
         return var0.timeStamp = var1;
      }
   }

   private static final class PartitionMaps {
      private final Map<String, List<String>> driveToPartitionMap = new HashMap<>();
      private final Map<String, String> partitionToLogicalDriveMap = new HashMap<>();
      private final Map<String, HWPartition> partitionMap = new HashMap<>();

      public PartitionMaps() {
      }

      public static Map y(WindowsDisks.PartitionMaps var0) {
         return var0.driveToPartitionMap;
      }

      public static Map a(WindowsDisks.PartitionMaps var0) {
         return var0.partitionMap;
      }

      public static Map E(WindowsDisks.PartitionMaps var0) {
         return var0.partitionToLogicalDriveMap;
      }
   }
}
