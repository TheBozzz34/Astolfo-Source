package oshi.software.os.unix.freebsd;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdFileSystem extends AbstractFileSystem {
   private static final List<String> TMP_FS_PATHS = Arrays.asList("/system", "/tmp", "/dev/fd");

   @Override
   public OSFileStore[] getFileStores(boolean localOnly) {
      HashMap hashMap = new HashMap();
      String string = "";

      for (String string2 : ExecutingCommand.runNative("geom part list")) {
         if (string2.contains("Name: ")) {
            string = string2.substring(string2.lastIndexOf(32) + 1);
         }

         if (!string.isEmpty()) {
            string2 = string2.trim();
            if (string2.startsWith("rawuuid:")) {
               hashMap.put(string, string2.substring(string2.lastIndexOf(32) + 1));
               string = "";
            }
         }
      }

      ArrayList arrayList = new ArrayList();
      HashMap hashMap2 = new HashMap();
      HashMap hashMap3 = new HashMap();

      for (String string3 : ExecutingCommand.runNative("df -i")) {
         if (string3.startsWith("/")) {
            String[] strings = ParseUtil.whitespaces.split(string3);
            if (strings.length > 7) {
               hashMap2.put(strings[0], ParseUtil.parseLongOrDefault(strings[6], 0L));
               hashMap3.put(strings[0], (Long)hashMap2.get(strings[0]) + ParseUtil.parseLongOrDefault(strings[5], 0L));
            }
         }
      }

      for (String string4 : ExecutingCommand.runNative("mount -p")) {
         String[] strings2 = ParseUtil.whitespaces.split(string4);
         if (strings2.length >= 5) {
            String string5 = strings2[0];
            String string6 = strings2[1];
            String string7 = strings2[2];
            String string8 = strings2[3];
            if ((!localOnly || !NETWORK_FS_TYPES.contains(string7))
               && !PSEUDO_FS_TYPES.contains(string7)
               && !string6.equals("/dev")
               && !ParseUtil.filePathStartsWith(TMP_FS_PATHS, string6)
               && (!string5.startsWith("rpool") || string6.equals("/"))) {
               String string9 = string6.substring(string6.lastIndexOf(47) + 1);
               if (string9.isEmpty()) {
                  string9 = string5.substring(string5.lastIndexOf(47) + 1);
               }

               File file = new File(string6);
               long l = file.getTotalSpace();
               long m = file.getUsableSpace();
               long n = file.getFreeSpace();
               String string10;
               if (string5.startsWith("/dev") || string6.equals("/")) {
                  string10 = "Local Disk";
               } else if (string5.equals("tmpfs")) {
                  string10 = "Ram Disk";
               } else if (NETWORK_FS_TYPES.contains(string7)) {
                  string10 = "Network Disk";
               } else {
                  string10 = "Mount Point";
               }

               String string11 = (java.lang.String)(hashMap.getOrDefault(string9, ""));
               OSFileStore osFileStore = new OSFileStore();
               osFileStore.setName(string9);
               osFileStore.setVolume(string5);
               osFileStore.setLabel(string9);
               osFileStore.setMount(string6);
               osFileStore.setDescription(string10);
               osFileStore.setType(string7);
               osFileStore.setOptions(string8);
               osFileStore.setUUID(string11);
               osFileStore.setFreeSpace(n);
               osFileStore.setUsableSpace(m);
               osFileStore.setTotalSpace(l);
               osFileStore.setFreeInodes(hashMap2.containsKey(string6) ? (Long)hashMap2.get(string6) : 0L);
               osFileStore.setTotalInodes(hashMap3.containsKey(string6) ? (Long)hashMap3.get(string6) : 0L);
               arrayList.add(osFileStore);
            }
         }
      }

      return (oshi.software.os.OSFileStore[])(arrayList.toArray(new OSFileStore[0]));
   }

   @Override
   public long getOpenFileDescriptors() {
      return BsdSysctlUtil.sysctl("kern.openfiles", 0);
   }

   @Override
   public long getMaxFileDescriptors() {
      return BsdSysctlUtil.sysctl("kern.maxfiles", 0);
   }

   public static boolean updateFileStoreStats(OSFileStore osFileStore) {
      for (OSFileStore osFileStore2 : new FreeBsdFileSystem().getFileStores()) {
         if (osFileStore.getName().equals(osFileStore2.getName()) && osFileStore.getVolume().equals(osFileStore2.getVolume()) && osFileStore.getMount().equals(osFileStore2.getMount())
            )
          {
            osFileStore.setLogicalVolume(osFileStore2.getLogicalVolume());
            osFileStore.setDescription(osFileStore2.getDescription());
            osFileStore.setType(osFileStore2.getType());
            osFileStore.setUUID(osFileStore2.getUUID());
            osFileStore.setFreeSpace(osFileStore2.getFreeSpace());
            osFileStore.setUsableSpace(osFileStore2.getUsableSpace());
            osFileStore.setTotalSpace(osFileStore2.getTotalSpace());
            osFileStore.setFreeInodes(osFileStore2.getFreeInodes());
            osFileStore.setTotalInodes(osFileStore2.getTotalInodes());
            return true;
         }
      }

      return false;
   }
}
