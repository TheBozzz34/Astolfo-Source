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
   private static final int MNT_SYNCHRONOUS = 2;
   private static final int MNT_NOEXEC = 4;
   private static final int MNT_NOSUID = 8;
   private static final int MNT_NODEV = 16;
   private static final int MNT_UNION = 32;
   private static final int MNT_ASYNC = 64;
   private static final int MNT_CPROTECT = 128;
   private static final int MNT_EXPORTED = 256;
   private static final int MNT_QUARANTINE = 1024;
   private static final int MNT_LOCAL = 4096;
   private static final int y = 8192;
   private static final int MNT_ROOTFS = 16384;
   private static final int MNT_DOVOLFS = 32768;
   private static final int MNT_DONTBROWSE = 1048576;
   private static final int MNT_IGNORE_OWNERSHIP = 2097152;
   private static final int MNT_AUTOMOUNTED = 4194304;
   private static final int MNT_JOURNALED = 8388608;
   private static final int MNT_NOUSERXATTR = 16777216;
   private static final int MNT_DEFWRITE = 33554432;
   private static final int MNT_MULTILABEL = 67108864;
   private static final int MNT_NOATIME = 268435456;
   private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();

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
      int i = SystemB.INSTANCE.getfsstat64(null, 0, 0);
      if (i > 0) {
         DASessionRef daSessionRef = DiskArbitration.INSTANCE.DASessionCreate(CoreFoundation.INSTANCE.CFAllocatorGetDefault());
         if (daSessionRef != null) {
            CFStringRef cfStringRef = CFStringRef.createCFString("DAVolumeName");
            Statfs[] statfss = new Statfs[i];
            i = SystemB.INSTANCE.getfsstat64(statfss, i * new Statfs().size(), 16);

            for (int j = 0; j < i; j++) {
               String string = new String(statfss[j].f_mntfromname, StandardCharsets.UTF_8).trim();
               int k = statfss[j].f_flags;
               if ((!localOnly || (k & 4096) != 0) && !string.equals("devfs") && !string.startsWith("map ")) {
                  String string2 = new String(statfss[j].f_fstypename, StandardCharsets.UTF_8).trim();
                  String string3 = "Volume";
                  if (LOCAL_DISK.matcher(string).matches()) {
                     string3 = "Local Disk";
                  } else if (string.startsWith("localhost:") || string.startsWith("//") || string.startsWith("smb://") || NETWORK_FS_TYPES.contains(string2)) {
                     string3 = "Network Drive";
                  }

                  String string4 = new String(statfss[j].f_mntonname, StandardCharsets.UTF_8).trim();
                  String string5 = "";
                  File file = new File(string4);
                  if (string5.isEmpty()) {
                     string5 = file.getName();
                     if (string5.isEmpty()) {
                        string5 = file.getPath();
                     }
                  }

                  if (nameToMatch == null || nameToMatch.equals(string5)) {
                     StringBuilder stringBuilder = new StringBuilder((1 & k) == 0 ? "rw" : "ro");
                     String string6 = OPTIONS_MAP.entrySet().stream().filter(e -> (e.getKey() & k) > 0).map(Entry::getValue).collect(Collectors.joining(","));
                     if (!string6.isEmpty()) {
                        stringBuilder.append(',').append(string6);
                     }

                     String string7 = "";
                     String string8 = string.replace("/dev/disk", "disk");
                     if (string8.startsWith("disk")) {
                        DADiskRef daDiskRef = DiskArbitration.INSTANCE.DADiskCreateFromBSDName(CoreFoundation.INSTANCE.CFAllocatorGetDefault(), daSessionRef, string);
                        if (daDiskRef != null) {
                           CFDictionaryRef cfDictionaryRef = DiskArbitration.INSTANCE.DADiskCopyDescription(daDiskRef);
                           if (cfDictionaryRef != null) {
                              Pointer pointer = cfDictionaryRef.getValue(cfStringRef);
                              CFStringRef cfStringRef2 = new CFStringRef(pointer);
                              string5 = cfStringRef2.stringValue();
                              if (string5 == null) {
                                 string5 = "unknown";
                              }

                              cfDictionaryRef.release();
                           }

                           daDiskRef.release();
                        }

                        CFMutableDictionaryRef cfMutableDictionaryRef = IOKitUtil.getBSDNameMatchingDict(string8);
                        if (cfMutableDictionaryRef != null) {
                           IOIterator ioIterator = IOKitUtil.getMatchingServices(cfMutableDictionaryRef);
                           if (ioIterator != null) {
                              IORegistryEntry ioRegistryEntry = ioIterator.next();
                              if (ioRegistryEntry != null && ioRegistryEntry.conformsTo("IOMedia")) {
                                 string7 = ioRegistryEntry.getStringProperty("UUID");
                                 if (string7 != null) {
                                    string7 = string7.toLowerCase();
                                 }

                                 ioRegistryEntry.release();
                              }

                              ioIterator.release();
                           }
                        }
                     }

                     OSFileStore osFileStore = new OSFileStore();
                     osFileStore.setName(string5);
                     osFileStore.setVolume(string);
                     osFileStore.setLabel(string5);
                     osFileStore.setMount(string4);
                     osFileStore.setDescription(string3);
                     osFileStore.setType(string2);
                     osFileStore.setOptions(stringBuilder.toString());
                     osFileStore.setUUID(string7 == null ? "" : string7);
                     osFileStore.setFreeSpace(file.getFreeSpace());
                     osFileStore.setUsableSpace(file.getUsableSpace());
                     osFileStore.setTotalSpace(file.getTotalSpace());
                     osFileStore.setFreeInodes(statfss[j].f_ffree);
                     osFileStore.setTotalInodes(statfss[j].f_files);
                     arrayList.add(osFileStore);
                  }
               }
            }

            cfStringRef.release();
            daSessionRef.release();
         }
      }

      return arrayList;
   }

   @Override
   public long getOpenFileDescriptors() {
      return SysctlUtil.sysctl("kern.num_files", 0);
   }

   @Override
   public long getMaxFileDescriptors() {
      return SysctlUtil.sysctl("kern.maxfiles", 0);
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

   static {
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
