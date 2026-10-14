package oshi.hardware.platform.unix.solaris;

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
public class SolarisUsbDevice extends AbstractUsbDevice {
   public SolarisUsbDevice(
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
            new SolarisUsbDevice(
               usbDevice.getName(),
               usbDevice.getVendor(),
               usbDevice.getVendorId(),
               usbDevice.getProductId(),
               usbDevice.getSerialNumber(),
               usbDevice.getUniqueDeviceId(),
               new SolarisUsbDevice[0]
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
      List list = ExecutingCommand.runNative("prtconf -pv");
      if (list.isEmpty()) {
         return new SolarisUsbDevice[0];
      }

      HashMap hashMap6 = new HashMap();
      String string = "";
      int i = 0;
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         String string2 = (String)object;
         if (string2.contains("Node 0x")) {
            string = string2.replaceFirst("^\\s*", "");
            int j = string2.length() - string.length();
            if (i == 0) {
               i = j;
            }

            hashMap6.put(j, string);
            if (j > i) {
               ((java.util.List)(hashMap4.computeIfAbsent(hashMap6.get(j - i), x -> new ArrayList<>()))).add(string);
            } else {
               arrayList.add(string);
            }
         } else if (!string.isEmpty()) {
            string2 = string2.trim();
            if (string2.startsWith("model:")) {
               hashMap.put(string, ParseUtil.getSingleQuoteStringValue(string2));
            } else if (string2.startsWith("name:")) {
               hashMap.putIfAbsent(string, ParseUtil.getSingleQuoteStringValue(string2));
            } else if (string2.startsWith("vendor-id:")) {
               hashMap2.put(string, string2.substring(string2.length() - 4));
            } else if (string2.startsWith("device-id:")) {
               hashMap3.put(string, string2.substring(string2.length() - 4));
            } else if (string2.startsWith("class-code:")) {
               hashMap5.putIfAbsent(string, string2.substring(string2.length() - 8, string2.length() - 4));
            } else if (string2.startsWith("device_type:")) {
               hashMap5.putIfAbsent(string, ParseUtil.getSingleQuoteStringValue(string2));
            }
         }
      }

      ArrayList arrayList2 = new ArrayList();

      for (Object object2 : arrayList) {
         String string3 = (String)object2;
         if ("000c".equals(hashMap5.getOrDefault(string3, "")) || "usb".equals(hashMap5.getOrDefault(string3, ""))) {
            arrayList2.add(getDeviceAndChildren(string3, "0000", "0000", hashMap, hashMap2, hashMap3, hashMap4));
         }
      }

      return (oshi.hardware.UsbDevice[])(arrayList2.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, UsbDevice[] connectedDevices) {
      for (UsbDevice usbDevice : connectedDevices) {
         deviceList.add(usbDevice);
         addDevicesToList(deviceList, usbDevice.getConnectedDevices());
      }
   }

   private static SolarisUsbDevice getDeviceAndChildren(
      String devPath,
      String vid,
      String pid,
      Map<String, String> nameMap,
      Map<String, String> vendorIdMap,
      Map<String, String> productIdMap,
      Map<String, List<String>> hubMap
   ) {
      String string = vendorIdMap.getOrDefault(devPath, vid);
      String string2 = productIdMap.getOrDefault(devPath, pid);
      List list = hubMap.getOrDefault(devPath, new ArrayList());
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         String string3 = (String)object;
         arrayList.add(getDeviceAndChildren(string3, string, string2, nameMap, vendorIdMap, productIdMap, hubMap));
      }

      Collections.sort(arrayList);
      return new SolarisUsbDevice(nameMap.getOrDefault(devPath, string + ":" + string2), "", string, string2, "", devPath, (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[0])));
   }
}
