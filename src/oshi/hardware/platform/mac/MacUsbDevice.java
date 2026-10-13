package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.CoreFoundation.CFIndex;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;

@Immutable
public class MacUsbDevice extends AbstractUsbDevice {
   private static final CoreFoundation CF = CoreFoundation.INSTANCE;
   private static final long c = 131638886385539L;

   public MacUsbDevice(String var1, String var2, String var3, String var4, String var5, String var6, UsbDevice[] var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public static UsbDevice[] getUsbDevices(boolean var0) {
      UsbDevice[] var4 = getUsbDevices();
      if (var0) {
         return var4;
      }

      ArrayList var5 = new ArrayList();

      for (UsbDevice var9 : var4) {
         addDevicesToList(var5, var9.getConnectedDevices());
      }

      return (oshi.hardware.UsbDevice[])(var5.toArray(new UsbDevice[0]));
   }

   private static UsbDevice[] getUsbDevices() {
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      HashMap var5 = new HashMap();
      HashMap var6 = new HashMap();
      HashMap var7 = new HashMap();
      HashMap var8 = new HashMap();
      ArrayList var9 = new ArrayList();
      IOIterator var10 = IOKitUtil.getMatchingServices("IOUSBController");
      if (var10 != null) {
         CFStringRef var11 = CFStringRef.createCFString("locationID");
         CFStringRef var12 = CFStringRef.createCFString("IOPropertyMatch");

         for (IORegistryEntry var13 = var10.next(); var13 != null; var13 = var10.next()) {
            long var14 = var13.getRegistryEntryID();
            var9.add(var14);
            var3.put(var14, var13.getName());
            CFTypeRef var16 = var13.createCFProperty(var11);
            if (var16 != null) {
               getControllerIdByLocation(var14, var16, var11, var12, var5, var6);
               var16.release();
            }

            IOIterator var17 = var13.getChildIterator("IOService");

            for (IORegistryEntry var18 = var17.next(); var18 != null; var18 = var17.next()) {
               long var19 = var18.getRegistryEntryID();
               IORegistryEntry var21 = var18.getParentEntry("IOUSB");
               long var22;
               if (var21 != null && var21.conformsTo("IOUSBDevice")) {
                  var22 = var21.getRegistryEntryID();
               } else {
                  var22 = var14;
               }

               if (var21 != null) {
                  var21.release();
               }

               ((java.util.List)(var8.computeIfAbsent(var22, var0 -> new ArrayList<>()))).add(var19);
               var3.put(var19, var18.getName().trim());
               String var24 = var18.getStringProperty("USB Vendor Name");
               if (var24 != null) {
                  var4.put(var19, var24.trim());
               }

               Long var25 = var18.getLongProperty("idVendor");
               if (var25 != null) {
                  var5.put(var19, String.format("%04x", 65535L & var25));
               }

               Long var26 = var18.getLongProperty("idProduct");
               if (var26 != null) {
                  var6.put(var19, String.format("%04x", 65535L & var26));
               }

               String var27 = var18.getStringProperty("USB Serial Number");
               if (var27 != null) {
                  var7.put(var19, var27.trim());
               }

               var18.release();
            }

            var17.release();
            var13.release();
         }

         var10.release();
         var11.release();
         var12.release();
      }

      ArrayList var28 = new ArrayList();

      for (Object var30$raw : var9) {
         Long var30 = (Long)var30$raw;
         var28.add(getDeviceAndChildren(var30, "0000", "0000", var3, var4, var5, var6, var7, var8));
      }

      return (oshi.hardware.UsbDevice[])(var28.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> var0, UsbDevice[] var1) {
      for (UsbDevice var8 : var1) {
         var0.add(
            new MacUsbDevice(
               var8.getName(), var8.getVendor(), var8.getVendorId(), var8.getProductId(), var8.getSerialNumber(), var8.getUniqueDeviceId(), new MacUsbDevice[0]
            )
         );
         addDevicesToList(var0, var8.getConnectedDevices());
      }
   }

   private static void getControllerIdByLocation(long var0, CFTypeRef var2, CFStringRef var3, CFStringRef var4, Map<Long, String> var5, Map<Long, String> var6) {
      CFMutableDictionaryRef var10 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
      var10.setValue(var3, var2);
      CFMutableDictionaryRef var11 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
      var11.setValue(var4, var10);
      IOIterator var12 = IOKitUtil.getMatchingServices(var11);
      var10.release();
      boolean var13 = false;
      if (var12 != null) {
         for (IORegistryEntry var14 = var12.next(); var14 != null && !var13; var14 = var12.next()) {
            IORegistryEntry var15 = var14.getParentEntry("IOService");
            if (var15 != null) {
               byte[] var16 = var15.getByteArrayProperty("vendor-id");
               if (var16 != null && var16.length >= 2) {
                  var5.put(var0, String.format("%02x%02x", var16[1], var16[0]));
                  var13 = true;
               }

               byte[] var17 = var15.getByteArrayProperty("device-id");
               if (var17 != null && var17.length >= 2) {
                  var6.put(var0, String.format("%02x%02x", var17[1], var17[0]));
                  var13 = true;
               }

               var15.release();
            }

            var14.release();
         }

         var12.release();
      }
   }

   private static MacUsbDevice getDeviceAndChildren(
      Long var0,
      String var1,
      String var2,
      Map<Long, String> var3,
      Map<Long, String> var4,
      Map<Long, String> var5,
      Map<Long, String> var6,
      Map<Long, String> var7,
      Map<Long, List<Long>> var8
   ) {
      String var12 = var5.getOrDefault(var0, var1);
      String var13 = var6.getOrDefault(var0, var2);
      List var14 = var8.getOrDefault(var0, new ArrayList());
      ArrayList var15 = new ArrayList();

      for (Object var17$raw : var14) {
         Long var17 = (Long)var17$raw;
         var15.add(getDeviceAndChildren(var17, var12, var13, var3, var4, var5, var6, var7, var8));
      }

      Collections.sort(var15);
      return new MacUsbDevice(
         var3.getOrDefault(var0, var12 + ":" + var13),
         var4.getOrDefault(var0, ""),
         var12,
         var13,
         var7.getOrDefault(var0, ""),
         "0x" + Long.toHexString(var0),
         (oshi.hardware.UsbDevice[])(var15.toArray(new UsbDevice[0]))
      );
   }
}
