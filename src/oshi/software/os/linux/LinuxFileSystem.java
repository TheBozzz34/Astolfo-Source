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
   private static final long a = 12599219933159L;
   private static final long f = -3508644936851390458L;

   @Override
   public OSFileStore[] getFileStores(boolean var1) {
      HashMap var5 = new HashMap();
      File var6 = new File("/dev/disk/by-uuid");
      if (var6.listFiles() != null) {
         for (File var10 : var6.listFiles()) {
            try {
               var5.put(var10.getCanonicalPath(), var10.getName().toLowerCase());
            } catch (IOException var12) {
            }
         }
      }

      List var13 = getFileStoreMatching(null, var5, var1);
      return (oshi.software.os.OSFileStore[])(var13.toArray(new OSFileStore[0]));
   }

   private static List<OSFileStore> getFileStoreMatching(String var0, Map<String, String> var1) {
      return getFileStoreMatching(var0, var1, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String var0, Map<String, String> var1, boolean var2) {
      ArrayList var6 = new ArrayList();

      for (String var9 : FileUtil.readFile(ProcPath.MOUNTS)) {
         String[] var10 = var9.split(" ");
         if (var10.length >= (int)f) {
            String var11 = var10[1].replaceAll("\\\\040", " ");
            String var12 = var10[2];
            if ((!var2 || !NETWORK_FS_TYPES.contains(var12))
               && !PSEUDO_FS_TYPES.contains(var12)
               && !var11.equals("/dev")
               && !ParseUtil.filePathStartsWith(TMP_FS_PATHS, var11)
               && !var11.endsWith("/shm")) {
               String var13 = var10[3];
               String var14 = var10[0].replaceAll("\\\\040", " ");
               if (var11.equals("/")) {
                  var14 = "/";
               }

               if (var0 == null || var0.equals(var14)) {
                  String var15 = var10[0].replaceAll("\\\\040", " ");
                  String var16 = var1 != null ? var1.getOrDefault(var10[0], "") : "";
                  String var17;
                  if (var15.startsWith("/dev")) {
                     var17 = "Local Disk";
                  } else if (var15.equals("tmpfs")) {
                     var17 = "Ram Disk";
                  } else if (NETWORK_FS_TYPES.contains(var12)) {
                     var17 = "Network Disk";
                  } else {
                     var17 = "Mount Point";
                  }

                  String var18 = "";
                  String var19 = "/dev/mapper/";
                  Path var20 = Paths.get(var15);
                  if (var20.toFile().exists() && Files.isSymbolicLink(var20)) {
                     try {
                        Path var21 = Files.readSymbolicLink(var20);
                        Path var22 = Paths.get(var19 + var21.toString());
                        if (var22.toFile().exists()) {
                           var18 = var22.normalize().toString();
                        }
                     } catch (IOException var34) {
                     }
                  }

                  long var35 = 0L;
                  long var23 = 0L;
                  long var25 = 0L;
                  long var27 = 0L;
                  long var29 = 0L;

                  try {
                     Statvfs var31 = new Statvfs();
                     if (0 == LibC.INSTANCE.statvfs(var11, var31)) {
                        var35 = var31.f_files.longValue();
                        var23 = var31.f_ffree.longValue();
                        var25 = var31.f_blocks.longValue() * var31.f_bsize.longValue();
                        var27 = var31.f_bavail.longValue() * var31.f_bsize.longValue();
                        var29 = var31.f_bfree.longValue() * var31.f_bsize.longValue();
                     } else {
                        File var32 = new File(var11);
                        var25 = var32.getTotalSpace();
                        var27 = var32.getUsableSpace();
                        var29 = var32.getFreeSpace();
                     }
                  } catch (UnsatisfiedLinkError | NoClassDefFoundError var33) {
                  }

                  OSFileStore var36 = new OSFileStore();
                  var36.setName(var14);
                  var36.setVolume(var15);
                  var36.setLabel(var14);
                  var36.setMount(var11);
                  var36.setDescription(var17);
                  var36.setType(var12);
                  var36.setOptions(var13);
                  var36.setUUID(var16);
                  var36.setFreeSpace(var29);
                  var36.setUsableSpace(var27);
                  var36.setTotalSpace(var25);
                  var36.setFreeInodes(var23);
                  var36.setTotalInodes(var35);
                  var36.setLogicalVolume(var18);
                  var6.add(var36);
               }
            }
         }
      }

      return var6;
   }

   @Override
   public long getOpenFileDescriptors() {
      return this.getFileDescriptors(0);
   }

   @Override
   public long getMaxFileDescriptors() {
      return this.getFileDescriptors(2);
   }

   private long getFileDescriptors(int var1) {
      String var5 = ProcPath.SYS_FS_FILE_NR;
      if (var1 >= 0 && var1 <= 2) {
         List var6 = FileUtil.readFile(var5);
         if (!var6.isEmpty()) {
            String[] var7 = ((String)var6.get(0)).split("\\D+");
            return ParseUtil.parseLongOrDefault(var7[var1], 0L);
         } else {
            return 0L;
         }
      } else {
         throw new IllegalArgumentException("Index must be between 0 and 2.");
      }
   }

   public static boolean updateFileStoreStats(OSFileStore var0) {
      for (OSFileStore var5 : getFileStoreMatching(var0.getName(), null)) {
         if (var0.getVolume().equals(var5.getVolume()) && var0.getMount().equals(var5.getMount())) {
            var0.setLogicalVolume(var5.getLogicalVolume());
            var0.setDescription(var5.getDescription());
            var0.setType(var5.getType());
            var0.setFreeSpace(var5.getFreeSpace());
            var0.setUsableSpace(var5.getUsableSpace());
            var0.setTotalSpace(var5.getTotalSpace());
            var0.setFreeInodes(var5.getFreeInodes());
            var0.setTotalInodes(var5.getTotalInodes());
            return true;
         }
      }

      return false;
   }
}
