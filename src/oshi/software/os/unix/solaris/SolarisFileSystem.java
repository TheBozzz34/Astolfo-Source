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
   private static final long a = 81894969883400L;

   @Override
   public OSFileStore[] getFileStores(boolean var1) {
      List var2 = getFileStoreMatching(null, var1);
      return (oshi.software.os.OSFileStore[])(var2.toArray(new OSFileStore[0]));
   }

   private static List<OSFileStore> getFileStoreMatching(String var0) {
      return getFileStoreMatching(var0, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String var0, boolean var1) {
      ArrayList var5 = new ArrayList();
      HashMap var6 = new HashMap();
      HashMap var7 = new HashMap();
      String var8 = null;
      String var9 = null;
      String var10 = null;
      String var11 = "df -g" + (var1 ? " -l" : "");

      for (String var13 : ExecutingCommand.runNative(var11)) {
         if (var13.startsWith("/")) {
            var8 = ParseUtil.Q.split(var13)[0];
            var9 = null;
         } else if (var13.contains("available") && var13.contains("total files")) {
            var9 = ParseUtil.getTextBetweenStrings(var13, "available", "total files").trim();
         } else if (var13.contains("free files")) {
            var10 = ParseUtil.getTextBetweenStrings(var13, "", "free files").trim();
            if (var8 != null && var9 != null) {
               var6.put(var8, ParseUtil.parseLongOrDefault(var10, 0L));
               var7.put(var8, ParseUtil.parseLongOrDefault(var9, 0L));
               var8 = null;
            }
         }
      }

      for (String var31 : ExecutingCommand.runNative("cat /etc/mnttab")) {
         String[] var14 = ParseUtil.Q.split(var31);
         if (var14.length >= 5) {
            String var15 = var14[0];
            String var16 = var14[1];
            String var17 = var14[2];
            String var18 = var14[3];
            if ((!var1 || !NETWORK_FS_TYPES.contains(var17))
               && !PSEUDO_FS_TYPES.contains(var17)
               && !var16.equals("/dev")
               && !ParseUtil.filePathStartsWith(TMP_FS_PATHS, var16)
               && (!var15.startsWith("rpool") || var16.equals("/"))) {
               String var19 = var16.substring(var16.lastIndexOf(47) + 1);
               if (var19.isEmpty()) {
                  var19 = var15.substring(var15.lastIndexOf(47) + 1);
               }

               if (var0 == null || var0.equals(var19)) {
                  File var20 = new File(var16);
                  long var21 = var20.getTotalSpace();
                  long var23 = var20.getUsableSpace();
                  long var25 = var20.getFreeSpace();
                  String var27;
                  if (var15.startsWith("/dev") || var16.equals("/")) {
                     var27 = "Local Disk";
                  } else if (var15.equals("tmpfs")) {
                     var27 = "Ram Disk";
                  } else if (NETWORK_FS_TYPES.contains(var17)) {
                     var27 = "Network Disk";
                  } else {
                     var27 = "Mount Point";
                  }

                  OSFileStore var28 = new OSFileStore();
                  var28.setName(var19);
                  var28.setVolume(var15);
                  var28.setLabel(var19);
                  var28.setMount(var16);
                  var28.setDescription(var27);
                  var28.setType(var17);
                  var28.setOptions(var18);
                  var28.setUUID("");
                  var28.setFreeSpace(var25);
                  var28.setUsableSpace(var23);
                  var28.setTotalSpace(var21);
                  var28.setFreeInodes(var6.containsKey(var16) ? (Long)var6.get(var16) : 0L);
                  var28.setTotalInodes(var7.containsKey(var16) ? (Long)var7.get(var16) : 0L);
                  var5.add(var28);
               }
            }
         }
      }

      return var5;
   }

   @Override
   public long getOpenFileDescriptors() {
      try (KstatUtil.KstatChain var4 = KstatUtil.openChain()) {
         Kstat var6 = var4.lookup(null, -1, "file_cache");
         if (var6 != null && var4.read(var6)) {
            return KstatUtil.dataLookupLong(var6, "buf_inuse");
         }
      }

      return 0L;
   }

   @Override
   public long getMaxFileDescriptors() {
      try (KstatUtil.KstatChain var4 = KstatUtil.openChain()) {
         Kstat var6 = var4.lookup(null, -1, "file_cache");
         if (var6 != null && var4.read(var6)) {
            return KstatUtil.dataLookupLong(var6, "buf_max");
         }
      }

      return 0L;
   }

   public static boolean updateFileStoreStats(OSFileStore var0) {
      for (OSFileStore var5 : getFileStoreMatching(var0.getName())) {
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
