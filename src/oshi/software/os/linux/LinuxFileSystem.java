package oshi.software.os.linux;

import com.sun.jna.platform.linux.LibC;
import com.sun.jna.platform.linux.LibC.Statvfs;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public class LinuxFileSystem extends AbstractFileSystem {
   private static final List<String> TMP_FS_PATHS = Arrays.asList("/run", "/sys", "/proc", ProcPath.PROC);

   @Override
   public OSFileStore[] getFileStores(boolean localOnly) {
      HashMap hashMap = new HashMap();
      File file = new File("/dev/disk/by-uuid");
      if (file.listFiles() != null) {
         for (File file2 : file.listFiles()) {
            try {
               hashMap.put(file2.getCanonicalPath(), file2.getName().toLowerCase());
            } catch (IOException ioException) {
            }
         }
      }

      List list = getFileStoreMatching(null, hashMap, localOnly);
      return (oshi.software.os.OSFileStore[])(list.toArray(new OSFileStore[0]));
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap) {
      return getFileStoreMatching(nameToMatch, uuidMap, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap, boolean localOnly) {
      ArrayList arrayList = new ArrayList();

      for (String string : FileUtil.readFile(ProcPath.MOUNTS)) {
         String[] strings = string.split(" ");
         if (strings.length >= 6) {
            String string2 = strings[1].replaceAll("\\\\040", " ");
            String string3 = strings[2];
            if ((!localOnly || !NETWORK_FS_TYPES.contains(string3))
               && !PSEUDO_FS_TYPES.contains(string3)
               && !string2.equals("/dev")
               && !ParseUtil.filePathStartsWith(TMP_FS_PATHS, string2)
               && !string2.endsWith("/shm")) {
               String string4 = strings[3];
               String string5 = strings[0].replaceAll("\\\\040", " ");
               if (string2.equals("/")) {
                  string5 = "/";
               }

               if (nameToMatch == null || nameToMatch.equals(string5)) {
                  String string6 = strings[0].replaceAll("\\\\040", " ");
                  String string7 = uuidMap != null ? uuidMap.getOrDefault(strings[0], "") : "";
                  String string8;
                  if (string6.startsWith("/dev")) {
                     string8 = "Local Disk";
                  } else if (string6.equals("tmpfs")) {
                     string8 = "Ram Disk";
                  } else if (NETWORK_FS_TYPES.contains(string3)) {
                     string8 = "Network Disk";
                  } else {
                     string8 = "Mount Point";
                  }

                  String string9 = "";
                  String string10 = "/dev/mapper/";
                  Path path = Paths.get(string6);
                  if (path.toFile().exists() && Files.isSymbolicLink(path)) {
                     try {
                        Path path2 = Files.readSymbolicLink(path);
                        Path path3 = Paths.get(string10 + path2.toString());
                        if (path3.toFile().exists()) {
                           string9 = path3.normalize().toString();
                        }
                     } catch (IOException ioException) {
                     }
                  }

                  long l = 0L;
                  long m = 0L;
                  long n = 0L;
                  long l2 = 0L;
                  long l3 = 0L;

                  try {
                     Statvfs statvfs2 = new Statvfs();
                     if (0 == LibC.INSTANCE.statvfs(string2, statvfs2)) {
                        l = statvfs2.f_files.longValue();
                        m = statvfs2.f_ffree.longValue();
                        n = statvfs2.f_blocks.longValue() * statvfs2.f_bsize.longValue();
                        l2 = statvfs2.f_bavail.longValue() * statvfs2.f_bsize.longValue();
                        l3 = statvfs2.f_bfree.longValue() * statvfs2.f_bsize.longValue();
                     } else {
                        File file = new File(string2);
                        n = file.getTotalSpace();
                        l2 = file.getUsableSpace();
                        l3 = file.getFreeSpace();
                     }
                  } catch (UnsatisfiedLinkError | NoClassDefFoundError value) {
                  }

                  OSFileStore osFileStore = new OSFileStore();
                  osFileStore.setName(string5);
                  osFileStore.setVolume(string6);
                  osFileStore.setLabel(string5);
                  osFileStore.setMount(string2);
                  osFileStore.setDescription(string8);
                  osFileStore.setType(string3);
                  osFileStore.setOptions(string4);
                  osFileStore.setUUID(string7);
                  osFileStore.setFreeSpace(l3);
                  osFileStore.setUsableSpace(l2);
                  osFileStore.setTotalSpace(n);
                  osFileStore.setFreeInodes(m);
                  osFileStore.setTotalInodes(l);
                  osFileStore.setLogicalVolume(string9);
                  arrayList.add(osFileStore);
               }
            }
         }
      }

      return arrayList;
   }

   @Override
   public long getOpenFileDescriptors() {
      return this.getFileDescriptors(0);
   }

   @Override
   public long getMaxFileDescriptors() {
      return this.getFileDescriptors(2);
   }

   private long getFileDescriptors(int index) {
      String string = ProcPath.SYS_FS_FILE_NR;
      if (index >= 0 && index <= 2) {
         List list = FileUtil.readFile(string);
         if (!list.isEmpty()) {
            String[] strings = ((String)list.get(0)).split("\\D+");
            return ParseUtil.parseLongOrDefault(strings[index], 0L);
         } else {
            return 0L;
         }
      } else {
         throw new IllegalArgumentException("Index must be between 0 and 2.");
      }
   }

   public static boolean updateFileStoreStats(OSFileStore osFileStore) {
      for (OSFileStore osFileStore2 : getFileStoreMatching(osFileStore.getName(), null)) {
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
