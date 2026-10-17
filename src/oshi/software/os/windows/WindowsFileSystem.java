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
   private static final int BUFSIZE = 255;
   private static final int FILE_CASE_PRESERVED_NAMES = 2;
   private static final int FILE_FILE_COMPRESSION = 16;
   private static final int z = 536870912;
   private static final int FILE_NAMED_STREAMS = 262144;
   private static final int FILE_PERSISTENT_ACLS = 8;
   private static final int FILE_READ_ONLY_VOLUME = 524288;
   private static final int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
   private static final int FILE_SUPPORTS_ENCRYPTION = 131072;
   private static final int FILE_SUPPORTS_OBJECT_IDS = 65536;
   private static final int FILE_SUPPORTS_REPARSE_POINTS = 128;
   private static final int y = 64;
   private static final int FILE_SUPPORTS_TRANSACTIONS = 2097152;
   private static final int FILE_SUPPORTS_USN_JOURNAL = 33554432;
   private static final int FILE_UNICODE_ON_DISK = 4;
   private static final int FILE_VOLUME_IS_COMPRESSED = 32768;
   private static final int FILE_VOLUME_QUOTAS = 32;
   private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
   private static final long MAX_WINDOWS_HANDLES;

   public WindowsFileSystem() {
      Kernel32.INSTANCE.SetErrorMode(1);
   }

   @Override
   public OSFileStore[] getFileStores(boolean localOnly) {
      ArrayList arrayList = getLocalVolumes(null);
      HashMap hashMap = new HashMap();

      for (Object object : arrayList) {
         OSFileStore osFileStore = (OSFileStore)object;
         hashMap.put(osFileStore.getMount(), osFileStore);
      }

      for (OSFileStore osFileStore2 : getWmiVolumes(null, localOnly)) {
         if (hashMap.containsKey(osFileStore2.getMount())) {
            OSFileStore osFileStore3 = (OSFileStore)hashMap.get(osFileStore2.getMount());
            osFileStore3.setName(osFileStore2.getName());
            if (osFileStore3.getLabel().isEmpty()) {
               osFileStore3.setLabel(osFileStore2.getLabel());
            }
         } else if (!localOnly) {
            arrayList.add(osFileStore2);
         }
      }

      return (oshi.software.os.OSFileStore[])(arrayList.toArray(new OSFileStore[0]));
   }

   private static ArrayList<OSFileStore> getLocalVolumes(String volumeToMatch) {
      ArrayList arrayList = new ArrayList();
      char[] chars = new char[255];
      HANDLE handle = Kernel32.INSTANCE.FindFirstVolume(chars, 255);
      if (handle == WinBase.INVALID_HANDLE_VALUE) {
         return arrayList;
      }

      boolean bl;
      do {
         char[] chars2 = new char[16];
         char[] chars3 = new char[255];
         char[] chars4 = new char[255];
         IntByReference intByReference = new IntByReference();
         LARGE_INTEGER largE_INTEGER = new LARGE_INTEGER(0L);
         LARGE_INTEGER largE_INTEGER2 = new LARGE_INTEGER(0L);
         LARGE_INTEGER largE_INTEGER3 = new LARGE_INTEGER(0L);
         String string = new String(chars).trim();
         Kernel32.INSTANCE.GetVolumeInformation(string, chars3, 255, null, null, intByReference, chars2, 16);
         int i = intByReference.getValue();
         Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(string, chars4, 255, null);
         String string2 = new String(chars4).trim();
         if (!string2.isEmpty() && (volumeToMatch == null || volumeToMatch.equals(string))) {
            String string3 = new String(chars3).trim();
            String string4 = new String(chars2).trim();
            StringBuilder stringBuilder = new StringBuilder((524288 & i) == 0 ? "rw" : "ro");
            String string5 = OPTIONS_MAP.entrySet().stream().filter(e -> (e.getKey() & i) > 0).map(Entry::getValue).collect(Collectors.joining(","));
            if (!string5.isEmpty()) {
               stringBuilder.append(',').append(string5);
            }

            Kernel32.INSTANCE.GetDiskFreeSpaceEx(string, largE_INTEGER, largE_INTEGER2, largE_INTEGER3);
            String string6 = ParseUtil.parseUuidOrDefault(string, "");
            OSFileStore osFileStore = new OSFileStore();
            osFileStore.setName(String.format("%s (%s)", string3, string2));
            osFileStore.setVolume(string);
            osFileStore.setLabel(string3);
            osFileStore.setMount(string2);
            osFileStore.setDescription(getDriveType(string2));
            osFileStore.setType(string4);
            osFileStore.setOptions(stringBuilder.toString());
            osFileStore.setUUID(string6);
            osFileStore.setFreeSpace(largE_INTEGER3.getValue());
            osFileStore.setUsableSpace(largE_INTEGER.getValue());
            osFileStore.setTotalSpace(largE_INTEGER2.getValue());
            arrayList.add(osFileStore);
         }

         bl = Kernel32.INSTANCE.FindNextVolume(handle, chars, 255);
      } while (bl);

      Kernel32.INSTANCE.FindVolumeClose(handle);
      return arrayList;
   }

   private static List<OSFileStore> getWmiVolumes(String nameToMatch, boolean localOnly) {
      ArrayList arrayList = new ArrayList();
      WmiResult wmiResult = Win32LogicalDisk.queryLogicalDisk(nameToMatch, localOnly);

      for (int i = 0; i < wmiResult.getResultCount(); i++) {
         long l = WmiUtil.getUint64(wmiResult, Win32LogicalDisk.LogicalDiskProperty.FREESPACE, i);
         long m = WmiUtil.getUint64(wmiResult, Win32LogicalDisk.LogicalDiskProperty.SIZE, i);
         String string = WmiUtil.getString(wmiResult, Win32LogicalDisk.LogicalDiskProperty.DESCRIPTION, i);
         String string2 = WmiUtil.getString(wmiResult, Win32LogicalDisk.LogicalDiskProperty.NAME, i);
         String string3 = WmiUtil.getString(wmiResult, Win32LogicalDisk.LogicalDiskProperty.VOLUMENAME, i);
         int j = WmiUtil.getUint32(wmiResult, Win32LogicalDisk.LogicalDiskProperty.DRIVETYPE, i);
         String string4;
         if (j != 4) {
            char[] chars = new char[255];
            Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(string2 + "\\", chars, 255);
            string4 = new String(chars).trim();
         } else {
            string4 = WmiUtil.getString(wmiResult, Win32LogicalDisk.LogicalDiskProperty.PROVIDERNAME, i);
            String[] strings = string4.split("\\\\");
            if (strings.length > 1 && strings[strings.length - 1].length() > 0) {
               string = strings[strings.length - 1];
            }
         }

         OSFileStore osFileStore = new OSFileStore();
         osFileStore.setName(String.format("%s (%s)", string, string2));
         osFileStore.setVolume(string4);
         osFileStore.setLabel(string3);
         osFileStore.setMount(string2 + "\\");
         osFileStore.setDescription(getDriveType(string2));
         osFileStore.setType(WmiUtil.getString(wmiResult, Win32LogicalDisk.LogicalDiskProperty.FILESYSTEM, i));
         osFileStore.setUUID("");
         osFileStore.setFreeSpace(l);
         osFileStore.setUsableSpace(l);
         osFileStore.setTotalSpace(m);
         arrayList.add(osFileStore);
      }

      return arrayList;
   }

   private static String getDriveType(String drive) {
      switch (Kernel32.INSTANCE.GetDriveType(drive)) {
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
      Map map = ProcessInformation.queryHandles();
      List list = (List)map.get(ProcessInformation.HandleCountProperty.HANDLECOUNT);
      long l = 0L;
      if (list != null) {
         for (int i = 0; i < list.size(); i++) {
            l += ((java.lang.Long)(list.get(i)));
         }
      }

      return l;
   }

   @Override
   public long getMaxFileDescriptors() {
      return MAX_WINDOWS_HANDLES;
   }

   public static boolean updateFileStoreStats(OSFileStore osFileStore) {
      List list = getLocalVolumes(osFileStore.getVolume());
      if (list.isEmpty()) {
         String string = osFileStore.getMount().length() < 2 ? null : osFileStore.getMount().substring(0, 2);
         list = getWmiVolumes(string, false);
      }

      for (Object object : list) {
         OSFileStore osFileStore2 = (OSFileStore)object;
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

   static {
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
