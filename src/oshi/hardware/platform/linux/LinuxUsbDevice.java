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
   private static final long c = 108885805590377L;

   public LinuxUsbDevice(String var1, String var2, String var3, String var4, String var5, String var6, UsbDevice[] var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public static UsbDevice[] getUsbDevices(boolean var0) {
      UsbDevice[] var4 = getUsbDevices();
      if (var0) {
         return var4;
      }

      ArrayList var5 = new ArrayList();

      for (UsbDevice var9 : var4) {
         var5.add(
            new LinuxUsbDevice(
               var9.getName(), var9.getVendor(), var9.getVendorId(), var9.getProductId(), var9.getSerialNumber(), var9.getUniqueDeviceId(), new MacUsbDevice[0]
            )
         );
         addDevicesToList(var5, var9.getConnectedDevices());
      }

      return (oshi.hardware.UsbDevice[])(var5.toArray(new UsbDevice[0]));
   }

   private static UsbDevice[] getUsbDevices() {
      Udev.UdevHandle var3 = Udev.INSTANCE.udev_new();
      Udev.UdevEnumerate var4 = Udev.INSTANCE.udev_enumerate_new(var3);
      Udev.INSTANCE.udev_enumerate_add_match_subsystem(var4, "usb");
      Udev.INSTANCE.udev_enumerate_scan_devices(var4);
      Udev.UdevListEntry var5 = Udev.INSTANCE.udev_enumerate_get_list_entry(var4);
      ArrayList var6 = new ArrayList();
      HashMap var7 = new HashMap();
      HashMap var8 = new HashMap();
      HashMap var9 = new HashMap();
      HashMap var10 = new HashMap();
      HashMap var11 = new HashMap();
      HashMap var12 = new HashMap();

      for (Udev.UdevListEntry var13 = var5; var13 != null; var13 = Udev.INSTANCE.udev_list_entry_get_next(var13)) {
         String var14 = Udev.INSTANCE.udev_list_entry_get_name(var13);
         Udev.UdevDevice var15 = Udev.INSTANCE.udev_device_new_from_syspath(var3, var14);
         if ("usb_device".equals(Udev.INSTANCE.udev_device_get_devtype(var15))) {
            String var16 = Udev.INSTANCE.udev_device_get_sysattr_value(var15, "product");
            if (var16 != null) {
               var7.put(var14, var16);
            }

            var16 = Udev.INSTANCE.udev_device_get_sysattr_value(var15, "manufacturer");
            if (var16 != null) {
               var8.put(var14, var16);
            }

            var16 = Udev.INSTANCE.udev_device_get_sysattr_value(var15, "idVendor");
            if (var16 != null) {
               var9.put(var14, var16);
            }

            var16 = Udev.INSTANCE.udev_device_get_sysattr_value(var15, "idProduct");
            if (var16 != null) {
               var10.put(var14, var16);
            }

            var16 = Udev.INSTANCE.udev_device_get_sysattr_value(var15, "serial");
            if (var16 != null) {
               var11.put(var14, var16);
            }

            Udev.UdevDevice var17 = Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(var15, "usb", "usb_device");
            if (var17 == null) {
               var6.add(var14);
            } else {
               String var18 = Udev.INSTANCE.udev_device_get_syspath(var17);
               ((java.util.List)(var12.computeIfAbsent(var18, var0 -> new ArrayList<>()))).add(var14);
            }

            Udev.INSTANCE.udev_device_unref(var15);
         }
      }

      Udev.INSTANCE.udev_enumerate_unref(var4);
      Udev.INSTANCE.udev_unref(var3);
      ArrayList var19 = new ArrayList();

      for (Object var21$raw : var6) {
         String var21 = (String)var21$raw;
         var19.add(getDeviceAndChildren(var21, "0000", "0000", var7, var8, var9, var10, var11, var12));
      }

      return (oshi.hardware.UsbDevice[])(var19.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> var0, UsbDevice[] var1) {
      for (UsbDevice var8 : var1) {
         var0.add(var8);
         addDevicesToList(var0, var8.getConnectedDevices());
      }
   }

   private static LinuxUsbDevice getDeviceAndChildren(
      String var0,
      String var1,
      String var2,
      Map<String, String> var3,
      Map<String, String> var4,
      Map<String, String> var5,
      Map<String, String> var6,
      Map<String, String> var7,
      Map<String, List<String>> var8
   ) {
      String var12 = var5.getOrDefault(var0, var1);
      String var13 = var6.getOrDefault(var0, var2);
      List var14 = var8.getOrDefault(var0, new ArrayList());
      ArrayList var15 = new ArrayList();

      for (Object var17$raw : var14) {
         String var17 = (String)var17$raw;
         var15.add(getDeviceAndChildren(var17, var12, var13, var3, var4, var5, var6, var7, var8));
      }

      Collections.sort(var15);
      return new LinuxUsbDevice(
         var3.getOrDefault(var0, var12 + ":" + var13),
         var4.getOrDefault(var0, ""),
         var12,
         var13,
         var7.getOrDefault(var0, ""),
         var0,
         (oshi.hardware.UsbDevice[])(var15.toArray(new UsbDevice[var15.size()]))
      );
   }
}
