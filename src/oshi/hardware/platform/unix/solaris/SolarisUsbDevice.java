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
   private static final long c = 94980161754475L;
   private static final long q = -2037229415150125048L;

   public SolarisUsbDevice(String var1, String var2, String var3, String var4, String var5, String var6, UsbDevice[] var7) {
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
            new SolarisUsbDevice(
               var9.getName(),
               var9.getVendor(),
               var9.getVendorId(),
               var9.getProductId(),
               var9.getSerialNumber(),
               var9.getUniqueDeviceId(),
               new SolarisUsbDevice[0]
            )
         );
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
      List var8 = ExecutingCommand.runNative("prtconf -pv");
      if (var8.isEmpty()) {
         return new SolarisUsbDevice[0];
      }

      HashMap var9 = new HashMap();
      String var10 = "";
      int var11 = 0;
      ArrayList var12 = new ArrayList();

      for (Object var14$raw : var8) {
         String var14 = (String)var14$raw;
         if (var14.contains("Node 0x")) {
            var10 = var14.replaceFirst("^\\s*", "");
            int var15 = var14.length() - var10.length();
            if (var11 == 0) {
               var11 = var15;
            }

            var9.put(var15, var10);
            if (var15 > var11) {
               ((java.util.List)(var6.computeIfAbsent(var9.get(var15 - var11), var0 -> new ArrayList<>()))).add(var10);
            } else {
               var12.add(var10);
            }
         } else if (!var10.isEmpty()) {
            var14 = var14.trim();
            if (var14.startsWith("model:")) {
               var3.put(var10, ParseUtil.getSingleQuoteStringValue(var14));
            } else if (var14.startsWith("name:")) {
               var3.putIfAbsent(var10, ParseUtil.getSingleQuoteStringValue(var14));
            } else if (var14.startsWith("vendor-id:")) {
               var4.put(var10, var14.substring(var14.length() - 4));
            } else if (var14.startsWith("device-id:")) {
               var5.put(var10, var14.substring(var14.length() - 4));
            } else if (var14.startsWith("class-code:")) {
               var7.putIfAbsent(var10, var14.substring(var14.length() - (int)q, var14.length() - 4));
            } else if (var14.startsWith("device_type:")) {
               var7.putIfAbsent(var10, ParseUtil.getSingleQuoteStringValue(var14));
            }
         }
      }

      ArrayList var16 = new ArrayList();

      for (Object var19$raw : var12) {
         String var19 = (String)var19$raw;
         if ("000c".equals(var7.getOrDefault(var19, "")) || "usb".equals(var7.getOrDefault(var19, ""))) {
            var16.add(getDeviceAndChildren(var19, "0000", "0000", var3, var4, var5, var6));
         }
      }

      return (oshi.hardware.UsbDevice[])(var16.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> var0, UsbDevice[] var1) {
      for (UsbDevice var8 : var1) {
         var0.add(var8);
         addDevicesToList(var0, var8.getConnectedDevices());
      }
   }

   private static SolarisUsbDevice getDeviceAndChildren(
      String var0, String var1, String var2, Map<String, String> var3, Map<String, String> var4, Map<String, String> var5, Map<String, List<String>> var6
   ) {
      String var10 = var4.getOrDefault(var0, var1);
      String var11 = var5.getOrDefault(var0, var2);
      List var12 = var6.getOrDefault(var0, new ArrayList());
      ArrayList var13 = new ArrayList();

      for (Object var15$raw : var12) {
         String var15 = (String)var15$raw;
         var13.add(getDeviceAndChildren(var15, var10, var11, var3, var4, var5, var6));
      }

      Collections.sort(var13);
      return new SolarisUsbDevice(var3.getOrDefault(var0, var10 + ":" + var11), "", var10, var11, "", var0, (oshi.hardware.UsbDevice[])(var13.toArray(new UsbDevice[0])));
   }
}
