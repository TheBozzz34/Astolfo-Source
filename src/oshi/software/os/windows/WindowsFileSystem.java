package oshi.software.os.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.driver.windows.wmi.Win32LogicalDisk;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public class WindowsFileSystem extends AbstractFileSystem {
   private static final int S = 255;
   private static final int T = 2;
   private static final int V = 16;
   private static final int z = 536870912;
   private static final int M = 262144;
   private static final int R = 8;
   private static final int I = 524288;
   private static final int d = 1048576;
   private static final int q = 131072;
   private static final int n = 65536;
   private static final int k = 128;
   private static final int y = 64;
   private static final int t = 2097152;
   private static final int u = 33554432;
   private static final int j = 4;
   private static final int c = 32768;
   private static final int Q = 32;
   private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
   private static final long MAX_WINDOWS_HANDLES;
   private static final long b = 91749895903559L;

   public WindowsFileSystem() {
      Kernel32.INSTANCE.SetErrorMode(1);
   }

   @Override
   public OSFileStore[] getFileStores(boolean var1) {
      ArrayList var5 = getLocalVolumes(null);
      HashMap var6 = new HashMap();

      for (Object var8$raw : var5) {
         OSFileStore var8 = (OSFileStore)var8$raw;
         var6.put(var8.getMount(), var8);
      }

      for (OSFileStore var11 : getWmiVolumes(null, var1)) {
         if (var6.containsKey(var11.getMount())) {
            OSFileStore var9 = (OSFileStore)var6.get(var11.getMount());
            var9.setName(var11.getName());
            if (var9.getLabel().isEmpty()) {
               var9.setLabel(var11.getLabel());
            }
         } else if (!var1) {
            var5.add(var11);
         }
      }

      return (oshi.software.os.OSFileStore[])(var5.toArray(new OSFileStore[0]));
   }

   private static ArrayList<OSFileStore> getLocalVolumes(String var0) {
      ArrayList var4 = new ArrayList();
      char[] var14 = new char[255];
      HANDLE var9 = Kernel32.INSTANCE.FindFirstVolume(var14, 255);
      if (var9 == WinBase.INVALID_HANDLE_VALUE) {
         return var4;
      }

      boolean var13;
      do {
         char[] var15 = new char[16];
         char[] var16 = new char[255];
         char[] var17 = new char[255];
         IntByReference var18 = new IntByReference();
         LARGE_INTEGER var10 = new LARGE_INTEGER(0L);
         LARGE_INTEGER var11 = new LARGE_INTEGER(0L);
         LARGE_INTEGER var12 = new LARGE_INTEGER(0L);
         String var5 = new String(var14).trim();
         Kernel32.INSTANCE.GetVolumeInformation(var5, var16, 255, null, null, var18, var15, 16);
         int var19 = var18.getValue();
         Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(var5, var17, 255, null);
         String var8 = new String(var17).trim();
         if (!var8.isEmpty() && (var0 == null || var0.equals(var5))) {
            String var7 = new String(var16).trim();
            String var6 = new String(var15).trim();
            StringBuilder var20 = new StringBuilder((524288 & var19) == 0 ? "rw" : "ro");
            String var21 = OPTIONS_MAP.entrySet().stream().filter(var1 -> (var1.getKey() & var19) > 0).map(Entry::getValue).collect(Collectors.joining(","));
            if (!var21.isEmpty()) {
               var20.append(',').append(var21);
            }

            Kernel32.INSTANCE.GetDiskFreeSpaceEx(var5, var10, var11, var12);
            String var22 = ParseUtil.parseUuidOrDefault(var5, "");
            OSFileStore var23 = new OSFileStore();
            var23.setName(String.format("%s (%s)", var7, var8));
            var23.setVolume(var5);
            var23.setLabel(var7);
            var23.setMount(var8);
            var23.setDescription(getDriveType(var8));
            var23.setType(var6);
            var23.setOptions(var20.toString());
            var23.setUUID(var22);
            var23.setFreeSpace(var12.getValue());
            var23.setUsableSpace(var10.getValue());
            var23.setTotalSpace(var11.getValue());
            var4.add(var23);
         }

         var13 = Kernel32.INSTANCE.FindNextVolume(var9, var14, 255);
      } while (var13);

      Kernel32.INSTANCE.FindVolumeClose(var9);
      return var4;
   }

   private static List<OSFileStore> getWmiVolumes(String var0, boolean var1) {
      ArrayList var9 = new ArrayList();
      WmiResult var10 = Win32LogicalDisk.queryLogicalDisk(var0, var1);

      for (int var11 = 0; var11 < var10.getResultCount(); var11++) {
         long var5 = WmiUtil.getUint64(var10, Win32LogicalDisk.LogicalDiskProperty.FREESPACE, var11);
         long var7 = WmiUtil.getUint64(var10, Win32LogicalDisk.LogicalDiskProperty.SIZE, var11);
         String var12 = WmiUtil.getString(var10, Win32LogicalDisk.LogicalDiskProperty.DESCRIPTION, var11);
         String var13 = WmiUtil.getString(var10, Win32LogicalDisk.LogicalDiskProperty.NAME, var11);
         String var14 = WmiUtil.getString(var10, Win32LogicalDisk.LogicalDiskProperty.VOLUMENAME, var11);
         int var15 = WmiUtil.getUint32(var10, Win32LogicalDisk.LogicalDiskProperty.DRIVETYPE, var11);
         String var16;
         if (var15 != 4) {
            char[] var17 = new char[255];
            Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(var13 + "\\", var17, 255);
            var16 = new String(var17).trim();
         } else {
            var16 = WmiUtil.getString(var10, Win32LogicalDisk.LogicalDiskProperty.PROVIDERNAME, var11);
            String[] var18 = var16.split("\\\\");
            if (var18.length > 1 && var18[var18.length - 1].length() > 0) {
               var12 = var18[var18.length - 1];
            }
         }

         OSFileStore var19 = new OSFileStore();
         var19.setName(String.format("%s (%s)", var12, var13));
         var19.setVolume(var16);
         var19.setLabel(var14);
         var19.setMount(var13 + "\\");
         var19.setDescription(getDriveType(var13));
         var19.setType(WmiUtil.getString(var10, Win32LogicalDisk.LogicalDiskProperty.FILESYSTEM, var11));
         var19.setUUID("");
         var19.setFreeSpace(var5);
         var19.setUsableSpace(var5);
         var19.setTotalSpace(var7);
         var9.add(var19);
      }

      return var9;
   }

   private static String getDriveType(String var0) {
      switch (Kernel32.INSTANCE.GetDriveType(var0)) {
         case 2:
            return "Removable drive";
         case 3:
            return "Fixed drive";
         case 4:
            return "Network drive";
         case 5:
            return "CD-ROM";
         case 6:
            return "RAM drive";
         default:
            return "Unknown drive type";
      }
   }

   @Override
   public long getOpenFileDescriptors() {
      Map var4 = ProcessInformation.queryHandles();
      List var5 = (List)var4.get(ProcessInformation.HandleCountProperty.HANDLECOUNT);
      long var6 = 0L;
      if (var5 != null) {
         for (int var8 = 0; var8 < var5.size(); var8++) {
            var6 += ((java.lang.Long)(var5.get(var8)));
         }
      }

      return var6;
   }

   @Override
   public long getMaxFileDescriptors() {
      return MAX_WINDOWS_HANDLES;
   }

   public static boolean updateFileStoreStats(OSFileStore var0) {
      List var4 = getLocalVolumes(var0.getVolume());
      if (var4.isEmpty()) {
         String var5 = var0.getMount().length() < 2 ? null : var0.getMount().substring(0, 2);
         var4 = getWmiVolumes(var5, false);
      }

      for (Object var6$raw : var4) {
         OSFileStore var6 = (OSFileStore)var6$raw;
         if (var0.getVolume().equals(var6.getVolume()) && var0.getMount().equals(var6.getMount())) {
            var0.setLogicalVolume(var6.getLogicalVolume());
            var0.setDescription(var6.getDescription());
            var0.setType(var6.getType());
            var0.setFreeSpace(var6.getFreeSpace());
            var0.setUsableSpace(var6.getUsableSpace());
            var0.setTotalSpace(var6.getTotalSpace());
            var0.setFreeInodes(var6.getFreeInodes());
            var0.setTotalInodes(var6.getTotalInodes());
            return true;
         }
      }

      return false;
   }

   static {
      long var20 = b ^ 2900452754953L;
      long var10000 = var20 ^ 9168396066803755451L;
      var10000 = var20 ^ 6405264537652694144L;
      var10000 = var20 ^ 3760757584754506692L;
      var10000 = var20 ^ 8314321614909431806L;
      var10000 = var20 ^ 5835176252407130963L;
      var10000 = var20 ^ 1257380778018589253L;
      var10000 = var20 ^ 2448566906961024649L;
      var10000 = var20 ^ 9043345678576879836L;
      var10000 = var20 ^ 5171864055468318174L;
      var10000 = var20 ^ 1181162977045451280L;
      var10000 = var20 ^ 6676908124552546362L;
      var10000 = var20 ^ 4100300639768648441L;
      var10000 = var20 ^ 5340556208263051333L;
      var10000 = var20 ^ 6269081358065699513L;
      var10000 = var20 ^ 875527033389639534L;
      var10000 = var20 ^ 3776995034674912706L;
      var10000 = var20 ^ 8921696893919589040L;
      OPTIONS_MAP.put(2, "casepn");
      OPTIONS_MAP.put(1, "casess");
      OPTIONS_MAP.put(16, "fcomp");
      OPTIONS_MAP.put(536870912, "dax");
      OPTIONS_MAP.put(262144, "streams");
      OPTIONS_MAP.put(8, "acls");
      OPTIONS_MAP.put(1048576, "wronce");
      OPTIONS_MAP.put(131072, "efs");
      OPTIONS_MAP.put(65536, "oids");
      OPTIONS_MAP.put(128, "reparse");
      OPTIONS_MAP.put(64, "sparse");
      OPTIONS_MAP.put(2097152, "trans");
      OPTIONS_MAP.put(33554432, "journaled");
      OPTIONS_MAP.put(4, "unicode");
      OPTIONS_MAP.put(32768, "vcomp");
      OPTIONS_MAP.put(32, "quota");
      if (System.getenv("ProgramFiles(x86)") == null) {
         MAX_WINDOWS_HANDLES = 16744448L;
      } else {
         MAX_WINDOWS_HANDLES = 16711680L;
      }
   }
}
