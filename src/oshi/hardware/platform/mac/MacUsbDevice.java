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

   public MacUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, UsbDevice[] connectedDevices) {
      super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
   }

   public static UsbDevice[] getUsbDevices(boolean tree) {
      UsbDevice[] usbDevices = getUsbDevices();
      if (tree) {
         return usbDevices;
      }

      ArrayList arrayList = new ArrayList();

      for (UsbDevice usbDevice : usbDevices) {
         addDevicesToList(arrayList, usbDevice.getConnectedDevices());
      }

      return (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[0]));
   }

   private static UsbDevice[] getUsbDevices() {
      HashMap hashMap = new HashMap();
      HashMap hashMap2 = new HashMap();
      HashMap hashMap3 = new HashMap();
      HashMap hashMap4 = new HashMap();
      HashMap hashMap5 = new HashMap();
      HashMap hashMap6 = new HashMap();
      ArrayList arrayList = new ArrayList();
      IOIterator ioIterator = IOKitUtil.getMatchingServices("IOUSBController");
      if (ioIterator != null) {
         CFStringRef cfStringRef = CFStringRef.createCFString("locationID");
         CFStringRef cfStringRef2 = CFStringRef.createCFString("IOPropertyMatch");

         for (IORegistryEntry ioRegistryEntry = ioIterator.next(); ioRegistryEntry != null; ioRegistryEntry = ioIterator.next()) {
            long l = ioRegistryEntry.getRegistryEntryID();
            arrayList.add(l);
            hashMap.put(l, ioRegistryEntry.getName());
            CFTypeRef cfTypeRef = ioRegistryEntry.createCFProperty(cfStringRef);
            if (cfTypeRef != null) {
               getControllerIdByLocation(l, cfTypeRef, cfStringRef, cfStringRef2, hashMap3, hashMap4);
               cfTypeRef.release();
            }

            IOIterator ioIterator2 = ioRegistryEntry.getChildIterator("IOService");

            for (IORegistryEntry ioRegistryEntry2 = ioIterator2.next(); ioRegistryEntry2 != null; ioRegistryEntry2 = ioIterator2.next()) {
               long m = ioRegistryEntry2.getRegistryEntryID();
               IORegistryEntry ioRegistryEntry3 = ioRegistryEntry2.getParentEntry("IOUSB");
               long n;
               if (ioRegistryEntry3 != null && ioRegistryEntry3.conformsTo("IOUSBDevice")) {
                  n = ioRegistryEntry3.getRegistryEntryID();
               } else {
                  n = l;
               }

               if (ioRegistryEntry3 != null) {
                  ioRegistryEntry3.release();
               }

               ((java.util.List)(hashMap6.computeIfAbsent(n, x -> new ArrayList<>()))).add(m);
               hashMap.put(m, ioRegistryEntry2.getName().trim());
               String string = ioRegistryEntry2.getStringProperty("USB Vendor Name");
               if (string != null) {
                  hashMap2.put(m, string.trim());
               }

               Long long2 = ioRegistryEntry2.getLongProperty("idVendor");
               if (long2 != null) {
                  hashMap3.put(m, String.format("%04x", 65535L & long2));
               }

               Long long3 = ioRegistryEntry2.getLongProperty("idProduct");
               if (long3 != null) {
                  hashMap4.put(m, String.format("%04x", 65535L & long3));
               }

               String string2 = ioRegistryEntry2.getStringProperty("USB Serial Number");
               if (string2 != null) {
                  hashMap5.put(m, string2.trim());
               }

               ioRegistryEntry2.release();
            }

            ioIterator2.release();
            ioRegistryEntry.release();
         }

         ioIterator.release();
         cfStringRef.release();
         cfStringRef2.release();
      }

      ArrayList arrayList2 = new ArrayList();

      for (Object object : arrayList) {
         Long long4 = (Long)object;
         arrayList2.add(getDeviceAndChildren(long4, "0000", "0000", hashMap, hashMap2, hashMap3, hashMap4, hashMap5, hashMap6));
      }

      return (oshi.hardware.UsbDevice[])(arrayList2.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, UsbDevice[] connectedDevices) {
      for (UsbDevice usbDevice : connectedDevices) {
         deviceList.add(
            new MacUsbDevice(
               usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), new MacUsbDevice[0]
            )
         );
         addDevicesToList(deviceList, usbDevice.getConnectedDevices());
      }
   }

   private static void getControllerIdByLocation(
      long id, CFTypeRef locationId, CFStringRef locationIDKey, CFStringRef ioPropertyMatchKey, Map<Long, String> vendorIdMap, Map<Long, String> productIdMap
   ) {
      CFMutableDictionaryRef cfMutableDictionaryRef = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
      cfMutableDictionaryRef.setValue(locationIDKey, locationId);
      CFMutableDictionaryRef cfMutableDictionaryRef2 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0L), null, null);
      cfMutableDictionaryRef2.setValue(ioPropertyMatchKey, cfMutableDictionaryRef);
      IOIterator ioIterator = IOKitUtil.getMatchingServices(cfMutableDictionaryRef2);
      cfMutableDictionaryRef.release();
      boolean bl = false;
      if (ioIterator != null) {
         for (IORegistryEntry ioRegistryEntry = ioIterator.next(); ioRegistryEntry != null && !bl; ioRegistryEntry = ioIterator.next()) {
            IORegistryEntry ioRegistryEntry2 = ioRegistryEntry.getParentEntry("IOService");
            if (ioRegistryEntry2 != null) {
               byte[] bytes = ioRegistryEntry2.getByteArrayProperty("vendor-id");
               if (bytes != null && bytes.length >= 2) {
                  vendorIdMap.put(id, String.format("%02x%02x", bytes[1], bytes[0]));
                  bl = true;
               }

               byte[] bytes2 = ioRegistryEntry2.getByteArrayProperty("device-id");
               if (bytes2 != null && bytes2.length >= 2) {
                  productIdMap.put(id, String.format("%02x%02x", bytes2[1], bytes2[0]));
                  bl = true;
               }

               ioRegistryEntry2.release();
            }

            ioRegistryEntry.release();
         }

         ioIterator.release();
      }
   }

   private static MacUsbDevice getDeviceAndChildren(
      Long registryEntryId,
      String vid,
      String pid,
      Map<Long, String> nameMap,
      Map<Long, String> vendorMap,
      Map<Long, String> vendorIdMap,
      Map<Long, String> productIdMap,
      Map<Long, String> serialMap,
      Map<Long, List<Long>> hubMap
   ) {
      String string = vendorIdMap.getOrDefault(registryEntryId, vid);
      String string2 = productIdMap.getOrDefault(registryEntryId, pid);
      List list = hubMap.getOrDefault(registryEntryId, new ArrayList());
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         Long long2 = (Long)object;
         arrayList.add(getDeviceAndChildren(long2, string, string2, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
      }

      Collections.sort(arrayList);
      return new MacUsbDevice(
         nameMap.getOrDefault(registryEntryId, string + ":" + string2),
         vendorMap.getOrDefault(registryEntryId, ""),
         string,
         string2,
         serialMap.getOrDefault(registryEntryId, ""),
         "0x" + Long.toHexString(registryEntryId),
         (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[0]))
      );
   }
}
