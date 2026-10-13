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
   private static final long a = 81837555799709L;

   @Override
   public OSFileStore[] getFileStores(boolean var1) {
      long var2 = a ^ 71641844292242L;
      HashMap var6 = new HashMap();
      String var7 = "";

      for (String var9 : ExecutingCommand.runNative("geom part list")) {
         if (var9.contains("Name: ")) {
            var7 = var9.substring(var9.lastIndexOf(32) + 1);
         }

         if (!var7.isEmpty()) {
            var9 = var9.trim();
            if (var9.startsWith("rawuuid:")) {
               var6.put(var7, var9.substring(var9.lastIndexOf(32) + 1));
               var7 = "";
            }
         }
      }

      ArrayList var29 = new ArrayList();
      HashMap var31 = new HashMap();
      long var10000 = 6849323925694564453L ^ var2;
      HashMap var10 = new HashMap();

      for (String var12 : ExecutingCommand.runNative("df -i")) {
         if (var12.startsWith("/")) {
            String[] var13 = ParseUtil.Q.split(var12);
            if (var13.length > 7) {
               var31.put(var13[0], ParseUtil.parseLongOrDefault(var13[6], 0L));
               var10.put(var13[0], (Long)var31.get(var13[0]) + ParseUtil.parseLongOrDefault(var13[5], 0L));
            }
         }
      }

      for (String var33 : ExecutingCommand.runNative("mount -p")) {
         String[] var34 = ParseUtil.Q.split(var33);
         if (var34.length >= 5) {
            String var14 = var34[0];
            String var15 = var34[1];
            String var16 = var34[2];
            String var17 = var34[3];
            if ((!var1 || !NETWORK_FS_TYPES.contains(var16))
               && !PSEUDO_FS_TYPES.contains(var16)
               && !var15.equals("/dev")
               && !ParseUtil.filePathStartsWith(TMP_FS_PATHS, var15)
               && (!var14.startsWith("rpool") || var15.equals("/"))) {
               String var18 = var15.substring(var15.lastIndexOf(47) + 1);
               if (var18.isEmpty()) {
                  var18 = var14.substring(var14.lastIndexOf(47) + 1);
               }

               File var19 = new File(var15);
               long var20 = var19.getTotalSpace();
               long var22 = var19.getUsableSpace();
               long var24 = var19.getFreeSpace();
               String var26;
               if (var14.startsWith("/dev") || var15.equals("/")) {
                  var26 = "Local Disk";
               } else if (var14.equals("tmpfs")) {
                  var26 = "Ram Disk";
               } else if (NETWORK_FS_TYPES.contains(var16)) {
                  var26 = "Network Disk";
               } else {
                  var26 = "Mount Point";
               }

               String var27 = (java.lang.String)(var6.getOrDefault(var18, ""));
               OSFileStore var28 = new OSFileStore();
               var28.setName(var18);
               var28.setVolume(var14);
               var28.setLabel(var18);
               var28.setMount(var15);
               var28.setDescription(var26);
               var28.setType(var16);
               var28.setOptions(var17);
               var28.setUUID(var27);
               var28.setFreeSpace(var24);
               var28.setUsableSpace(var22);
               var28.setTotalSpace(var20);
               var28.setFreeInodes(var31.containsKey(var15) ? (Long)var31.get(var15) : 0L);
               var28.setTotalInodes(var10.containsKey(var15) ? (Long)var10.get(var15) : 0L);
               var29.add(var28);
            }
         }
      }

      return (oshi.software.os.OSFileStore[])(var29.toArray(new OSFileStore[0]));
   }

   @Override
   public long getOpenFileDescriptors() {
      return BsdSysctlUtil.sysctl("kern.openfiles", 0);
   }

   @Override
   public long getMaxFileDescriptors() {
      return BsdSysctlUtil.sysctl("kern.maxfiles", 0);
   }

   public static boolean updateFileStoreStats(OSFileStore var0) {
      for (OSFileStore var7 : new FreeBsdFileSystem().getFileStores()) {
         if (var0.getName().equals(var7.getName()) && var0.getVolume().equals(var7.getVolume()) && var0.getMount().equals(var7.getMount())) {
            var0.setLogicalVolume(var7.getLogicalVolume());
            var0.setDescription(var7.getDescription());
            var0.setType(var7.getType());
            var0.setUUID(var7.getUUID());
            var0.setFreeSpace(var7.getFreeSpace());
            var0.setUsableSpace(var7.getUsableSpace());
            var0.setTotalSpace(var7.getTotalSpace());
            var0.setFreeInodes(var7.getFreeInodes());
            var0.setTotalInodes(var7.getTotalInodes());
            return true;
         }
      }

      return false;
   }
}
