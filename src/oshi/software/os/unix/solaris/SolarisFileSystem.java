package oshi.software.os.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
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
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public class SolarisFileSystem extends AbstractFileSystem {
   private static final List<String> TMP_FS_PATHS = Arrays.asList("/system", "/tmp", "/dev/fd");

   @Override
   public OSFileStore[] getFileStores(boolean localOnly) {
      List<OSFileStore> fsList = getFileStoreMatching(null, localOnly);
      return fsList.toArray(new OSFileStore[0]);
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
      return getFileStoreMatching(nameToMatch, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
      ArrayList arrayList = new ArrayList();
      HashMap hashMap = new HashMap();
      HashMap hashMap2 = new HashMap();
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = "df -g" + (localOnly ? " -l" : "");

      for (String string5 : ExecutingCommand.runNative(string4)) {
         if (string5.startsWith("/")) {
            string = ParseUtil.whitespaces.split(string5)[0];
            string2 = null;
         } else if (string5.contains("available") && string5.contains("total files")) {
            string2 = ParseUtil.getTextBetweenStrings(string5, "available", "total files").trim();
         } else if (string5.contains("free files")) {
            string3 = ParseUtil.getTextBetweenStrings(string5, "", "free files").trim();
            if (string != null && string2 != null) {
               hashMap.put(string, ParseUtil.parseLongOrDefault(string3, 0L));
               hashMap2.put(string, ParseUtil.parseLongOrDefault(string2, 0L));
               string = null;
            }
         }
      }

      for (String string6 : ExecutingCommand.runNative("cat /etc/mnttab")) {
         String[] strings = ParseUtil.whitespaces.split(string6);
         if (strings.length >= 5) {
            String string7 = strings[0];
            String string8 = strings[1];
            String string9 = strings[2];
            String string10 = strings[3];
            if ((!localOnly || !NETWORK_FS_TYPES.contains(string9))
               && !PSEUDO_FS_TYPES.contains(string9)
               && !string8.equals("/dev")
               && !ParseUtil.filePathStartsWith(TMP_FS_PATHS, string8)
               && (!string7.startsWith("rpool") || string8.equals("/"))) {
               String string11 = string8.substring(string8.lastIndexOf(47) + 1);
               if (string11.isEmpty()) {
                  string11 = string7.substring(string7.lastIndexOf(47) + 1);
               }

               if (nameToMatch == null || nameToMatch.equals(string11)) {
                  File file = new File(string8);
                  long m = file.getTotalSpace();
                  long n = file.getUsableSpace();
                  long l2 = file.getFreeSpace();
                  String string12;
                  if (string7.startsWith("/dev") || string8.equals("/")) {
                     string12 = "Local Disk";
                  } else if (string7.equals("tmpfs")) {
                     string12 = "Ram Disk";
                  } else if (NETWORK_FS_TYPES.contains(string9)) {
                     string12 = "Network Disk";
                  } else {
                     string12 = "Mount Point";
                  }

                  OSFileStore osFileStore = new OSFileStore();
                  osFileStore.setName(string11);
                  osFileStore.setVolume(string7);
                  osFileStore.setLabel(string11);
                  osFileStore.setMount(string8);
                  osFileStore.setDescription(string12);
                  osFileStore.setType(string9);
                  osFileStore.setOptions(string10);
                  osFileStore.setUUID("");
                  osFileStore.setFreeSpace(l2);
                  osFileStore.setUsableSpace(n);
                  osFileStore.setTotalSpace(m);
                  osFileStore.setFreeInodes(hashMap.containsKey(string8) ? (Long)hashMap.get(string8) : 0L);
                  osFileStore.setTotalInodes(hashMap2.containsKey(string8) ? (Long)hashMap2.get(string8) : 0L);
                  arrayList.add(osFileStore);
               }
            }
         }
      }

      return arrayList;
   }

   @Override
   public long getOpenFileDescriptors() {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup(null, -1, "file_cache");
         if (kstat != null && kstatChain.read(kstat)) {
            return KstatUtil.dataLookupLong(kstat, "buf_inuse");
         }
      }

      return 0L;
   }

   @Override
   public long getMaxFileDescriptors() {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup(null, -1, "file_cache");
         if (kstat != null && kstatChain.read(kstat)) {
            return KstatUtil.dataLookupLong(kstat, "buf_max");
         }
      }

      return 0L;
   }

   public static boolean updateFileStoreStats(OSFileStore osFileStore) {
      for (OSFileStore osFileStore2 : getFileStoreMatching(osFileStore.getName())) {
         if (osFileStore.getVolume().equals(osFileStore2.getVolume()) && osFileStore.getMount().equals(osFileStore2.getMount())) {
            osFileStore.setLogicalVolume(osFileStore2.getLogicalVolume());
            osFileStore.setDescription(osFileStore2.getDescription());
            osFileStore.setType(osFileStore2.getType());
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
