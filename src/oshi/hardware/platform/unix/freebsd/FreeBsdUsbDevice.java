package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public class FreeBsdUsbDevice extends AbstractUsbDevice {
   public FreeBsdUsbDevice(
      String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, UsbDevice[] connectedDevices
   ) {
      super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
   }

   public static UsbDevice[] getUsbDevices(boolean tree) {
      UsbDevice[] usbDevices = getUsbDevices();
      if (tree) {
         return usbDevices;
      }

      ArrayList arrayList = new ArrayList();

      for (UsbDevice usbDevice : usbDevices) {
         arrayList.add(
            new FreeBsdUsbDevice(
               usbDevice.getName(),
               usbDevice.getVendor(),
               usbDevice.getVendorId(),
               usbDevice.getProductId(),
               usbDevice.getSerialNumber(),
               usbDevice.getUniqueDeviceId(),
               new FreeBsdUsbDevice[0]
            )
         );
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
      HashMap hashMap7 = new HashMap();
      List list = ExecutingCommand.runNative("lshal");
      if (list.isEmpty()) {
         return new FreeBsdUsbDevice[0];
      }

      String string = "";
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         String string2 = (String)object;
         if (string2.startsWith("udi =")) {
            string = ParseUtil.getSingleQuoteStringValue(string2);
         } else if (!string.isEmpty()) {
            string2 = string2.trim();
            if (!string2.isEmpty()) {
               if (string2.startsWith("freebsd.driver =") && "usbus".equals(ParseUtil.getSingleQuoteStringValue(string2))) {
                  arrayList.add(string);
               } else if (string2.contains(".parent =")) {
                  String string3 = ParseUtil.getSingleQuoteStringValue(string2);
                  if (!string.replace(string3, "").startsWith("_if")) {
                     hashMap6.put(string, string3);
                     ((java.util.List)(hashMap7.computeIfAbsent(string3, x -> new ArrayList<>()))).add(string);
                  }
               } else if (string2.contains(".product =")) {
                  hashMap.put(string, ParseUtil.getSingleQuoteStringValue(string2));
               } else if (string2.contains(".vendor =")) {
                  hashMap2.put(string, ParseUtil.getSingleQuoteStringValue(string2));
               } else if (string2.contains(".serial =")) {
                  String string4 = ParseUtil.getSingleQuoteStringValue(string2);
                  hashMap5.put(string, string4.startsWith("0x") ? ParseUtil.hexStringToString(string4.replace("0x", "")) : string4);
               } else if (string2.contains(".vendor_id =")) {
                  hashMap3.put(string, String.format("%04x", ParseUtil.getFirstIntValue(string2)));
               } else if (string2.contains(".product_id =")) {
                  hashMap4.put(string, String.format("%04x", ParseUtil.getFirstIntValue(string2)));
               }
            }
         }
      }

      ArrayList arrayList2 = new ArrayList();

      for (Object object2 : arrayList) {
         String string5 = (String)object2;
         String string6 = (String)hashMap6.get(string5);
         hashMap7.put(string6, hashMap7.get(string5));
         arrayList2.add(getDeviceAndChildren(string6, "0000", "0000", hashMap, hashMap2, hashMap3, hashMap4, hashMap5, hashMap7));
      }

      return (oshi.hardware.UsbDevice[])(arrayList2.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, UsbDevice[] connectedDevices) {
      for (UsbDevice usbDevice : connectedDevices) {
         deviceList.add(usbDevice);
         addDevicesToList(deviceList, usbDevice.getConnectedDevices());
      }
   }

   private static FreeBsdUsbDevice getDeviceAndChildren(
      String devPath,
      String vid,
      String pid,
      Map<String, String> nameMap,
      Map<String, String> vendorMap,
      Map<String, String> vendorIdMap,
      Map<String, String> productIdMap,
      Map<String, String> serialMap,
      Map<String, List<String>> hubMap
   ) {
      String string = vendorIdMap.getOrDefault(devPath, vid);
      String string2 = productIdMap.getOrDefault(devPath, pid);
      List list = hubMap.getOrDefault(devPath, new ArrayList());
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         String string3 = (String)object;
         arrayList.add(getDeviceAndChildren(string3, string, string2, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
      }

      Collections.sort(arrayList);
      return new FreeBsdUsbDevice(
         nameMap.getOrDefault(devPath, string + ":" + string2),
         vendorMap.getOrDefault(devPath, ""),
         string,
         string2,
         serialMap.getOrDefault(devPath, ""),
         devPath,
         (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[0]))
      );
   }
}
