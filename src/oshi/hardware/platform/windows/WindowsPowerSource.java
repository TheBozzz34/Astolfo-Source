package oshi.hardware.platform.windows;

import com.sun.jna.Memory;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.jna.platform.windows.PowrProf;

@ThreadSafe
public final class WindowsPowerSource extends AbstractPowerSource {
   private static final GUID GUID_DEVCLASS_BATTERY = GUID.fromString("{72631E54-78A4-11D0-BCF7-00AA00B7B32A}");
   private static final int CHAR_WIDTH = W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? 2 : 1;
   private static final boolean X64 = Platform.is64Bit();
   private static final int I = Integer.MIN_VALUE;
   private static final int y = 536870912;
   private static final int q = 2;
   private static final int i = 4;
   private static final int e = 1073741824;
   private static final int f = 2703424;
   private static final int J = 2703436;
   private static final int t = 2703428;
   private static final long h = 133014970210929L;

   public WindowsPowerSource(
      String var1,
      String var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      boolean var15,
      boolean var16,
      boolean var17,
      PowerSource.CapacityUnits var18,
      int var19,
      int var20,
      int var21,
      int var22,
      String var23,
      LocalDate var24,
      String var25,
      String var26,
      double var27
   ) {
      super(var1, var2, var3, var5, var7, var9, var11, var13, var15, var16, var17, var18, var19, var20, var21, var22, var23, var24, var25, var26, var27);
   }

   public static PowerSource[] getPowerSources() {
      return new WindowsPowerSource[]{getPowerSource("System Battery")};
   }

   private static WindowsPowerSource getPowerSource(String var0) {
      long var1 = h ^ 128484759881403L;
      String var4 = var0;
      String var5 = "unknown";
      double var6 = 1.0;
      double var8 = -1.0;
      double var10 = 0.0;
      int var12 = 0;
      double var13 = -1.0;
      double var15 = 0.0;
      boolean var17 = false;
      boolean var18 = false;
      boolean var19 = false;
      PowerSource.CapacityUnits var20 = PowerSource.CapacityUnits.RELATIVE;
      int var21 = 0;
      int var22 = 1;
      int var23 = 1;
      int var24 = -1;
      String var25 = "unknown";
      LocalDate var26 = null;
      String var27 = "unknown";
      String var28 = "unknown";
      double var29 = 0.0;
      int var31 = new PowrProf.SystemBatteryState().size();
      Memory var32 = new Memory(var31);
      if (0 == PowrProf.INSTANCE.CallNtPowerInformation(5, null, 0, var32, var31)) {
         PowrProf.SystemBatteryState var33 = new PowrProf.SystemBatteryState(var32);
         if (var33.batteryPresent > 0) {
            if (var33.acOnLine == 0 && var33.charging == 0 && var33.discharging > 0) {
               var8 = var33.estimatedTime;
            } else if (var33.charging > 0) {
               var8 = -2.0;
            }

            var22 = var33.maxCapacity;
            var21 = var33.remainingCapacity;
            var6 = Math.min(1.0, (double)var21 / var22);
            var12 = var33.rate;
         }
      }

      HANDLE var48 = SetupApi.INSTANCE.SetupDiGetClassDevs(GUID_DEVCLASS_BATTERY, null, null, 18);
      if (WinBase.INVALID_HANDLE_VALUE != var48) {
         for (int var34 = 0; var34 < 100; var34++) {
            SP_DEVICE_INTERFACE_DATA var35 = new SP_DEVICE_INTERFACE_DATA();
            var35.cbSize = var35.size();
            if (SetupApi.INSTANCE.SetupDiEnumDeviceInterfaces(var48, null, GUID_DEVCLASS_BATTERY, var34, var35)) {
               IntByReference var36 = new IntByReference(0);
               SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(var48, var35, null, 0, var36, null);
               long var10000 = 3428640691553928165L ^ var1;
               if (122 == Kernel32.INSTANCE.GetLastError()) {
                  Memory var37 = new Memory(var36.getValue());
                  var37.setInt(0L, 4 + (X64 ? 4 : CHAR_WIDTH));
                  if (SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(var48, var35, var37, (int)var37.size(), var36, null)) {
                     String var38 = CHAR_WIDTH > 1 ? var37.getWideString(4L) : var37.getString(4L);
                     HANDLE var39 = Kernel32.INSTANCE.CreateFile(var38, -1073741824, 3, null, 3, 128, null);
                     if (!WinBase.INVALID_HANDLE_VALUE.equals(var39)) {
                        PowrProf.BATTERY_QUERY_INFORMATION var40 = new PowrProf.BATTERY_QUERY_INFORMATION();
                        IntByReference var41 = new IntByReference(0);
                        IntByReference var42 = new IntByReference();
                        IntByReference var43 = new IntByReference();
                        if (Kernel32.INSTANCE.DeviceIoControl(var39, 2703424, var41.getPointer(), 4, var42.getPointer(), 4, var43, null)) {
                           var40.BatteryTag = var42.getValue();
                           if (var40.BatteryTag > 0) {
                              var40.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryInformation.ordinal();
                              var40.write();
                              PowrProf.BATTERY_INFORMATION var44 = new PowrProf.BATTERY_INFORMATION();
                              if (Kernel32.INSTANCE
                                 .DeviceIoControl(var39, 2703428, var40.getPointer(), var40.size(), var44.getPointer(), var44.size(), var43, null)) {
                                 var44.read();
                                 if (0 != (var44.Capabilities & -2147483648) && 0 == (var44.Capabilities & 536870912)) {
                                    if (0 == (var44.Capabilities & 1073741824)) {
                                       var20 = PowerSource.CapacityUnits.MWH;
                                    }

                                    var25 = new String(var44.Chemistry, StandardCharsets.US_ASCII);
                                    var23 = var44.DesignedCapacity;
                                    var22 = var44.FullChargedCapacity;
                                    var24 = var44.CycleCount;
                                    PowrProf.BATTERY_WAIT_STATUS var45 = new PowrProf.BATTERY_WAIT_STATUS();
                                    var45.BatteryTag = var40.BatteryTag;
                                    var45.write();
                                    PowrProf.BATTERY_STATUS var46 = new PowrProf.BATTERY_STATUS();
                                    if (Kernel32.INSTANCE
                                       .DeviceIoControl(var39, 2703436, var45.getPointer(), var45.size(), var46.getPointer(), var46.size(), var43, null)) {
                                       var46.read();
                                       if (0 != (var46.PowerState & 1)) {
                                          var17 = true;
                                       }

                                       if (0 != (var46.PowerState & 2)) {
                                          var19 = true;
                                       }

                                       if (0 != (var46.PowerState & 4)) {
                                          var18 = true;
                                       }

                                       var21 = var46.Capacity;
                                       var13 = var46.Voltage > 0 ? var46.Voltage / 1000.0 : var46.Voltage;
                                       var12 = var46.Rate;
                                       if (var13 > 0.0) {
                                          var15 = var12 / var13;
                                       }
                                    }
                                 }

                                 var5 = batteryQueryString(var39, var42.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryDeviceName.ordinal());
                                 var27 = batteryQueryString(var39, var42.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureName.ordinal());
                                 var28 = batteryQueryString(var39, var42.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatterySerialNumber.ordinal());
                                 var40.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureDate.ordinal();
                                 var40.write();
                                 PowrProf.BATTERY_MANUFACTURE_DATE var49 = new PowrProf.BATTERY_MANUFACTURE_DATE();
                                 if (Kernel32.INSTANCE
                                    .DeviceIoControl(var39, 2703428, var40.getPointer(), var40.size(), var49.getPointer(), var49.size(), var43, null)) {
                                    var49.read();
                                    if (var49.Year > 1900 && var49.Month > 0 && var49.Day > 0) {
                                       var26 = LocalDate.of(var49.Year, var49.Month, var49.Day);
                                    }
                                 }

                                 var40.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryTemperature.ordinal();
                                 var40.write();
                                 IntByReference var50 = new IntByReference();
                                 if (Kernel32.INSTANCE.DeviceIoControl(var39, 2703428, var40.getPointer(), var40.size(), var50.getPointer(), 4, var43, null)) {
                                    var29 = var50.getValue() / 10.0 - 273.15;
                                 }

                                 var40.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryEstimatedTime.ordinal();
                                 if (var12 != 0) {
                                    var40.AtRate = var12;
                                 }

                                 var40.write();
                                 IntByReference var47 = new IntByReference();
                                 if (Kernel32.INSTANCE.DeviceIoControl(var39, 2703428, var40.getPointer(), var40.size(), var47.getPointer(), 4, var43, null)) {
                                    var10 = var47.getValue();
                                 }

                                 if (var10 < 0.0 && var12 != 0) {
                                    var10 = (var22 - var21) * 3600.0 / var12;
                                    if (var10 < 0.0) {
                                       var10 *= -1.0;
                                    }
                                 }
                                 break;
                              }
                           }
                        }

                        Kernel32.INSTANCE.CloseHandle(var39);
                     }
                  }
               }
            } else if (259 == Kernel32.INSTANCE.GetLastError()) {
               break;
            }
         }

         SetupApi.INSTANCE.SetupDiDestroyDeviceInfoList(var48);
      }

      return new WindowsPowerSource(
         var4, var5, var6, var8, var10, var12, var13, var15, var17, var18, var19, var20, var21, var22, var23, var24, var25, var26, var27, var28, var29
      );
   }

   private static String batteryQueryString(HANDLE var0, int var1, int var2) {
      PowrProf.BATTERY_QUERY_INFORMATION var6 = new PowrProf.BATTERY_QUERY_INFORMATION();
      var6.BatteryTag = var1;
      var6.InformationLevel = var2;
      var6.write();
      IntByReference var7 = new IntByReference();
      boolean var8 = false;
      long var9 = 0L;

      Memory var11;
      do {
         var9 += 256L;
         var11 = new Memory(var9);
         var8 = Kernel32.INSTANCE.DeviceIoControl(var0, 2703428, var6.getPointer(), var6.size(), var11, (int)var11.size(), var7, null);
      } while (!var8 && var9 < 4096L);

      return CHAR_WIDTH > 1 ? var11.getWideString(0L) : var11.getString(0L);
   }

   static {
      long var20 = h ^ 126136256595451L;
      long var10000 = var20 ^ 7761713442717934053L;
      var10000 = var20 ^ 7838068076569233102L;
      var10000 = var20 ^ 6335743634963345783L;
      var10000 = var20 ^ 4279477659759214889L;
      var10000 = var20 ^ 5420189662427001838L;
      var10000 = var20 ^ 1818520588667256052L;
      var10000 = var20 ^ 8805963225893806249L;
      var10000 = var20 ^ 6237680454327634458L;
   }
}
