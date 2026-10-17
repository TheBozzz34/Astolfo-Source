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

   private WindowsDisks() {
   }

   public static boolean updateDiskStats(HWDiskStore diskStore) {
      String string = null;
      HWPartition[] hwPartitions = diskStore.getPartitions();
      if (hwPartitions.length > 0) {
         string = Integer.toString(hwPartitions[0].getMajor());
      } else {
         if (!diskStore.getName().startsWith("\\\\.\\PHYSICALDRIVE")) {
            return false;
         }

         string = diskStore.getName().substring("\\\\.\\PHYSICALDRIVE".length(), diskStore.getName().length());
      }

      WindowsDisks.DiskStats diskStats = queryReadWriteStats(string);
      if (WindowsDisks.DiskStats.readMapOf(diskStats).containsKey(string)) {
         diskStore.setReads((long)(WindowsDisks.DiskStats.readMapOf(diskStats).getOrDefault(string, 0L)));
         diskStore.setReadBytes((long)(WindowsDisks.DiskStats.readByteMapOf(diskStats).getOrDefault(string, 0L)));
         diskStore.setWrites((long)(WindowsDisks.DiskStats.writeMapOf(diskStats).getOrDefault(string, 0L)));
         diskStore.setWriteBytes((long)(WindowsDisks.DiskStats.writeByteMapOf(diskStats).getOrDefault(string, 0L)));
         diskStore.setCurrentQueueLength((long)(WindowsDisks.DiskStats.queueLengthMapOf(diskStats).getOrDefault(string, 0L)));
         diskStore.setTransferTime(
            WindowsDisks.DiskStats.timeStampOf(diskStats) - ((java.lang.Long)(WindowsDisks.DiskStats.idleTimeMapOf(diskStats).getOrDefault(string, WindowsDisks.DiskStats.timeStampOf(diskStats))))
         );
         diskStore.setTimeStamp(WindowsDisks.DiskStats.timeStampOf(diskStats));
         return true;
      } else {
         return false;
      }
   }

   public static HWDiskStore[] getDisks() {
      ArrayList arrayList = new ArrayList();
      WindowsDisks.DiskStats diskStats = queryReadWriteStats(null);
      WindowsDisks.PartitionMaps partitionMaps = queryPartitionMaps();
      WmiResult wmiResult = Win32DiskDrive.queryDiskDrive();

      for (int i = 0; i < wmiResult.getResultCount(); i++) {
         HWDiskStore hwDiskStore = new HWDiskStore();
         hwDiskStore.setName(WmiUtil.getString(wmiResult, Win32DiskDrive.DiskDriveProperty.NAME, i));
         hwDiskStore.setModel(
            String.format(
                  "%s %s",
                  WmiUtil.getString(wmiResult, Win32DiskDrive.DiskDriveProperty.MODEL, i),
                  WmiUtil.getString(wmiResult, Win32DiskDrive.DiskDriveProperty.MANUFACTURER, i)
               )
               .trim()
         );
         hwDiskStore.setSerial(ParseUtil.hexStringToString(WmiUtil.getString(wmiResult, Win32DiskDrive.DiskDriveProperty.SERIALNUMBER, i)));
         String string = Integer.toString(WmiUtil.getUint32(wmiResult, Win32DiskDrive.DiskDriveProperty.INDEX, i));
         hwDiskStore.setReads((long)(WindowsDisks.DiskStats.readMapOf(diskStats).getOrDefault(string, 0L)));
         hwDiskStore.setReadBytes((long)(WindowsDisks.DiskStats.readByteMapOf(diskStats).getOrDefault(string, 0L)));
         hwDiskStore.setWrites((long)(WindowsDisks.DiskStats.writeMapOf(diskStats).getOrDefault(string, 0L)));
         hwDiskStore.setWriteBytes((long)(WindowsDisks.DiskStats.writeByteMapOf(diskStats).getOrDefault(string, 0L)));
         hwDiskStore.setCurrentQueueLength((long)(WindowsDisks.DiskStats.queueLengthMapOf(diskStats).getOrDefault(string, 0L)));
         hwDiskStore.setTransferTime(
            WindowsDisks.DiskStats.timeStampOf(diskStats) - ((java.lang.Long)(WindowsDisks.DiskStats.idleTimeMapOf(diskStats).getOrDefault(string, WindowsDisks.DiskStats.timeStampOf(diskStats))))
         );
         hwDiskStore.setTimeStamp(WindowsDisks.DiskStats.timeStampOf(diskStats));
         hwDiskStore.setSize(WmiUtil.getUint64(wmiResult, Win32DiskDrive.DiskDriveProperty.SIZE, i));
         ArrayList arrayList2 = new ArrayList();
         List list = (List)WindowsDisks.PartitionMaps.y(partitionMaps).get(hwDiskStore.getName());
         if (list != null && !list.isEmpty()) {
            for (Object object : list) {
               String string2 = (String)object;
               if (WindowsDisks.PartitionMaps.partitionMapOf(partitionMaps).containsKey(string2)) {
                  arrayList2.add(WindowsDisks.PartitionMaps.partitionMapOf(partitionMaps).get(string2));
               }
            }
         }

         hwDiskStore.setPartitions((oshi.hardware.HWPartition[])(arrayList2.toArray(new HWPartition[0])));
         arrayList.add(hwDiskStore);
      }

      return (oshi.hardware.HWDiskStore[])(arrayList.toArray(new HWDiskStore[0]));
   }

   private static WindowsDisks.DiskStats queryReadWriteStats(String index) {
      WindowsDisks.DiskStats diskStats = new WindowsDisks.DiskStats();
      Pair pair = PhysicalDisk.queryDiskCounters();
      List list = (List)pair.getA();
      Map map = (Map)pair.getB();
      WindowsDisks.DiskStats.setTimeStampOf(diskStats, System.currentTimeMillis());
      List list2 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKREADSPERSEC);
      List list3 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKREADBYTESPERSEC);
      List list4 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITESPERSEC);
      List list5 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITEBYTESPERSEC);
      List list6 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.CURRENTDISKQUEUELENGTH);
      List list7 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.PERCENTIDLETIME);
      if (!list.isEmpty() && list2 != null && list3 != null && list4 != null && list5 != null && list6 != null && list7 != null) {
         for (int i = 0; i < list.size(); i++) {
            String string = getIndexFromName((String)list.get(i));
            if (index == null || index.equals(string)) {
               WindowsDisks.DiskStats.readMapOf(diskStats).put(string, list2.get(i));
               WindowsDisks.DiskStats.readByteMapOf(diskStats).put(string, list3.get(i));
               WindowsDisks.DiskStats.writeMapOf(diskStats).put(string, list4.get(i));
               WindowsDisks.DiskStats.writeByteMapOf(diskStats).put(string, list5.get(i));
               WindowsDisks.DiskStats.queueLengthMapOf(diskStats).put(string, list6.get(i));
               WindowsDisks.DiskStats.idleTimeMapOf(diskStats).put(string, (Long)list7.get(i) / 10000L);
            }
         }

         return diskStats;
      } else {
         return diskStats;
      }
   }

   private static WindowsDisks.PartitionMaps queryPartitionMaps() {
      WindowsDisks.PartitionMaps partitionMaps = new WindowsDisks.PartitionMaps();
      WmiResult wmiResult = Win32DiskDriveToDiskPartition.queryDriveToPartition();

      for (int i = 0; i < wmiResult.getResultCount(); i++) {
         Matcher matcher2 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult, Win32DiskDriveToDiskPartition.DriveToPartitionProperty.ANTECEDENT, i));
         Matcher matcher3 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult, Win32DiskDriveToDiskPartition.DriveToPartitionProperty.DEPENDENT, i));
         if (matcher2.matches() && matcher3.matches()) {
            ((java.util.List)(WindowsDisks.PartitionMaps.y(partitionMaps).computeIfAbsent(matcher2.group(1).replaceAll("\\\\\\\\", "\\\\"), x -> new ArrayList<>()))).add(matcher3.group(1));
         }
      }

      WmiResult wmiResult2 = Win32LogicalDiskToPartition.queryDiskToPartition();

      for (int j = 0; j < wmiResult2.getResultCount(); j++) {
         Matcher matcher4 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult2, Win32LogicalDiskToPartition.DiskToPartitionProperty.ANTECEDENT, j));
         Matcher matcher5 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult2, Win32LogicalDiskToPartition.DiskToPartitionProperty.DEPENDENT, j));
         if (matcher4.matches() && matcher5.matches()) {
            WindowsDisks.PartitionMaps.partitionToLogicalDriveMapOf(partitionMaps).put(matcher4.group(1), matcher5.group(1) + "\\");
         }
      }

      WmiResult wmiResult3 = Win32DiskPartition.queryPartition();

      for (int k = 0; k < wmiResult3.getResultCount(); k++) {
         String string = WmiUtil.getString(wmiResult3, Win32DiskPartition.DiskPartitionProperty.DEVICEID, k);
         String string2 = (java.lang.String)(WindowsDisks.PartitionMaps.partitionToLogicalDriveMapOf(partitionMaps).getOrDefault(string, ""));
         String string3 = "";
         if (!string2.isEmpty()) {
            char[] chars = new char[255];
            Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(string2, chars, 255);
            string3 = ParseUtil.parseUuidOrDefault(new String(chars).trim(), "");
         }

         WindowsDisks.PartitionMaps.partitionMapOf(partitionMaps)
            .put(
               string,
               new HWPartition(
                  WmiUtil.getString(wmiResult3, Win32DiskPartition.DiskPartitionProperty.NAME, k),
                  WmiUtil.getString(wmiResult3, Win32DiskPartition.DiskPartitionProperty.TYPE, k),
                  WmiUtil.getString(wmiResult3, Win32DiskPartition.DiskPartitionProperty.DESCRIPTION, k),
                  string3,
                  WmiUtil.getUint64(wmiResult3, Win32DiskPartition.DiskPartitionProperty.SIZE, k),
                  WmiUtil.getUint32(wmiResult3, Win32DiskPartition.DiskPartitionProperty.DISKINDEX, k),
                  WmiUtil.getUint32(wmiResult3, Win32DiskPartition.DiskPartitionProperty.INDEX, k),
                  string2
               )
            );
      }

      return partitionMaps;
   }

   private static String getIndexFromName(String s) {
      return s.isEmpty() ? s : s.split("\\s")[0];
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

      public static Map readMapOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.readMap;
      }

      public static Map readByteMapOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.readByteMap;
      }

      public static Map writeMapOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.writeMap;
      }

      public static Map writeByteMapOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.writeByteMap;
      }

      public static Map queueLengthMapOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.queueLengthMap;
      }

      public static long timeStampOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.timeStamp;
      }

      public static Map idleTimeMapOf(WindowsDisks.DiskStats diskStats) {
         return diskStats.idleTimeMap;
      }

      public static long setTimeStampOf(WindowsDisks.DiskStats diskStats, long l) {
         return diskStats.timeStamp = l;
      }
   }

   private static final class PartitionMaps {
      private final Map<String, List<String>> driveToPartitionMap = new HashMap<>();
      private final Map<String, String> partitionToLogicalDriveMap = new HashMap<>();
      private final Map<String, HWPartition> partitionMap = new HashMap<>();

      public PartitionMaps() {
      }

      public static Map y(WindowsDisks.PartitionMaps partitionMaps) {
         return partitionMaps.driveToPartitionMap;
      }

      public static Map partitionMapOf(WindowsDisks.PartitionMaps partitionMaps) {
         return partitionMaps.partitionMap;
      }

      public static Map partitionToLogicalDriveMapOf(WindowsDisks.PartitionMaps partitionMaps) {
         return partitionMaps.partitionToLogicalDriveMap;
      }
   }
}
