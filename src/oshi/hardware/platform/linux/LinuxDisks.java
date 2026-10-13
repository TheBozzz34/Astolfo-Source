package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.jna.platform.linux.Udev;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class LinuxDisks {
   private static final int SECTORSIZE = 512;
   private static final int[] UDEV_STAT_ORDERS = new int[LinuxDisks.UdevStat.values().length];
   private static final int UDEV_STAT_LENGTH;
   private static final long a = 138698851331683L;

   private LinuxDisks() {
   }

   public static HWDiskStore[] getDisks() {
      return getDisks(null);
   }

   private static HWDiskStore[] getDisks(HWDiskStore var0) {
      HWDiskStore var4 = null;
      ArrayList var5 = new ArrayList();
      Map var6 = readMountsMap();
      Udev.UdevHandle var7 = Udev.INSTANCE.udev_new();
      Udev.UdevEnumerate var8 = Udev.INSTANCE.udev_enumerate_new(var7);
      Udev.INSTANCE.udev_enumerate_add_match_subsystem(var8, "block");
      Udev.INSTANCE.udev_enumerate_scan_devices(var8);

      Udev.UdevDevice var10;
      for (Udev.UdevListEntry var9 = Udev.INSTANCE.udev_enumerate_get_list_entry(var8);
         (var10 = Udev.INSTANCE.udev_device_new_from_syspath(var7, Udev.INSTANCE.udev_list_entry_get_name(var9))) != null;
         var9 = Udev.INSTANCE.udev_list_entry_get_next(var9)
      ) {
         String var11 = Udev.INSTANCE.udev_device_get_devnode(var10);
         if (var11 != null && !var11.startsWith("/dev/loop") && !var11.startsWith("/dev/ram")) {
            if ("disk".equals(Udev.INSTANCE.udev_device_get_devtype(var10))) {
               var4 = new HWDiskStore();
               var4.setName(var11);
               var4.setModel(
                  Udev.INSTANCE.udev_device_get_property_value(var10, "ID_MODEL") == null
                     ? "Unknown"
                     : Udev.INSTANCE.udev_device_get_property_value(var10, "ID_MODEL")
               );
               var4.setSerial(
                  Udev.INSTANCE.udev_device_get_property_value(var10, "ID_SERIAL_SHORT") == null
                     ? "Unknown"
                     : Udev.INSTANCE.udev_device_get_property_value(var10, "ID_SERIAL_SHORT")
               );
               var4.setSize(ParseUtil.parseLongOrDefault(Udev.INSTANCE.udev_device_get_sysattr_value(var10, "size"), 0L) * 512L);
               if (var0 == null) {
                  var4.setPartitions(new HWPartition[0]);
                  computeDiskStats(var4, var10);
                  var5.add(var4);
               } else if (var4.equals(var0)) {
                  computeDiskStats(var0, var10);
                  var5.add(var0);
                  Udev.INSTANCE.udev_device_unref(var10);
                  break;
               }
            } else if ("partition".equals(Udev.INSTANCE.udev_device_get_devtype(var10)) && var4 != null) {
               HWPartition[] var12 = new HWPartition[var4.getPartitions().length + 1];
               System.arraycopy(var4.getPartitions(), 0, var12, 0, var4.getPartitions().length);
               String var13 = Udev.INSTANCE.udev_device_get_devnode(var10);
               var12[var12.length - 1] = new HWPartition(
                  var13,
                  Udev.INSTANCE.udev_device_get_sysname(var10),
                  Udev.INSTANCE.udev_device_get_property_value(var10, "ID_FS_TYPE") == null
                     ? "partition"
                     : Udev.INSTANCE.udev_device_get_property_value(var10, "ID_FS_TYPE"),
                  Udev.INSTANCE.udev_device_get_property_value(var10, "ID_FS_UUID") == null
                     ? ""
                     : Udev.INSTANCE.udev_device_get_property_value(var10, "ID_FS_UUID"),
                  ParseUtil.parseLongOrDefault(Udev.INSTANCE.udev_device_get_sysattr_value(var10, "size"), 0L) * 512L,
                  ParseUtil.parseIntOrDefault(Udev.INSTANCE.udev_device_get_property_value(var10, "MAJOR"), 0),
                  ParseUtil.parseIntOrDefault(Udev.INSTANCE.udev_device_get_property_value(var10, "MINOR"), 0),
                  (java.lang.String)(var6.getOrDefault(var13, ""))
               );
               var4.setPartitions(var12);
            }
         }

         Udev.INSTANCE.udev_device_unref(var10);
      }

      Udev.INSTANCE.udev_enumerate_unref(var8);
      Udev.INSTANCE.udev_unref(var7);
      return (oshi.hardware.HWDiskStore[])(var5.toArray(new HWDiskStore[0]));
   }

   public static boolean updateDiskStats(HWDiskStore var0) {
      return 0 < getDisks(var0).length;
   }

   private static Map<String, String> readMountsMap() {
      HashMap var3 = new HashMap();

      for (String var6 : FileUtil.readFile(ProcPath.MOUNTS)) {
         String[] var7 = ParseUtil.Q.split(var6);
         if (var7.length >= 2 && var7[0].startsWith("/dev/")) {
            var3.put(var7[0], var7[1]);
         }
      }

      return var3;
   }

   private static void computeDiskStats(HWDiskStore var0, Udev.UdevDevice var1) {
      String var4 = Udev.INSTANCE.udev_device_get_sysattr_value(var1, "stat");
      long[] var5 = ParseUtil.parseStringToLongArray(var4, UDEV_STAT_ORDERS, UDEV_STAT_LENGTH, ' ');
      var0.setTimeStamp(System.currentTimeMillis());
      var0.setReads(var5[LinuxDisks.UdevStat.READS.ordinal()]);
      var0.setReadBytes(var5[LinuxDisks.UdevStat.READ_BYTES.ordinal()] * 512L);
      var0.setWrites(var5[LinuxDisks.UdevStat.WRITES.ordinal()]);
      var0.setWriteBytes(var5[LinuxDisks.UdevStat.WRITE_BYTES.ordinal()] * 512L);
      var0.setCurrentQueueLength(var5[LinuxDisks.UdevStat.QUEUE_LENGTH.ordinal()]);
      var0.setTransferTime(var5[LinuxDisks.UdevStat.ACTIVE_MS.ordinal()]);
   }

   static {
      long var20 = a ^ 11268348412566L;
      long var10000 = var20 ^ 3924662742454959701L;

      for (LinuxDisks.UdevStat var25 : LinuxDisks.UdevStat.values()) {
         UDEV_STAT_ORDERS[var25.ordinal()] = var25.getOrder();
      }

      String var26 = FileUtil.getStringFromFile(ProcPath.DISKSTATS);
      int var27 = 11;
      if (!var26.isEmpty()) {
         var27 = ParseUtil.countStringToLongArray(var26, ' ');
      }

      UDEV_STAT_LENGTH = var27;
   }

   public enum UdevStat {
      READS(0),
      READ_BYTES(2),
      WRITES(4),
      WRITE_BYTES(6),
      QUEUE_LENGTH(8),
      ACTIVE_MS(9);

      private int order;

      public int getOrder() {
         return this.order;
      }

      UdevStat(int var3) {
         this.order = var3;
      }
   }
}
