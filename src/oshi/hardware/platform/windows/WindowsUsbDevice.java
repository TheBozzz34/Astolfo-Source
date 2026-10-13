package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Cfgmgr32;
import com.sun.jna.platform.win32.Cfgmgr32Util;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32DiskDrive;
import oshi.driver.windows.wmi.Win32PnPEntity;
import oshi.driver.windows.wmi.Win32USBController;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@Immutable
public class WindowsUsbDevice extends AbstractUsbDevice {
   private static final long c = 33233266106493L;

   public WindowsUsbDevice(String var1, String var2, String var3, String var4, String var5, String var6, UsbDevice[] var7) {
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
      HashSet var4 = new HashSet();
      ArrayList var5 = new ArrayList();
      List var6 = getControllerDeviceIdList();

      for (Object var8$raw : var6) {
         String var8 = (String)var8$raw;
         putChildrenInDeviceTree(var8, 0, var3, var4);
      }

      Map var11 = populateDeviceCache(var4);

      for (Object var9$raw : var6) {
         String var9 = (String)var9$raw;
         WindowsUsbDevice var10 = getDeviceAndChildren(var9, "0000", "0000", var3, var11);
         if (var10 != null) {
            var5.add(var10);
         }
      }

      return (oshi.hardware.UsbDevice[])(var5.toArray(new WindowsUsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> var0, UsbDevice[] var1) {
      for (UsbDevice var8 : var1) {
         var0.add(
            new WindowsUsbDevice(
               var8.getName(), var8.getVendor(), var8.getVendorId(), var8.getProductId(), var8.getSerialNumber(), var8.getUniqueDeviceId(), new UsbDevice[0]
            )
         );
         addDevicesToList(var0, var8.getConnectedDevices());
      }
   }

   private static Map<String, WindowsUsbDevice> populateDeviceCache(Set<String> var0) {
      HashMap var4 = new HashMap();
      if (!var0.isEmpty()) {
         StringBuilder var5 = new StringBuilder();
         boolean var6 = true;

         for (String var8 : var0) {
            if (var6) {
               var5.append(" WHERE (PnPDeviceID=\"");
               var6 = false;
            } else {
               var5.append(" OR (PnPDeviceID=\"");
            }

            var5.append(var8).append("\")");
         }

         String var14 = var5.toString();
         WmiResult var15 = Win32PnPEntity.queryDeviceId(var14);

         for (int var9 = 0; var9 < var15.getResultCount(); var9++) {
            String var10 = WmiUtil.getString(var15, Win32PnPEntity.PnPEntityProperty.PNPDEVICEID, var9);
            String var11 = WmiUtil.getString(var15, Win32PnPEntity.PnPEntityProperty.NAME, var9);
            String var12 = WmiUtil.getString(var15, Win32PnPEntity.PnPEntityProperty.MANUFACTURER, var9);
            WindowsUsbDevice var13 = new WindowsUsbDevice(var11, var12, null, null, "", var10, new WindowsUsbDevice[0]);
            var4.put(var10, var13);
         }

         WmiResult var16 = Win32DiskDrive.queryDiskDriveId(var14);

         for (int var17 = 0; var17 < var16.getResultCount(); var17++) {
            String var18 = WmiUtil.getString(var16, Win32DiskDrive.DeviceIdProperty.PNPDEVICEID, var17);
            if (var4.containsKey(var18)) {
               WindowsUsbDevice var19 = (WindowsUsbDevice)var4.get(var18);
               var19.serialNumber = ParseUtil.hexStringToString(WmiUtil.getString(var16, Win32DiskDrive.DeviceIdProperty.SERIALNUMBER, var17));
            }
         }
      }

      return var4;
   }

   private static void putChildrenInDeviceTree(String var0, int var1, Map<String, List<String>> var2, Set<String> var3) {
      var3.add(var0);
      int var7 = var1;
      if (var7 == 0) {
         IntByReference var8 = new IntByReference();
         Cfgmgr32.INSTANCE.CM_Locate_DevNode(var8, var0, 0);
         var7 = var8.getValue();
      }

      IntByReference var13 = new IntByReference();
      if (0 == Cfgmgr32.INSTANCE.CM_Get_Child(var13, var7, 0)) {
         ArrayList var9 = new ArrayList();
         String var10 = Cfgmgr32Util.CM_Get_Device_ID(var13.getValue());
         var9.add(var10);
         var2.put(var0, var9);
         putChildrenInDeviceTree(var10, var13.getValue(), var2, var3);

         for (IntByReference var11 = new IntByReference(); 0 == Cfgmgr32.INSTANCE.CM_Get_Sibling(var11, var13.getValue(), 0); var13 = var11) {
            String var12 = Cfgmgr32Util.CM_Get_Device_ID(var11.getValue());
            ((List)var2.get(var0)).add(var12);
            putChildrenInDeviceTree(var12, var11.getValue(), var2, var3);
         }
      }
   }

   private static WindowsUsbDevice getDeviceAndChildren(
      String var0, String var1, String var2, Map<String, List<String>> var3, Map<String, WindowsUsbDevice> var4
   ) {
      String var8 = var1;
      String var9 = var2;
      Pair var10 = ParseUtil.parsePnPDeviceIdToVendorProductId(var0);
      if (var10 != null) {
         var8 = (String)var10.getA();
         var9 = (String)var10.getB();
      }

      List var11 = var3.getOrDefault(var0, new ArrayList());
      ArrayList var12 = new ArrayList();

      for (Object var14$raw : var11) {
         String var14 = (String)var14$raw;
         WindowsUsbDevice var15 = getDeviceAndChildren(var14, var8, var9, var3, var4);
         if (var15 != null) {
            var12.add(var15);
         }
      }

      Collections.sort(var12);
      if (var4.containsKey(var0)) {
         WindowsUsbDevice var16 = (WindowsUsbDevice)var4.get(var0);
         if (var16.name.isEmpty()) {
            var16.name = var8 + ":" + var9;
         }

         var16.vendorId = var8;
         var16.productId = var9;
         var16.connectedDevices = (oshi.hardware.UsbDevice[])(var12.toArray(new WindowsUsbDevice[0]));
         return var16;
      } else {
         return null;
      }
   }

   private static List<String> getControllerDeviceIdList() {
      ArrayList var3 = new ArrayList();
      WmiResult var4 = Win32USBController.queryUSBControllers();

      for (int var5 = 0; var5 < var4.getResultCount(); var5++) {
         var3.add(WmiUtil.getString(var4, Win32USBController.USBControllerProperty.PNPDEVICEID, var5));
      }

      return var3;
   }
}
