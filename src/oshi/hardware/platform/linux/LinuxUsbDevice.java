package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.hardware.platform.mac.MacUsbDevice;
import oshi.jna.platform.linux.Udev;

@Immutable
public class LinuxUsbDevice extends AbstractUsbDevice {
   public LinuxUsbDevice(
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
            new LinuxUsbDevice(
               usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), new MacUsbDevice[0]
            )
         );
         addDevicesToList(arrayList, usbDevice.getConnectedDevices());
      }

      return (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[0]));
   }

   private static UsbDevice[] getUsbDevices() {
      Udev.UdevHandle udevHandle = Udev.INSTANCE.udev_new();
      Udev.UdevEnumerate udevEnumerate = Udev.INSTANCE.udev_enumerate_new(udevHandle);
      Udev.INSTANCE.udev_enumerate_add_match_subsystem(udevEnumerate, "usb");
      Udev.INSTANCE.udev_enumerate_scan_devices(udevEnumerate);
      Udev.UdevListEntry udevListEntry = Udev.INSTANCE.udev_enumerate_get_list_entry(udevEnumerate);
      ArrayList arrayList = new ArrayList();
      HashMap hashMap = new HashMap();
      HashMap hashMap2 = new HashMap();
      HashMap hashMap3 = new HashMap();
      HashMap hashMap4 = new HashMap();
      HashMap hashMap5 = new HashMap();
      HashMap hashMap6 = new HashMap();

      for (Udev.UdevListEntry udevListEntry2 = udevListEntry; udevListEntry2 != null; udevListEntry2 = Udev.INSTANCE.udev_list_entry_get_next(udevListEntry2)) {
         String string = Udev.INSTANCE.udev_list_entry_get_name(udevListEntry2);
         Udev.UdevDevice udevDevice = Udev.INSTANCE.udev_device_new_from_syspath(udevHandle, string);
         if ("usb_device".equals(Udev.INSTANCE.udev_device_get_devtype(udevDevice))) {
            String string2 = Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "product");
            if (string2 != null) {
               hashMap.put(string, string2);
            }

            string2 = Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "manufacturer");
            if (string2 != null) {
               hashMap2.put(string, string2);
            }

            string2 = Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "idVendor");
            if (string2 != null) {
               hashMap3.put(string, string2);
            }

            string2 = Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "idProduct");
            if (string2 != null) {
               hashMap4.put(string, string2);
            }

            string2 = Udev.INSTANCE.udev_device_get_sysattr_value(udevDevice, "serial");
            if (string2 != null) {
               hashMap5.put(string, string2);
            }

            Udev.UdevDevice udevDevice2 = Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(udevDevice, "usb", "usb_device");
            if (udevDevice2 == null) {
               arrayList.add(string);
            } else {
               String string3 = Udev.INSTANCE.udev_device_get_syspath(udevDevice2);
               ((java.util.List)(hashMap6.computeIfAbsent(string3, x -> new ArrayList<>()))).add(string);
            }

            Udev.INSTANCE.udev_device_unref(udevDevice);
         }
      }

      Udev.INSTANCE.udev_enumerate_unref(udevEnumerate);
      Udev.INSTANCE.udev_unref(udevHandle);
      ArrayList arrayList2 = new ArrayList();

      for (Object object : arrayList) {
         String string4 = (String)object;
         arrayList2.add(getDeviceAndChildren(string4, "0000", "0000", hashMap, hashMap2, hashMap3, hashMap4, hashMap5, hashMap6));
      }

      return (oshi.hardware.UsbDevice[])(arrayList2.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, UsbDevice[] connectedDevices) {
      for (UsbDevice usbDevice : connectedDevices) {
         deviceList.add(usbDevice);
         addDevicesToList(deviceList, usbDevice.getConnectedDevices());
      }
   }

   private static LinuxUsbDevice getDeviceAndChildren(
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
      return new LinuxUsbDevice(
         nameMap.getOrDefault(devPath, string + ":" + string2),
         vendorMap.getOrDefault(devPath, ""),
         string,
         string2,
         serialMap.getOrDefault(devPath, ""),
         devPath,
         (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[arrayList.size()]))
      );
   }
}
