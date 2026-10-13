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
   private static final long a = 71824371803481L;
   private static final long e = 819779402765172752L;

   private MacDisks() {
   }

   private static Map<MacDisks.CFKey, CFStringRef> mapCFKeys() {
      EnumMap var3 = new EnumMap<>(MacDisks.CFKey.class);

      for (MacDisks.CFKey var7 : MacDisks.CFKey.values()) {
         var3.put(var7, CFStringRef.createCFString(var7.getKey()));
      }

      return var3;
   }

   private static Map<String, String> queryMountPointMap() {
      HashMap var3 = new HashMap();
      int var4 = SystemB.INSTANCE.getfsstat64(null, 0, 0);
      Statfs[] var5 = new Statfs[var4];
      SystemB.INSTANCE.getfsstat64(var5, var4 * new Statfs().size(), (int)e);

      for (Statfs var9 : var5) {
         String var10 = new String(var9.f_mntfromname, StandardCharsets.UTF_8).trim();
         var3.put(var10.replace("/dev/", ""), new String(var9.f_mntonname, StandardCharsets.UTF_8).trim());
      }

      return var3;
   }

   private static Map<String, String> queryLogicalVolumeMap() {
      HashMap var3 = new HashMap();
      HashSet var4 = new HashSet();
      boolean var5 = false;

      for (String var7 : ExecutingCommand.runNative("diskutil cs list")) {
         if (var7.contains("Logical Volume Group")) {
            var4.clear();
            var5 = false;
         } else if (var7.contains("Logical Volume Family")) {
            var5 = true;
         } else if (var7.contains("Disk:")) {
            String var8 = ParseUtil.parseLastString(var7);
            if (!var5) {
               var4.add(ParseUtil.parseLastString(var7));
            } else {
               for (Object var10$raw : var4) {
                  String var10 = (String)var10$raw;
                  var3.put(var10, var8);
               }

               var4.clear();
            }
         }
      }

      return var3;
   }

   private static boolean updateDiskStats(
      HWDiskStore var0, DASessionRef var1, Map<String, String> var2, Map<String, String> var3, Map<MacDisks.CFKey, CFStringRef> var4
   ) {
      String var8 = var0.getName();
      CFMutableDictionaryRef var9 = IOKitUtil.getBSDNameMatchingDict(var8);
      if (var9 != null) {
         IOIterator var10 = IOKitUtil.getMatchingServices(var9);
         if (var10 != null) {
            IORegistryEntry var11 = var10.next();
            if (var11 != null) {
               if (var11.conformsTo("IOMedia")) {
                  IORegistryEntry var12 = var11.getParentEntry("IOService");
                  if (var12 != null && var12.conformsTo("IOBlockStorageDriver")) {
                     CFMutableDictionaryRef var13 = var12.createCFProperties();
                     Pointer var14 = var13.getValue((PointerType)var4.get(MacDisks.CFKey.STATISTICS));
                     CFDictionaryRef var15 = new CFDictionaryRef(var14);
                     var0.setTimeStamp(System.currentTimeMillis());
                     var14 = var15.getValue((PointerType)var4.get(MacDisks.CFKey.READ_OPS));
                     CFNumberRef var16 = new CFNumberRef(var14);
                     var0.setReads(var16.longValue());
                     var14 = var15.getValue((PointerType)var4.get(MacDisks.CFKey.READ_BYTES));
                     var16.setPointer(var14);
                     var0.setReadBytes(var16.longValue());
                     var14 = var15.getValue((PointerType)var4.get(MacDisks.CFKey.WRITE_OPS));
                     var16.setPointer(var14);
                     var0.setWrites(var16.longValue());
                     var14 = var15.getValue((PointerType)var4.get(MacDisks.CFKey.WRITE_BYTES));
                     var16.setPointer(var14);
                     var0.setWriteBytes(var16.longValue());
                     var14 = var15.getValue((PointerType)var4.get(MacDisks.CFKey.READ_TIME));
                     var16.setPointer(var14);
                     long var17 = var16.longValue();
                     var14 = var15.getValue((PointerType)var4.get(MacDisks.CFKey.WRITE_TIME));
                     var16.setPointer(var14);
                     var17 += var16.longValue();
                     var0.setTransferTime(var17 / 1000000L);
                     var13.release();
                  }

                  ArrayList var30 = new ArrayList();
                  CFMutableDictionaryRef var37 = var11.createCFProperties();
                  Pointer var38 = var37.getValue((PointerType)var4.get(MacDisks.CFKey.BSD_UNIT));
                  CFNumberRef var42 = new CFNumberRef(var38);
                  var38 = var37.getValue((PointerType)var4.get(MacDisks.CFKey.LEAF));
                  CFBooleanRef var44 = new CFBooleanRef(var38);
                  CFMutableDictionaryRef var18 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  var18.setValue((PointerType)var4.get(MacDisks.CFKey.BSD_UNIT), var42);
                  var18.setValue((PointerType)var4.get(MacDisks.CFKey.WHOLE), var44);
                  var9 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  var9.setValue((PointerType)var4.get(MacDisks.CFKey.IO_PROPERTY_MATCH), var18);
                  IOIterator var19 = IOKitUtil.getMatchingServices(var9);
                  var37.release();
                  var18.release();
                  if (var19 != null) {
                     for (IORegistryEntry var20 = IOKit.INSTANCE.IOIteratorNext(var19); var20 != null; var20 = IOKit.INSTANCE.IOIteratorNext(var19)) {
                        String var21 = var20.getStringProperty("BSD Name");
                        String var22 = var21;
                        String var23 = "";
                        DADiskRef var24 = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), var1, var21);
                        if (var24 != null) {
                           CFDictionaryRef var25 = DA.DADiskCopyDescription(var24);
                           if (var25 != null) {
                              var38 = var25.getValue((PointerType)var4.get(MacDisks.CFKey.DA_MEDIA_NAME));
                              CFStringRef var26 = new CFStringRef(var38);
                              var23 = var26.stringValue();
                              if (var23 == null) {
                                 var23 = "unknown";
                              }

                              var38 = var25.getValue((PointerType)var4.get(MacDisks.CFKey.DA_VOLUME_NAME));
                              if (var38 == null) {
                                 var22 = var23;
                              } else {
                                 var26.setPointer(var38);
                                 var22 = var26.stringValue();
                              }

                              var25.release();
                           }

                           var24.release();
                        }

                        String var45;
                        if (var3.containsKey(var21)) {
                           var45 = "Logical Volume: " + (String)var3.get(var21);
                        } else {
                           var45 = var2.getOrDefault(var21, "");
                        }

                        Long var46 = var20.getLongProperty("Size");
                        Integer var27 = var20.getIntegerProperty("BSD Major");
                        Integer var28 = var20.getIntegerProperty("BSD Minor");
                        var30.add(
                           new HWPartition(
                              var21,
                              var22,
                              var23,
                              var20.getStringProperty("UUID"),
                              var46 == null ? 0L : var46,
                              var27 == null ? 0 : var27,
                              var28 == null ? 0 : var28,
                              var45
                           )
                        );
                        var20.release();
                     }

                     var19.release();
                  }

                  Collections.sort(var30);
                  var0.setPartitions((oshi.hardware.HWPartition[])(var30.toArray(new HWPartition[0])));
                  if (var12 != null) {
                     var12.release();
                  }
               }

               var11.release();
            }

            var10.release();
            return true;
         }
      }

      return false;
   }

   public static boolean updateDiskStats(HWDiskStore var0) {
      DASessionRef var4 = DA.DASessionCreate(CF.CFAllocatorGetDefault());
      if (var4 == null) {
         return false;
      }

      Map var5 = mapCFKeys();
      boolean var6 = updateDiskStats(var0, var4, queryMountPointMap(), queryLogicalVolumeMap(), var5);
      var4.release();

      for (Object var8$raw : var5.values()) {
         CFTypeRef var8 = (CFTypeRef)var8$raw;
         var8.release();
      }

      return var6;
   }

   public static HWDiskStore[] getDisks() {
      Map var3 = queryMountPointMap();
      Map var4 = queryLogicalVolumeMap();
      Map var5 = mapCFKeys();
      ArrayList var6 = new ArrayList();
      DASessionRef var7 = DA.DASessionCreate(CF.CFAllocatorGetDefault());
      if (var7 == null) {
         return new HWDiskStore[0];
      }

      ArrayList var8 = new ArrayList();
      IOIterator var9 = IOKitUtil.getMatchingServices("IOMedia");
      if (var9 != null) {
         for (IORegistryEntry var10 = var9.next(); var10 != null; var10 = var9.next()) {
            Boolean var11 = var10.getBooleanProperty("Whole");
            if (var11 != null && var11) {
               DADiskRef var12 = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), var7, var10);
               var8.add(DA.DADiskGetBSDName(var12));
               var12.release();
            }

            var10.release();
         }

         var9.release();
      }

      for (Object var29$raw : var8) {
         String var29 = (String)var29$raw;
         String var31 = "";
         String var13 = "";
         long var14 = 0L;
         String var16 = "/dev/" + var29;
         DADiskRef var17 = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), var7, var16);
         if (var17 != null) {
            CFDictionaryRef var18 = DA.DADiskCopyDescription(var17);
            if (var18 != null) {
               Pointer var19 = var18.getValue((PointerType)var5.get(MacDisks.CFKey.DA_DEVICE_MODEL));
               CFStringRef var20 = new CFStringRef(var19);
               var31 = var20.stringValue();
               if (var31 == null) {
                  var31 = "unknown";
               }

               var19 = var18.getValue((PointerType)var5.get(MacDisks.CFKey.DA_MEDIA_SIZE));
               CFNumberRef var21 = new CFNumberRef(var19);
               var14 = var21.longValue();
               var18.release();
               if (!"Disk Image".equals(var31)) {
                  CFStringRef var22 = CFStringRef.createCFString(var31);
                  CFMutableDictionaryRef var23 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  var23.setValue((PointerType)var5.get(MacDisks.CFKey.MODEL), var22);
                  CFMutableDictionaryRef var24 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
                  var24.setValue((PointerType)var5.get(MacDisks.CFKey.IO_PROPERTY_MATCH), var23);
                  IOIterator var25 = IOKitUtil.getMatchingServices(var24);
                  var22.release();
                  var23.release();
                  if (var25 != null) {
                     for (IORegistryEntry var26 = var25.next(); var26 != null; var26 = var25.next()) {
                        var13 = var26.getStringProperty("Serial Number");
                        var26.release();
                        if (var13 != null) {
                           break;
                        }

                        var26.release();
                     }

                     var25.release();
                  }

                  if (var13 == null) {
                     var13 = "";
                  }
               }
            }

            var17.release();
            if (var14 > 0L) {
               HWDiskStore var33 = new HWDiskStore();
               var33.setName(var29);
               var33.setModel(var31.trim());
               var33.setSerial(var13.trim());
               var33.setSize(var14);
               updateDiskStats(var33, var7, var3, var4, var5);
               var6.add(var33);
            }
         }
      }

      var7.release();

      for (Object var30$raw : var5.values()) {
         CFTypeRef var30 = (CFTypeRef)var30$raw;
         var30.release();
      }

      Collections.sort(var6);
      return (oshi.hardware.HWDiskStore[])(var6.toArray(new HWDiskStore[0]));
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

      CFKey(String var3) {
         this.key = var3;
      }

      public String getKey() {
         return this.key;
      }
   }
}
