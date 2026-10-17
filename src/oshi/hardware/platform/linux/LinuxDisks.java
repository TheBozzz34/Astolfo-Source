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

   private LinuxDisks() {
   }

   public static HWDiskStore[] getDisks() {
      return getDisks(null);
   }

   private static HWDiskStore[] getDisks(HWDiskStore storeToUpdate) {
      HWDiskStore hwDiskStore = null;
      ArrayList arrayList = new ArrayList();
      Map map = readMountsMap();
      Udev.UdevHandle udevHandle = Udev.INSTANCE.udev_new();
      Udev.UdevEnumerate udevEnumerate = Udev.INSTANCE.udev_enumerate_new(udevHandle);
      Udev.INSTANCE.udev_enumerate_add_match_subsystem(udevEnumerate, "block");
      Udev.INSTANCE.udev_enumerate_scan_devices(udevEnumerate);

      Udev.UdevDevice udevDevice;
      for (Udev.UdevListEntry udevListEntry = Udev.INSTANCE.udev_enumerate_get_list_entry(udevEnumerate);
         (udevDevice = Udev.INSTANCE.udev_device_new_from_syspath(udevHandle, Udev.INSTANCE.udev_list_entry_get_name(udevListEntry))) != null;
         udevListEntry = Udev.INSTANCE.udev_list_entry_get_next(udevListEntry)
      ) {
         String string = Udev.INSTANCE.udev_device_get_devnode(udevDevice);
         if (string != null && !string.startsWith("/dev/loop") && !string.startsWith("/dev/ram")) {
            if ("disk".equals(Udev.INSTANCE.udev_device_get_devtype(udevDevice))) {
               hwDiskStore = new HWDiskStore();
               hwDiskStore.setName(string);
               hwDiskStore.setModel(
                  Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_MODEL") == null
                     ? "Unknown"
                     : Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_MODEL")
               );
               hwDiskStore.setSerial(
                  Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_SERIAL_SHORT") == null
                     ? "Unknown"
                     : Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_SERIAL_SHORT")
               );
               hwDiskStore.setSize(ParseUtil.parseLongOrDefault(Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "size"), 0L) * 512L);
               if (storeToUpdate == null) {
                  hwDiskStore.setPartitions(new HWPartition[0]);
                  computeDiskStats(hwDiskStore, udevDevice);
                  arrayList.add(hwDiskStore);
               } else if (hwDiskStore.equals(storeToUpdate)) {
                  computeDiskStats(storeToUpdate, udevDevice);
                  arrayList.add(storeToUpdate);
                  Udev.INSTANCE.udev_device_unref(udevDevice);
                  break;
               }
            } else if ("partition".equals(Udev.INSTANCE.udev_device_get_devtype(udevDevice)) && hwDiskStore != null) {
               HWPartition[] hwPartitions = new HWPartition[hwDiskStore.getPartitions().length + 1];
               System.arraycopy(hwDiskStore.getPartitions(), 0, hwPartitions, 0, hwDiskStore.getPartitions().length);
               String string2 = Udev.INSTANCE.udev_device_get_devnode(udevDevice);
               hwPartitions[hwPartitions.length - 1] = new HWPartition(
                  string2,
                  Udev.INSTANCE.udev_device_get_sysname(udevDevice),
                  Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_FS_TYPE") == null
                     ? "partition"
                     : Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_FS_TYPE"),
                  Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_FS_UUID") == null
                     ? ""
                     : Udev.INSTANCE.udev_device_get_property_value(udevDevice, "ID_FS_UUID"),
                  ParseUtil.parseLongOrDefault(Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "size"), 0L) * 512L,
                  ParseUtil.parseIntOrDefault(Udev.INSTANCE.udev_device_get_property_value(udevDevice, "MAJOR"), 0),
                  ParseUtil.parseIntOrDefault(Udev.INSTANCE.udev_device_get_property_value(udevDevice, "MINOR"), 0),
                  (java.lang.String)(map.getOrDefault(string2, ""))
               );
               hwDiskStore.setPartitions(hwPartitions);
            }
         }

         Udev.INSTANCE.udev_device_unref(udevDevice);
      }

      Udev.INSTANCE.udev_enumerate_unref(udevEnumerate);
      Udev.INSTANCE.udev_unref(udevHandle);
      return (oshi.hardware.HWDiskStore[])(arrayList.toArray(new HWDiskStore[0]));
   }

   public static boolean updateDiskStats(HWDiskStore diskStore) {
      return 0 < getDisks(diskStore).length;
   }

   private static Map<String, String> readMountsMap() {
      HashMap hashMap = new HashMap();

      for (String string : FileUtil.readFile(ProcPath.MOUNTS)) {
         String[] strings = ParseUtil.whitespaces.split(string);
         if (strings.length >= 2 && strings[0].startsWith("/dev/")) {
            hashMap.put(strings[0], strings[1]);
         }
      }

      return hashMap;
   }

   private static void computeDiskStats(HWDiskStore store, Udev.UdevDevice disk) {
      String string = Udev.INSTANCE.udev_device_get_sysattr_value(disk, "stat");
      long[] longs = ParseUtil.parseStringToLongArray(string, UDEV_STAT_ORDERS, UDEV_STAT_LENGTH, ' ');
      store.setTimeStamp(System.currentTimeMillis());
      store.setReads(longs[LinuxDisks.UdevStat.READS.ordinal()]);
      store.setReadBytes(longs[LinuxDisks.UdevStat.READ_BYTES.ordinal()] * 512L);
      store.setWrites(longs[LinuxDisks.UdevStat.WRITES.ordinal()]);
      store.setWriteBytes(longs[LinuxDisks.UdevStat.WRITE_BYTES.ordinal()] * 512L);
      store.setCurrentQueueLength(longs[LinuxDisks.UdevStat.QUEUE_LENGTH.ordinal()]);
      store.setTransferTime(longs[LinuxDisks.UdevStat.ACTIVE_MS.ordinal()]);
   }

   static {
      for (LinuxDisks.UdevStat udevStat : LinuxDisks.UdevStat.values()) {
         UDEV_STAT_ORDERS[udevStat.ordinal()] = udevStat.getOrder();
      }

      String string = FileUtil.getStringFromFile(ProcPath.DISKSTATS);
      int i = 11;
      if (!string.isEmpty()) {
         i = ParseUtil.countStringToLongArray(string, ' ');
      }

      UDEV_STAT_LENGTH = i;
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

      UdevStat(int order) {
         this.order = order;
      }
   }
}
