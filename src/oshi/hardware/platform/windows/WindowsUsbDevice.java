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
   public WindowsUsbDevice(
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
         addDevicesToList(arrayList, usbDevice.getConnectedDevices());
      }

      return (oshi.hardware.UsbDevice[])(arrayList.toArray(new UsbDevice[0]));
   }

   private static UsbDevice[] getUsbDevices() {
      HashMap hashMap = new HashMap();
      HashSet hashSet = new HashSet();
      ArrayList arrayList = new ArrayList();
      List list = getControllerDeviceIdList();

      for (Object object : list) {
         String string = (String)object;
         putChildrenInDeviceTree(string, 0, hashMap, hashSet);
      }

      Map map = populateDeviceCache(hashSet);

      for (Object object2 : list) {
         String string2 = (String)object2;
         WindowsUsbDevice windowsUsbDevice = getDeviceAndChildren(string2, "0000", "0000", hashMap, map);
         if (windowsUsbDevice != null) {
            arrayList.add(windowsUsbDevice);
         }
      }

      return (oshi.hardware.UsbDevice[])(arrayList.toArray(new WindowsUsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, UsbDevice[] connectedDevices) {
      for (UsbDevice usbDevice : connectedDevices) {
         deviceList.add(
            new WindowsUsbDevice(
               usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), new UsbDevice[0]
            )
         );
         addDevicesToList(deviceList, usbDevice.getConnectedDevices());
      }
   }

   private static Map<String, WindowsUsbDevice> populateDeviceCache(Set<String> devicesToAdd) {
      HashMap hashMap = new HashMap();
      if (!devicesToAdd.isEmpty()) {
         StringBuilder stringBuilder = new StringBuilder();
         boolean bl = true;

         for (String string : devicesToAdd) {
            if (bl) {
               stringBuilder.append(" WHERE (PnPDeviceID=\"");
               bl = false;
            } else {
               stringBuilder.append(" OR (PnPDeviceID=\"");
            }

            stringBuilder.append(string).append("\")");
         }

         String string2 = stringBuilder.toString();
         WmiResult wmiResult = Win32PnPEntity.queryDeviceId(string2);

         for (int i = 0; i < wmiResult.getResultCount(); i++) {
            String string3 = WmiUtil.getString(wmiResult, Win32PnPEntity.PnPEntityProperty.PNPDEVICEID, i);
            String string4 = WmiUtil.getString(wmiResult, Win32PnPEntity.PnPEntityProperty.NAME, i);
            String string5 = WmiUtil.getString(wmiResult, Win32PnPEntity.PnPEntityProperty.MANUFACTURER, i);
            WindowsUsbDevice windowsUsbDevice = new WindowsUsbDevice(string4, string5, null, null, "", string3, new WindowsUsbDevice[0]);
            hashMap.put(string3, windowsUsbDevice);
         }

         WmiResult wmiResult2 = Win32DiskDrive.queryDiskDriveId(string2);

         for (int j = 0; j < wmiResult2.getResultCount(); j++) {
            String string6 = WmiUtil.getString(wmiResult2, Win32DiskDrive.DeviceIdProperty.PNPDEVICEID, j);
            if (hashMap.containsKey(string6)) {
               WindowsUsbDevice windowsUsbDevice2 = (WindowsUsbDevice)hashMap.get(string6);
               windowsUsbDevice2.serialNumber = ParseUtil.hexStringToString(WmiUtil.getString(wmiResult2, Win32DiskDrive.DeviceIdProperty.SERIALNUMBER, j));
            }
         }
      }

      return hashMap;
   }

   private static void putChildrenInDeviceTree(String deviceId, int deviceInstance, Map<String, List<String>> deviceTreeMap, Set<String> devicesSeen) {
      devicesSeen.add(deviceId);
      int i = deviceInstance;
      if (i == 0) {
         IntByReference intByReference = new IntByReference();
         Cfgmgr32.INSTANCE.CM_Locate_DevNode(intByReference, deviceId, 0);
         i = intByReference.getValue();
      }

      IntByReference intByReference2 = new IntByReference();
      if (0 == Cfgmgr32.INSTANCE.CM_Get_Child(intByReference2, i, 0)) {
         ArrayList arrayList = new ArrayList();
         String string = Cfgmgr32Util.CM_Get_Device_ID(intByReference2.getValue());
         arrayList.add(string);
         deviceTreeMap.put(deviceId, arrayList);
         putChildrenInDeviceTree(string, intByReference2.getValue(), deviceTreeMap, devicesSeen);

         for (IntByReference intByReference3 = new IntByReference(); 0 == Cfgmgr32.INSTANCE.CM_Get_Sibling(intByReference3, intByReference2.getValue(), 0); intByReference2 = intByReference3) {
            String string2 = Cfgmgr32Util.CM_Get_Device_ID(intByReference3.getValue());
            ((List)deviceTreeMap.get(deviceId)).add(string2);
            putChildrenInDeviceTree(string2, intByReference3.getValue(), deviceTreeMap, devicesSeen);
         }
      }
   }

   private static WindowsUsbDevice getDeviceAndChildren(
      String hubDeviceId, String vid, String pid, Map<String, List<String>> deviceTreeMap, Map<String, WindowsUsbDevice> usbDeviceCache
   ) {
      String string = vid;
      String string2 = pid;
      Pair pair = ParseUtil.parsePnPDeviceIdToVendorProductId(hubDeviceId);
      if (pair != null) {
         string = (String)pair.getA();
         string2 = (String)pair.getB();
      }

      List list = deviceTreeMap.getOrDefault(hubDeviceId, new ArrayList());
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         String string3 = (String)object;
         WindowsUsbDevice windowsUsbDevice = getDeviceAndChildren(string3, string, string2, deviceTreeMap, usbDeviceCache);
         if (windowsUsbDevice != null) {
            arrayList.add(windowsUsbDevice);
         }
      }

      Collections.sort(arrayList);
      if (usbDeviceCache.containsKey(hubDeviceId)) {
         WindowsUsbDevice windowsUsbDevice2 = (WindowsUsbDevice)usbDeviceCache.get(hubDeviceId);
         if (windowsUsbDevice2.name.isEmpty()) {
            windowsUsbDevice2.name = string + ":" + string2;
         }

         windowsUsbDevice2.vendorId = string;
         windowsUsbDevice2.productId = string2;
         windowsUsbDevice2.connectedDevices = (oshi.hardware.UsbDevice[])(arrayList.toArray(new WindowsUsbDevice[0]));
         return windowsUsbDevice2;
      } else {
         return null;
      }
   }

   private static List<String> getControllerDeviceIdList() {
      ArrayList arrayList = new ArrayList();
      WmiResult wmiResult = Win32USBController.queryUSBControllers();

      for (int i = 0; i < wmiResult.getResultCount(); i++) {
         arrayList.add(WmiUtil.getString(wmiResult, Win32USBController.USBControllerProperty.PNPDEVICEID, i));
      }

      return arrayList;
   }
}
