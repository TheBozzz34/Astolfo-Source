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

   private FreeBsdDisks() {
   }

   public static boolean updateDiskStats(HWDiskStore diskStore) {
      List list = ExecutingCommand.runNative("iostat -Ix " + diskStore.getName());
      long l = System.currentTimeMillis();
      boolean bl = false;

      for (Object object : list) {
         String string = (String)object;
         String[] strings = ParseUtil.whitespaces.split(string);
         if (strings.length >= 7 && strings[0].equals(diskStore.getName())) {
            bl = true;
            diskStore.setReads((long)ParseUtil.parseDoubleOrDefault(strings[1], 0.0));
            diskStore.setWrites((long)ParseUtil.parseDoubleOrDefault(strings[2], 0.0));
            diskStore.setReadBytes((long)(ParseUtil.parseDoubleOrDefault(strings[3], 0.0) * 1024.0));
            diskStore.setWriteBytes((long)(ParseUtil.parseDoubleOrDefault(strings[4], 0.0) * 1024.0));
            diskStore.setCurrentQueueLength(ParseUtil.parseLongOrDefault(strings[5], 0L));
            diskStore.setTransferTime((long)(ParseUtil.parseDoubleOrDefault(strings[6], 0.0) * 1000.0));
            diskStore.setTimeStamp(l);
         }
      }

      return bl;
   }

   public static HWDiskStore[] getDisks() {
      HashMap hashMap = new HashMap();

      for (String string : ExecutingCommand.runNative("mount")) {
         Matcher matcher2 = MOUNT_PATTERN.matcher(string);
         if (matcher2.matches()) {
            hashMap.put(matcher2.group(1), matcher2.group(2));
         }
      }

      HashMap hashMap2 = new HashMap();
      List list2 = Arrays.asList(ParseUtil.whitespaces.split(BsdSysctlUtil.sysctl("kern.disks", "")));
      ArrayList arrayList = new ArrayList();
      List list3 = ExecutingCommand.runNative("iostat -Ix");
      long l = System.currentTimeMillis();

      for (Object object : list3) {
         String string2 = (String)object;
         String[] strings = ParseUtil.whitespaces.split(string2);
         if (strings.length >= 7 && list2.contains(strings[0])) {
            HWDiskStore hwDiskStore = new HWDiskStore();
            hwDiskStore.setName(strings[0]);
            hwDiskStore.setReads((long)ParseUtil.parseDoubleOrDefault(strings[1], 0.0));
            hwDiskStore.setWrites((long)ParseUtil.parseDoubleOrDefault(strings[2], 0.0));
            hwDiskStore.setReadBytes((long)(ParseUtil.parseDoubleOrDefault(strings[3], 0.0) * 1024.0));
            hwDiskStore.setWriteBytes((long)(ParseUtil.parseDoubleOrDefault(strings[4], 0.0) * 1024.0));
            hwDiskStore.setCurrentQueueLength(ParseUtil.parseLongOrDefault(strings[5], 0L));
            hwDiskStore.setTransferTime((long)(ParseUtil.parseDoubleOrDefault(strings[6], 0.0) * 1000.0));
            hwDiskStore.setTimeStamp(l);
            hashMap2.put(strings[0], hwDiskStore);
         }
      }

      List list4 = ExecutingCommand.runNative("geom disk list");
      HWDiskStore hwDiskStore2 = null;

      for (Object object2 : list4) {
         String string3 = (String)object2;
         if (string3.startsWith("Geom name:")) {
            if (hwDiskStore2 != null) {
               setPartitions(hwDiskStore2, arrayList);
            }

            String string4 = string3.substring(string3.lastIndexOf(32) + 1);
            if (list2.contains(string4)) {
               hwDiskStore2 = (HWDiskStore)hashMap2.get(string4);
               if (hwDiskStore2 == null) {
                  hwDiskStore2 = new HWDiskStore();
                  hwDiskStore2.setName(string4);
               }
            }
         }

         if (hwDiskStore2 != null) {
            string3 = string3.trim();
            if (string3.startsWith("Mediasize:")) {
               String[] strings2 = ParseUtil.whitespaces.split(string3);
               if (strings2.length > 1) {
                  hwDiskStore2.setSize(ParseUtil.parseLongOrDefault(strings2[1], 0L));
               }
            }

            if (string3.startsWith("descr:")) {
               hwDiskStore2.setModel(string3.replace("descr:", "").trim());
            }

            if (string3.startsWith("ident:")) {
               hwDiskStore2.setSerial(string3.replace("ident:", "").replace("(null)", "").trim());
            }
         }
      }

      list4 = ExecutingCommand.runNative("geom part list");
      hwDiskStore2 = null;
      HWPartition hwPartition = null;

      for (Object object3 : list4) {
         String string5 = (String)object3;
         string5 = string5.trim();
         if (string5.startsWith("Geom name:")) {
            String string6 = string5.substring(string5.lastIndexOf(32) + 1);
            if (list2.contains(string6)) {
               hwDiskStore2 = (HWDiskStore)hashMap2.get(string6);
               if (hwDiskStore2 == null) {
                  hwDiskStore2 = new HWDiskStore();
                  hwDiskStore2.setName(string6);
               }
            }
         }

         if (hwDiskStore2 != null) {
            if (string5.contains("Name:")) {
               if (hwPartition != null) {
                  arrayList.add(hwPartition);
                  hwPartition = null;
               }

               String string7 = string5.substring(string5.lastIndexOf(32) + 1);
               if (string7.startsWith(hwDiskStore2.getName())) {
                  hwPartition = new HWPartition();
                  hwPartition.setIdentification(string7);
                  hwPartition.setName(string7);
                  hwPartition.setMountPoint((java.lang.String)(hashMap.getOrDefault(string7, "")));
               }
            }

            if (hwPartition != null) {
               String[] strings3 = ParseUtil.whitespaces.split(string5);
               if (strings3.length >= 2) {
                  if (string5.startsWith("Mediasize:")) {
                     hwPartition.setSize(ParseUtil.parseLongOrDefault(strings3[1], 0L));
                  } else if (string5.startsWith("rawuuid:")) {
                     hwPartition.setUuid(strings3[1]);
                  } else if (string5.startsWith("type:")) {
                     hwPartition.setType(strings3[1]);
                  }
               }
            }
         }
      }

      if (hwDiskStore2 != null) {
         setPartitions(hwDiskStore2, arrayList);
      }

      ArrayList arrayList2 = new ArrayList(hashMap2.keySet().size());
      arrayList2.addAll(hashMap2.values());
      Collections.sort(arrayList2);
      return (oshi.hardware.HWDiskStore[])(arrayList2.toArray(new HWDiskStore[0]));
   }

   private static void setPartitions(HWDiskStore store, List<HWPartition> partList) {
      HWPartition[] hwPartitions = new HWPartition[partList.size()];
      int j = 0;
      Collections.sort(partList);

      for (HWPartition hwPartition : partList) {
         hwPartition.setMinor(ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("stat -f %i /dev/" + hwPartition.getName()), 0));
         hwPartitions[j++] = hwPartition;
      }

      partList.clear();
      store.setPartitions(hwPartitions);
   }
}
