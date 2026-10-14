package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFIndex;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.DiskArbitration.DADiskRef;
import com.sun.jna.platform.mac.DiskArbitration.DASessionRef;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.SystemB.Statfs;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class MacDisks {
   private static final CoreFoundation CF = CoreFoundation.INSTANCE;
   private static final DiskArbitration DA = DiskArbitration.INSTANCE;

   private MacDisks() {
   }

   private static Map<MacDisks.CFKey, CFStringRef> mapCFKeys() {
      EnumMap enumMap = new EnumMap<>(MacDisks.CFKey.class);

      for (MacDisks.CFKey cfKey : MacDisks.CFKey.values()) {
         enumMap.put(cfKey, CFStringRef.createCFString(cfKey.getKey()));
      }

      return enumMap;
   }

   private static Map<String, String> queryMountPointMap() {
      HashMap hashMap = new HashMap();
      int i = SystemB.INSTANCE.getfsstat64(null, 0, 0);
      Statfs[] statfss = new Statfs[i];
      SystemB.INSTANCE.getfsstat64(statfss, i * new Statfs().size(), 16);

      for (Statfs statfs : statfss) {
         String string = new String(statfs.f_mntfromname, StandardCharsets.UTF_8).trim();
         hashMap.put(string.replace("/dev/", ""), new String(statfs.f_mntonname, StandardCharsets.UTF_8).trim());
      }

      return hashMap;
   }

   private static Map<String, String> queryLogicalVolumeMap() {
      HashMap hashMap = new HashMap();
      HashSet hashSet = new HashSet();
      boolean bl = false;

      for (String string : ExecutingCommand.runNative("diskutil cs list")) {
         if (string.contains("Logical Volume Group")) {
            hashSet.clear();
            bl = false;
         } else if (string.contains("Logical Volume Family")) {
            bl = true;
         } else if (string.contains("Disk:")) {
            String string2 = ParseUtil.parseLastString(string);
            if (!bl) {
               hashSet.add(ParseUtil.parseLastString(string));
            } else {
               for (Object object : hashSet) {
                  String string3 = (String)object;
                  hashMap.put(string3, string2);
               }

               hashSet.clear();
            }
         }
      }

      return hashMap;
   }

   private static boolean updateDiskStats(
      HWDiskStore diskStore,
      DASessionRef session,
      Map<String, String> mountPointMap,
      Map<String, String> logicalVolumeMap,
      Map<MacDisks.CFKey, CFStringRef> cfKeyMap
   ) {
      String string = diskStore.getName();
      CFMutableDictionaryRef cfMutableDictionaryRef = IOKitUtil.getBSDNameMatchingDict(string);
      if (cfMutableDictionaryRef != null) {
         IOIterator ioIterator = IOKitUtil.getMatchingServices(cfMutableDictionaryRef);
         if (ioIterator != null) {
            IORegistryEntry ioRegistryEntry = ioIterator.next();
            if (ioRegistryEntry != null) {
               if (ioRegistryEntry.conformsTo("IOMedia")) {
                  IORegistryEntry ioRegistryEntry2 = ioRegistryEntry.getParentEntry("IOService");
                  if (ioRegistryEntry2 != null && ioRegistryEntry2.conformsTo("IOBlockStorageDriver")) {
                     CFMutableDictionaryRef cfMutableDictionaryRef2 = ioRegistryEntry2.createCFProperties();
                     Pointer pointer = cfMutableDictionaryRef2.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.STATISTICS));
                     CFDictionaryRef cfDictionaryRef = new CFDictionaryRef(pointer);
                     diskStore.setTimeStamp(System.currentTimeMillis());
                     pointer = cfDictionaryRef.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.READ_OPS));
                     CFNumberRef cfNumberRef = new CFNumberRef(pointer);
                     diskStore.setReads(cfNumberRef.longValue());
                     pointer = cfDictionaryRef.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.READ_BYTES));
                     cfNumberRef.setPointer(pointer);
                     diskStore.setReadBytes(cfNumberRef.longValue());
                     pointer = cfDictionaryRef.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.WRITE_OPS));
                     cfNumberRef.setPointer(pointer);
                     diskStore.setWrites(cfNumberRef.longValue());
                     pointer = cfDictionaryRef.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.WRITE_BYTES));
                     cfNumberRef.setPointer(pointer);
                     diskStore.setWriteBytes(cfNumberRef.longValue());
                     pointer = cfDictionaryRef.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.READ_TIME));
                     cfNumberRef.setPointer(pointer);
                     long l = cfNumberRef.longValue();
                     pointer = cfDictionaryRef.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.WRITE_TIME));
                     cfNumberRef.setPointer(pointer);
                     l += cfNumberRef.longValue();
                     diskStore.setTransferTime(l / 1000000L);
                     cfMutableDictionaryRef2.release();
                  }

                  ArrayList arrayList = new ArrayList();
                  CFMutableDictionaryRef cfMutableDictionaryRef3 = ioRegistryEntry.createCFProperties();
                  Pointer pointer2 = cfMutableDictionaryRef3.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.BSD_UNIT));
                  CFNumberRef cfNumberRef2 = new CFNumberRef(pointer2);
                  pointer2 = cfMutableDictionaryRef3.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.LEAF));
                  CFBooleanRef cfBooleanRef = new CFBooleanRef(pointer2);
                  CFMutableDictionaryRef cfMutableDictionaryRef4 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  cfMutableDictionaryRef4.setValue((PointerType)cfKeyMap.get(MacDisks.CFKey.BSD_UNIT), cfNumberRef2);
                  cfMutableDictionaryRef4.setValue((PointerType)cfKeyMap.get(MacDisks.CFKey.WHOLE), cfBooleanRef);
                  cfMutableDictionaryRef = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  cfMutableDictionaryRef.setValue((PointerType)cfKeyMap.get(MacDisks.CFKey.IO_PROPERTY_MATCH), cfMutableDictionaryRef4);
                  IOIterator ioIterator2 = IOKitUtil.getMatchingServices(cfMutableDictionaryRef);
                  cfMutableDictionaryRef3.release();
                  cfMutableDictionaryRef4.release();
                  if (ioIterator2 != null) {
                     for (IORegistryEntry ioRegistryEntry3 = IOKit.INSTANCE.IOIteratorNext(ioIterator2); ioRegistryEntry3 != null; ioRegistryEntry3 = IOKit.INSTANCE.IOIteratorNext(ioIterator2)) {
                        String string2 = ioRegistryEntry3.getStringProperty("BSD Name");
                        String string3 = string2;
                        String string4 = "";
                        DADiskRef daDiskRef = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, string2);
                        if (daDiskRef != null) {
                           CFDictionaryRef cfDictionaryRef2 = DA.DADiskCopyDescription(daDiskRef);
                           if (cfDictionaryRef2 != null) {
                              pointer2 = cfDictionaryRef2.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.DA_MEDIA_NAME));
                              CFStringRef cfStringRef = new CFStringRef(pointer2);
                              string4 = cfStringRef.stringValue();
                              if (string4 == null) {
                                 string4 = "unknown";
                              }

                              pointer2 = cfDictionaryRef2.getValue((PointerType)cfKeyMap.get(MacDisks.CFKey.DA_VOLUME_NAME));
                              if (pointer2 == null) {
                                 string3 = string4;
                              } else {
                                 cfStringRef.setPointer(pointer2);
                                 string3 = cfStringRef.stringValue();
                              }

                              cfDictionaryRef2.release();
                           }

                           daDiskRef.release();
                        }

                        String string5;
                        if (logicalVolumeMap.containsKey(string2)) {
                           string5 = "Logical Volume: " + (String)logicalVolumeMap.get(string2);
                        } else {
                           string5 = mountPointMap.getOrDefault(string2, "");
                        }

                        Long long2 = ioRegistryEntry3.getLongProperty("Size");
                        Integer integer = ioRegistryEntry3.getIntegerProperty("BSD Major");
                        Integer integer2 = ioRegistryEntry3.getIntegerProperty("BSD Minor");
                        arrayList.add(
                           new HWPartition(
                              string2,
                              string3,
                              string4,
                              ioRegistryEntry3.getStringProperty("UUID"),
                              long2 == null ? 0L : long2,
                              integer == null ? 0 : integer,
                              integer2 == null ? 0 : integer2,
                              string5
                           )
                        );
                        ioRegistryEntry3.release();
                     }

                     ioIterator2.release();
                  }

                  Collections.sort(arrayList);
                  diskStore.setPartitions((oshi.hardware.HWPartition[])(arrayList.toArray(new HWPartition[0])));
                  if (ioRegistryEntry2 != null) {
                     ioRegistryEntry2.release();
                  }
               }

               ioRegistryEntry.release();
            }

            ioIterator.release();
            return true;
         }
      }

      return false;
   }

   public static boolean updateDiskStats(HWDiskStore diskStore) {
      DASessionRef daSessionRef = DA.DASessionCreate(CF.CFAllocatorGetDefault());
      if (daSessionRef == null) {
         return false;
      }

      Map map = mapCFKeys();
      boolean bl = updateDiskStats(diskStore, daSessionRef, queryMountPointMap(), queryLogicalVolumeMap(), map);
      daSessionRef.release();

      for (Object object : map.values()) {
         CFTypeRef cfTypeRef = (CFTypeRef)object;
         cfTypeRef.release();
      }

      return bl;
   }

   public static HWDiskStore[] getDisks() {
      Map map = queryMountPointMap();
      Map map2 = queryLogicalVolumeMap();
      Map map3 = mapCFKeys();
      ArrayList arrayList = new ArrayList();
      DASessionRef daSessionRef = DA.DASessionCreate(CF.CFAllocatorGetDefault());
      if (daSessionRef == null) {
         return new HWDiskStore[0];
      }

      ArrayList arrayList2 = new ArrayList();
      IOIterator ioIterator = IOKitUtil.getMatchingServices("IOMedia");
      if (ioIterator != null) {
         for (IORegistryEntry ioRegistryEntry = ioIterator.next(); ioRegistryEntry != null; ioRegistryEntry = ioIterator.next()) {
            Boolean boolean2 = ioRegistryEntry.getBooleanProperty("Whole");
            if (boolean2 != null && boolean2) {
               DADiskRef daDiskRef = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), daSessionRef, ioRegistryEntry);
               arrayList2.add(DA.DADiskGetBSDName(daDiskRef));
               daDiskRef.release();
            }

            ioRegistryEntry.release();
         }

         ioIterator.release();
      }

      for (Object object : arrayList2) {
         String string = (String)object;
         String string2 = "";
         String string3 = "";
         long l = 0L;
         String string4 = "/dev/" + string;
         DADiskRef daDiskRef2 = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), daSessionRef, string4);
         if (daDiskRef2 != null) {
            CFDictionaryRef cfDictionaryRef = DA.DADiskCopyDescription(daDiskRef2);
            if (cfDictionaryRef != null) {
               Pointer pointer = cfDictionaryRef.getValue((PointerType)map3.get(MacDisks.CFKey.DA_DEVICE_MODEL));
               CFStringRef cfStringRef = new CFStringRef(pointer);
               string2 = cfStringRef.stringValue();
               if (string2 == null) {
                  string2 = "unknown";
               }

               pointer = cfDictionaryRef.getValue((PointerType)map3.get(MacDisks.CFKey.DA_MEDIA_SIZE));
               CFNumberRef cfNumberRef = new CFNumberRef(pointer);
               l = cfNumberRef.longValue();
               cfDictionaryRef.release();
               if (!"Disk Image".equals(string2)) {
                  CFStringRef cfStringRef2 = CFStringRef.createCFString(string2);
                  CFMutableDictionaryRef cfMutableDictionaryRef = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  cfMutableDictionaryRef.setValue((PointerType)map3.get(MacDisks.CFKey.MODEL), cfStringRef2);
                  CFMutableDictionaryRef cfMutableDictionaryRef2 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  cfMutableDictionaryRef2.setValue((PointerType)map3.get(MacDisks.CFKey.IO_PROPERTY_MATCH), cfMutableDictionaryRef);
                  IOIterator ioIterator2 = IOKitUtil.getMatchingServices(cfMutableDictionaryRef2);
                  cfStringRef2.release();
                  cfMutableDictionaryRef.release();
                  if (ioIterator2 != null) {
                     for (IORegistryEntry ioRegistryEntry2 = ioIterator2.next(); ioRegistryEntry2 != null; ioRegistryEntry2 = ioIterator2.next()) {
                        string3 = ioRegistryEntry2.getStringProperty("Serial Number");
                        ioRegistryEntry2.release();
                        if (string3 != null) {
                           break;
                        }

                        ioRegistryEntry2.release();
                     }

                     ioIterator2.release();
                  }

                  if (string3 == null) {
                     string3 = "";
                  }
               }
            }

            daDiskRef2.release();
            if (l > 0L) {
               HWDiskStore hwDiskStore = new HWDiskStore();
               hwDiskStore.setName(string);
               hwDiskStore.setModel(string2.trim());
               hwDiskStore.setSerial(string3.trim());
               hwDiskStore.setSize(l);
               updateDiskStats(hwDiskStore, daSessionRef, map, map2, map3);
               arrayList.add(hwDiskStore);
            }
         }
      }

      daSessionRef.release();

      for (Object object2 : map3.values()) {
         CFTypeRef cfTypeRef = (CFTypeRef)object2;
         cfTypeRef.release();
      }

      Collections.sort(arrayList);
      return (oshi.hardware.HWDiskStore[])(arrayList.toArray(new HWDiskStore[0]));
   }

   private enum CFKey {
      IO_PROPERTY_MATCH("IOPropertyMatch"),
      STATISTICS("Statistics"),
      READ_OPS("Operations (Read)"),
      READ_BYTES("Bytes (Read)"),
      READ_TIME("Total Time (Read)"),
      WRITE_OPS("Operations (Write)"),
      WRITE_BYTES("Bytes (Write)"),
      WRITE_TIME("Total Time (Write)"),
      BSD_UNIT("BSD Unit"),
      LEAF("Leaf"),
      WHOLE("Whole"),
      DA_MEDIA_NAME("DAMediaName"),
      DA_VOLUME_NAME("DAVolumeName"),
      DA_MEDIA_SIZE("DAMediaSize"),
      DA_DEVICE_MODEL("DADeviceModel"),
      MODEL("Model");

      private final String key;

      CFKey(String key) {
         this.key = key;
      }

      public String getKey() {
         return this.key;
      }
   }
}
