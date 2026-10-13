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
   private static final long c = 20841872819770L;

   public FreeBsdUsbDevice(String var1, String var2, String var3, String var4, String var5, String var6, UsbDevice[] var7) {
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
            new FreeBsdUsbDevice(
               var9.getName(),
               var9.getVendor(),
               var9.getVendorId(),
               var9.getProductId(),
               var9.getSerialNumber(),
               var9.getUniqueDeviceId(),
               new FreeBsdUsbDevice[0]
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
      HashMap var8 = new HashMap();
      HashMap var9 = new HashMap();
      List var10 = ExecutingCommand.runNative("lshal");
      if (var10.isEmpty()) {
         return new FreeBsdUsbDevice[0];
      }

      String var11 = "";
      ArrayList var12 = new ArrayList();

      for (Object var14$raw : var10) {
         String var14 = (String)var14$raw;
         if (var14.startsWith("udi =")) {
            var11 = ParseUtil.getSingleQuoteStringValue(var14);
         } else if (!var11.isEmpty()) {
            var14 = var14.trim();
            if (!var14.isEmpty()) {
               if (var14.startsWith("freebsd.driver =") && "usbus".equals(ParseUtil.getSingleQuoteStringValue(var14))) {
                  var12.add(var11);
               } else if (var14.contains(".parent =")) {
                  String var15 = ParseUtil.getSingleQuoteStringValue(var14);
                  if (!var11.replace(var15, "").startsWith("_if")) {
                     var8.put(var11, var15);
                     ((java.util.List)(var9.computeIfAbsent(var15, var0 -> new ArrayList<>()))).add(var11);
                  }
               } else if (var14.contains(".product =")) {
                  var3.put(var11, ParseUtil.getSingleQuoteStringValue(var14));
               } else if (var14.contains(".vendor =")) {
                  var4.put(var11, ParseUtil.getSingleQuoteStringValue(var14));
               } else if (var14.contains(".serial =")) {
                  String var20 = ParseUtil.getSingleQuoteStringValue(var14);
                  var7.put(var11, var20.startsWith("0x") ? ParseUtil.hexStringToString(var20.replace("0x", "")) : var20);
               } else if (var14.contains(".vendor_id =")) {
                  var5.put(var11, String.format("%04x", ParseUtil.getFirstIntValue(var14)));
               } else if (var14.contains(".product_id =")) {
                  var6.put(var11, String.format("%04x", ParseUtil.getFirstIntValue(var14)));
               }
            }
         }
      }

      ArrayList var17 = new ArrayList();

      for (Object var21$raw : var12) {
         String var21 = (String)var21$raw;
         String var16 = (String)var8.get(var21);
         var9.put(var16, var9.get(var21));
         var17.add(getDeviceAndChildren(var16, "0000", "0000", var3, var4, var5, var6, var7, var9));
      }

      return (oshi.hardware.UsbDevice[])(var17.toArray(new UsbDevice[0]));
   }

   private static void addDevicesToList(List<UsbDevice> var0, UsbDevice[] var1) {
      for (UsbDevice var8 : var1) {
         var0.add(var8);
         addDevicesToList(var0, var8.getConnectedDevices());
      }
   }

   private static FreeBsdUsbDevice getDeviceAndChildren(
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
      return new FreeBsdUsbDevice(
         var3.getOrDefault(var0, var12 + ":" + var13),
         var4.getOrDefault(var0, ""),
         var12,
         var13,
         var7.getOrDefault(var0, ""),
         var0,
         (oshi.hardware.UsbDevice[])(var15.toArray(new UsbDevice[0]))
      );
   }
}
