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
   private SolarisDisks() {
   }

   public static boolean updateDiskStats(HWDiskStore diskStore) {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup(null, 0, diskStore.getName());
         if (kstat != null && kstatChain.read(kstat)) {
            KstatIO kstatIO = new KstatIO(kstat.ks_data);
            diskStore.setReads(kstatIO.reads);
            diskStore.setWrites(kstatIO.writes);
            diskStore.setReadBytes(kstatIO.nread);
            diskStore.setWriteBytes(kstatIO.nwritten);
            diskStore.setCurrentQueueLength((long)kstatIO.wcnt + kstatIO.rcnt);
            diskStore.setTransferTime(kstatIO.rtime / 1000000L);
            diskStore.setTimeStamp(kstat.ks_snaptime / 1000000L);
            return true;
         }
      }

      return false;
   }

   public static HWDiskStore[] getDisks() {
      HashMap hashMap = new HashMap();
      List list = ExecutingCommand.runNative("iostat -er");
      HashMap hashMap2 = new HashMap();
      List list2 = ExecutingCommand.runNative("iostat -ern");

      for (int i = 0; i < list.size() && i < list2.size(); i++) {
         String string = (String)list.get(i);
         String[] strings = string.split(",");
         if (strings.length >= 5 && !"device".equals(strings[0])) {
            HWDiskStore hwDiskStore = new HWDiskStore();
            hwDiskStore.setName(strings[0]);
            hashMap.put(strings[0], hwDiskStore);
            String string2 = (String)list2.get(i);
            String[] strings2 = string2.split(",");
            if (strings2.length >= 5 && !"device".equals(strings2[4])) {
               hashMap2.put(strings[0], strings2[4]);
            }
         }
      }

      HashMap hashMap3 = new HashMap();
      List list3 = ExecutingCommand.runNative("lshal");
      String string3 = "";

      for (Object object : list3) {
         String string4 = (String)object;
         if (string4.startsWith("udi ")) {
            String string5 = ParseUtil.getSingleQuoteStringValue(string4);
            string3 = string5.substring(string5.lastIndexOf(47) + 1);
         } else {
            string4 = string4.trim();
            if (string4.startsWith("block.major")) {
               hashMap3.put(string3, ParseUtil.getFirstIntValue(string4));
            }
         }
      }

      list = ExecutingCommand.runNative("iostat -Er");
      string3 = "";
      String string6 = "";
      String string7 = "";
      String string8 = "";
      String string9 = "";
      long l = 0L;

      for (Object object2 : list) {
         String string10 = (String)object2;
         String[] strings3 = string10.split(",");

         for (String string11 : strings3) {
            string11 = string11.trim();
            if (hashMap.keySet().contains(string11)) {
               if (!string3.isEmpty()) {
                  updateStore((HWDiskStore)hashMap.get(string3), string6, string7, string8, string9, l, (String)hashMap2.get(string3), (int)(hashMap3.getOrDefault(string3, 0)));
               }

               string3 = string11;
               string6 = "";
               string7 = "";
               string8 = "";
               string9 = "";
               l = 0L;
            } else if (string11.startsWith("Model:")) {
               string6 = string11.replace("Model:", "").trim();
            } else if (string11.startsWith("Serial No:")) {
               string9 = string11.replace("Serial No:", "").trim();
            } else if (string11.startsWith("Vendor:")) {
               string7 = string11.replace("Vendor:", "").trim();
            } else if (string11.startsWith("Product:")) {
               string8 = string11.replace("Product:", "").trim();
            } else if (string11.startsWith("Size:")) {
               String[] strings4 = string11.split("<");
               if (strings4.length > 1) {
                  strings4 = ParseUtil.whitespaces.split(strings4[1]);
                  l = ParseUtil.parseLongOrDefault(strings4[0], 0L);
               }
            }
         }

         if (!string3.isEmpty()) {
            updateStore((HWDiskStore)hashMap.get(string3), string6, string7, string8, string9, l, (String)hashMap2.get(string3), (int)(hashMap3.getOrDefault(string3, 0)));
         }
      }

      HWDiskStore[] hwDiskStores = new HWDiskStore[hashMap.keySet().size()];
      int j = 0;

      for (Object object3 : hashMap.entrySet()) {
         Entry entry = (Entry)object3;
         updateDiskStats((HWDiskStore)entry.getValue());
         hwDiskStores[j++] = (HWDiskStore)entry.getValue();
      }

      return hwDiskStores;
   }

   private static void updateStore(HWDiskStore store, String model, String vendor, String product, String serial, long size, String mount, int major) {
      store.setModel(model.isEmpty() ? (vendor + " " + product).trim() : model);
      store.setSerial(serial);
      store.setSize(size);
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("prtvtoc /dev/dsk/" + mount);
      if (list.size() > 1) {
         int i = 0;

         for (Object object : list) {
            String string = (String)object;
            if (string.startsWith("*")) {
               if (string.endsWith("bytes/sector")) {
                  String[] strings = ParseUtil.whitespaces.split(string);
                  if (strings.length > 0) {
                     i = ParseUtil.parseIntOrDefault(strings[1], 0);
                  }
               }
            } else if (i > 0) {
               String[] strings2 = ParseUtil.whitespaces.split(string.trim());
               if (strings2.length >= 6 && !"2".equals(strings2[0])) {
                  HWPartition hwPartition = new HWPartition();
                  hwPartition.setIdentification(mount + "s" + strings2[0]);
                  hwPartition.setMajor(major);
                  hwPartition.setMinor(ParseUtil.parseIntOrDefault(strings2[0], 0));
                  switch (ParseUtil.parseIntOrDefault(strings2[1], 0)) {
                     case 1:
                     case 24:
                        hwPartition.setName("boot");
                        break;
                     case 2:
                        hwPartition.setName("root");
                        break;
                     case 3:
                        hwPartition.setName("swap");
                        break;
                     case 4:
                        hwPartition.setName("usr");
                        break;
                     case 5:
                        hwPartition.setName("backup");
                        break;
                     case 6:
                        hwPartition.setName("stand");
                        break;
                     case 7:
                        hwPartition.setName("var");
                        break;
                     case 8:
                        hwPartition.setName("home");
                        break;
                     case 9:
                        hwPartition.setName("altsctr");
                        break;
                     case 10:
                        hwPartition.setName("cache");
                        break;
                     case 11:
                        hwPartition.setName("reserved");
                        break;
                     case 12:
                        hwPartition.setName("system");
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
                        hwPartition.setName("unknown");
                        break;
                     case 14:
                        hwPartition.setName("public region");
                        break;
                     case 15:
                        hwPartition.setName("private region");
                  }

                  switch (strings2[2]) {
                     case "00":
                        hwPartition.setType("wm");
                        break;
                     case "10":
                        hwPartition.setType("rm");
                        break;
                     case "01":
                        hwPartition.setType("wu");
                        break;
                     default:
                        hwPartition.setType("ru");
                  }

                  hwPartition.setSize(i * ParseUtil.parseLongOrDefault(strings2[4], 0L));
                  if (strings2.length > 6) {
                     hwPartition.setMountPoint(strings2[6]);
                  }

                  arrayList.add(hwPartition);
               }
            }
         }

         store.setPartitions((oshi.hardware.HWPartition[])(arrayList.toArray(new HWPartition[0])));
      }
   }
}
