package oshi.software.os.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.DiskArbitration.DADiskRef;
import com.sun.jna.platform.mac.DiskArbitration.DASessionRef;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.SystemB.Statfs;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacFileSystem extends AbstractFileSystem {
   private static final Pattern LOCAL_DISK = Pattern.compile("/dev/disk\\d");
   private static final int n = 2;
   private static final int G = 4;
   private static final int Z = 8;
   private static final int i = 16;
   private static final int L = 32;
   private static final int p = 64;
   private static final int c = 128;
   private static final int X = 256;
   private static final int V = 1024;
   private static final int H = 4096;
   private static final int y = 8192;
   private static final int D = 16384;
   private static final int g = 32768;
   private static final int s = 1048576;
   private static final int E = 2097152;
   private static final int f = 4194304;
   private static final int b = 8388608;
   private static final int a = 16777216;
   private static final int Q = 33554432;
   private static final int d = 67108864;
   private static final int M = 268435456;
   private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
   private static final long j = 92491963963306L;

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
      int var6 = SystemB.INSTANCE.getfsstat64(null, 0, 0);
      if (var6 > 0) {
         DASessionRef var7 = DiskArbitration.INSTANCE.DASessionCreate(CoreFoundation.INSTANCE.CFAllocatorGetDefault());
         if (var7 != null) {
            CFStringRef var8 = CFStringRef.createCFString("DAVolumeName");
            Statfs[] var9 = new Statfs[var6];
            var6 = SystemB.INSTANCE.getfsstat64(var9, var6 * new Statfs().size(), 16);

            for (int var10 = 0; var10 < var6; var10++) {
               String var11 = new String(var9[var10].f_mntfromname, StandardCharsets.UTF_8).trim();
               int var12 = var9[var10].f_flags;
               if ((!var1 || (var12 & 4096) != 0) && !var11.equals("devfs") && !var11.startsWith("map ")) {
                  String var13 = new String(var9[var10].f_fstypename, StandardCharsets.UTF_8).trim();
                  String var14 = "Volume";
                  if (LOCAL_DISK.matcher(var11).matches()) {
                     var14 = "Local Disk";
                  } else if (var11.startsWith("localhost:") || var11.startsWith("//") || var11.startsWith("smb://") || NETWORK_FS_TYPES.contains(var13)) {
                     var14 = "Network Drive";
                  }

                  String var15 = new String(var9[var10].f_mntonname, StandardCharsets.UTF_8).trim();
                  String var16 = "";
                  File var17 = new File(var15);
                  if (var16.isEmpty()) {
                     var16 = var17.getName();
                     if (var16.isEmpty()) {
                        var16 = var17.getPath();
                     }
                  }

                  if (var0 == null || var0.equals(var16)) {
                     StringBuilder var18 = new StringBuilder((1 & var12) == 0 ? "rw" : "ro");
                     String var19 = OPTIONS_MAP.entrySet()
                        .stream()
                        .filter(var1x -> (var1x.getKey() & var12) > 0)
                        .map(Entry::getValue)
                        .collect(Collectors.joining(","));
                     if (!var19.isEmpty()) {
                        var18.append(',').append(var19);
                     }

                     String var20 = "";
                     String var21 = var11.replace("/dev/disk", "disk");
                     if (var21.startsWith("disk")) {
                        DADiskRef var22 = DiskArbitration.INSTANCE.DADiskCreateFromBSDName(CoreFoundation.INSTANCE.CFAllocatorGetDefault(), var7, var11);
                        if (var22 != null) {
                           CFDictionaryRef var23 = DiskArbitration.INSTANCE.DADiskCopyDescription(var22);
                           if (var23 != null) {
                              Pointer var24 = var23.getValue(var8);
                              CFStringRef var25 = new CFStringRef(var24);
                              var16 = var25.stringValue();
                              if (var16 == null) {
                                 var16 = "unknown";
                              }

                              var23.release();
                           }

                           var22.release();
                        }

                        CFMutableDictionaryRef var28 = IOKitUtil.getBSDNameMatchingDict(var21);
                        if (var28 != null) {
                           IOIterator var29 = IOKitUtil.getMatchingServices(var28);
                           if (var29 != null) {
                              IORegistryEntry var30 = var29.next();
                              if (var30 != null && var30.conformsTo("IOMedia")) {
                                 var20 = var30.getStringProperty("UUID");
                                 if (var20 != null) {
                                    var20 = var20.toLowerCase();
                                 }

                                 var30.release();
                              }

                              var29.release();
                           }
                        }
                     }

                     OSFileStore var27 = new OSFileStore();
                     var27.setName(var16);
                     var27.setVolume(var11);
                     var27.setLabel(var16);
                     var27.setMount(var15);
                     var27.setDescription(var14);
                     var27.setType(var13);
                     var27.setOptions(var18.toString());
                     var27.setUUID(var20 == null ? "" : var20);
                     var27.setFreeSpace(var17.getFreeSpace());
                     var27.setUsableSpace(var17.getUsableSpace());
                     var27.setTotalSpace(var17.getTotalSpace());
                     var27.setFreeInodes(var9[var10].f_ffree);
                     var27.setTotalInodes(var9[var10].f_files);
                     var5.add(var27);
                  }
               }
            }

            var8.release();
            var7.release();
         }
      }

      return var5;
   }

   @Override
   public long getOpenFileDescriptors() {
      return SysctlUtil.sysctl("kern.num_files", 0);
   }

   @Override
   public long getMaxFileDescriptors() {
      return SysctlUtil.sysctl("kern.maxfiles", 0);
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

   static {
      long var20 = j ^ 8107563618164L;
      long var10000 = var20 ^ 6773660303754022199L;
      var10000 = var20 ^ 6862932709373756311L;
      var10000 = var20 ^ 5712720609604324619L;
      var10000 = var20 ^ 2001626407066233910L;
      var10000 = var20 ^ 9099338308994518738L;
      var10000 = var20 ^ 31139249713393155L;
      var10000 = var20 ^ 5986434935583962528L;
      var10000 = var20 ^ 3587284759962264400L;
      var10000 = var20 ^ 3880283592438777608L;
      var10000 = var20 ^ 7351119441171523468L;
      var10000 = var20 ^ 7688607387556712199L;
      var10000 = var20 ^ 7045822073283355856L;
      var10000 = var20 ^ 4334293339206271154L;
      var10000 = var20 ^ 5857050442163654540L;
      var10000 = var20 ^ 910732893986040306L;
      var10000 = var20 ^ 8381276703735974282L;
      var10000 = var20 ^ 6009506647858760630L;
      var10000 = var20 ^ 1188318127022887495L;
      var10000 = var20 ^ 2762389838947541368L;
      var10000 = var20 ^ 6155800240456056066L;
      var10000 = var20 ^ 6583895418908265928L;
      OPTIONS_MAP.put(2, "synchronous");
      OPTIONS_MAP.put(4, "noexec");
      OPTIONS_MAP.put(8, "nosuid");
      OPTIONS_MAP.put(16, "nodev");
      OPTIONS_MAP.put(32, "union");
      OPTIONS_MAP.put(64, "asynchronous");
      OPTIONS_MAP.put(128, "content-protection");
      OPTIONS_MAP.put(256, "exported");
      OPTIONS_MAP.put(1024, "quarantined");
      OPTIONS_MAP.put(4096, "local");
      OPTIONS_MAP.put(8192, "quotas");
      OPTIONS_MAP.put(16384, "rootfs");
      OPTIONS_MAP.put(32768, "volfs");
      OPTIONS_MAP.put(1048576, "nobrowse");
      OPTIONS_MAP.put(2097152, "noowners");
      OPTIONS_MAP.put(4194304, "automounted");
      OPTIONS_MAP.put(8388608, "journaled");
      OPTIONS_MAP.put(16777216, "nouserxattr");
      OPTIONS_MAP.put(33554432, "defwrite");
      OPTIONS_MAP.put(67108864, "multilabel");
      OPTIONS_MAP.put(268435456, "noatime");
   }
}
