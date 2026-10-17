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
   private static final int BATTERY_SYSTEM_BATTERY = Integer.MIN_VALUE;
   private static final int y = 536870912;
   private static final int BATTERY_DISCHARGING = 2;
   private static final int BATTERY_CHARGING = 4;
   private static final int BATTERY_CAPACITY_RELATIVE = 1073741824;
   private static final int IOCTL_BATTERY_QUERY_TAG = 2703424;
   private static final int IOCTL_BATTERY_QUERY_STATUS = 2703436;
   private static final int IOCTL_BATTERY_QUERY_INFORMATION = 2703428;

   public WindowsPowerSource(
      String psName,
      String psDeviceName,
      double psRemainingCapacityPercent,
      double psTimeRemainingEstimated,
      double psTimeRemainingInstant,
      double psPowerUsageRate,
      double psVoltage,
      double psAmperage,
      boolean psPowerOnLine,
      boolean psCharging,
      boolean psDischarging,
      PowerSource.CapacityUnits psCapacityUnits,
      int psCurrentCapacity,
      int psMaxCapacity,
      int psDesignCapacity,
      int psCycleCount,
      String psChemistry,
      LocalDate psManufactureDate,
      String psManufacturer,
      String psSerialNumber,
      double psTemperature
   ) {
      super(
         psName,
         psDeviceName,
         psRemainingCapacityPercent,
         psTimeRemainingEstimated,
         psTimeRemainingInstant,
         psPowerUsageRate,
         psVoltage,
         psAmperage,
         psPowerOnLine,
         psCharging,
         psDischarging,
         psCapacityUnits,
         psCurrentCapacity,
         psMaxCapacity,
         psDesignCapacity,
         psCycleCount,
         psChemistry,
         psManufactureDate,
         psManufacturer,
         psSerialNumber,
         psTemperature
      );
   }

   public static PowerSource[] getPowerSources() {
      return new WindowsPowerSource[]{getPowerSource("System Battery")};
   }

   private static WindowsPowerSource getPowerSource(String name) {
      String string = name;
      String string2 = "unknown";
      double d = 1.0;
      double e = -1.0;
      double d2 = 0.0;
      int i = 0;
      double d3 = -1.0;
      double d4 = 0.0;
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
      int j = 0;
      int k = 1;
      int l = 1;
      int m = -1;
      String string3 = "unknown";
      LocalDate localDate = null;
      String string4 = "unknown";
      String string5 = "unknown";
      double d5 = 0.0;
      int n = new PowrProf.SystemBatteryState().size();
      Memory memory = new Memory(n);
      if (0 == PowrProf.INSTANCE.CallNtPowerInformation(5, null, 0, memory, n)) {
         PowrProf.SystemBatteryState systemBatteryState = new PowrProf.SystemBatteryState(memory);
         if (systemBatteryState.batteryPresent > 0) {
            if (systemBatteryState.acOnLine == 0 && systemBatteryState.charging == 0 && systemBatteryState.discharging > 0) {
               e = systemBatteryState.estimatedTime;
            } else if (systemBatteryState.charging > 0) {
               e = -2.0;
            }

            k = systemBatteryState.maxCapacity;
            j = systemBatteryState.remainingCapacity;
            d = Math.min(1.0, (double)j / k);
            i = systemBatteryState.rate;
         }
      }

      HANDLE handle = SetupApi.INSTANCE.SetupDiGetClassDevs(GUID_DEVCLASS_BATTERY, null, null, 18);
      if (WinBase.INVALID_HANDLE_VALUE != handle) {
         for (int i2 = 0; i2 < 100; i2++) {
            SP_DEVICE_INTERFACE_DATA sP_DEVICE_INTERFACE_DATA = new SP_DEVICE_INTERFACE_DATA();
            sP_DEVICE_INTERFACE_DATA.cbSize = sP_DEVICE_INTERFACE_DATA.size();
            if (SetupApi.INSTANCE.SetupDiEnumDeviceInterfaces(handle, null, GUID_DEVCLASS_BATTERY, i2, sP_DEVICE_INTERFACE_DATA)) {
               IntByReference intByReference = new IntByReference(0);
               SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(handle, sP_DEVICE_INTERFACE_DATA, null, 0, intByReference, null);
               if (122 == Kernel32.INSTANCE.GetLastError()) {
                  Memory memory2 = new Memory(intByReference.getValue());
                  memory2.setInt(0L, 4 + (X64 ? 4 : CHAR_WIDTH));
                  if (SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(handle, sP_DEVICE_INTERFACE_DATA, memory2, (int)memory2.size(), intByReference, null)) {
                     String string6 = CHAR_WIDTH > 1 ? memory2.getWideString(4L) : memory2.getString(4L);
                     HANDLE handle2 = Kernel32.INSTANCE.CreateFile(string6, -1073741824, 3, null, 3, 128, null);
                     if (!WinBase.INVALID_HANDLE_VALUE.equals(handle2)) {
                        PowrProf.BATTERY_QUERY_INFORMATION batterY_QUERY_INFORMATION = new PowrProf.BATTERY_QUERY_INFORMATION();
                        IntByReference intByReference2 = new IntByReference(0);
                        IntByReference intByReference3 = new IntByReference();
                        IntByReference intByReference4 = new IntByReference();
                        if (Kernel32.INSTANCE.DeviceIoControl(handle2, 2703424, intByReference2.getPointer(), 4, intByReference3.getPointer(), 4, intByReference4, null)) {
                           batterY_QUERY_INFORMATION.BatteryTag = intByReference3.getValue();
                           if (batterY_QUERY_INFORMATION.BatteryTag > 0) {
                              batterY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryInformation.ordinal();
                              batterY_QUERY_INFORMATION.write();
                              PowrProf.BATTERY_INFORMATION batterY_INFORMATION = new PowrProf.BATTERY_INFORMATION();
                              if (Kernel32.INSTANCE
                                 .DeviceIoControl(handle2, 2703428, batterY_QUERY_INFORMATION.getPointer(), batterY_QUERY_INFORMATION.size(), batterY_INFORMATION.getPointer(), batterY_INFORMATION.size(), intByReference4, null)) {
                                 batterY_INFORMATION.read();
                                 if (0 != (batterY_INFORMATION.Capabilities & -2147483648) && 0 == (batterY_INFORMATION.Capabilities & 536870912)) {
                                    if (0 == (batterY_INFORMATION.Capabilities & 1073741824)) {
                                       capacityUnits = PowerSource.CapacityUnits.MWH;
                                    }

                                    string3 = new String(batterY_INFORMATION.Chemistry, StandardCharsets.US_ASCII);
                                    l = batterY_INFORMATION.DesignedCapacity;
                                    k = batterY_INFORMATION.FullChargedCapacity;
                                    m = batterY_INFORMATION.CycleCount;
                                    PowrProf.BATTERY_WAIT_STATUS batterY_WAIT_STATUS = new PowrProf.BATTERY_WAIT_STATUS();
                                    batterY_WAIT_STATUS.BatteryTag = batterY_QUERY_INFORMATION.BatteryTag;
                                    batterY_WAIT_STATUS.write();
                                    PowrProf.BATTERY_STATUS batterY_STATUS = new PowrProf.BATTERY_STATUS();
                                    if (Kernel32.INSTANCE
                                       .DeviceIoControl(handle2, 2703436, batterY_WAIT_STATUS.getPointer(), batterY_WAIT_STATUS.size(), batterY_STATUS.getPointer(), batterY_STATUS.size(), intByReference4, null)) {
                                       batterY_STATUS.read();
                                       if (0 != (batterY_STATUS.PowerState & 1)) {
                                          bl = true;
                                       }

                                       if (0 != (batterY_STATUS.PowerState & 2)) {
                                          bl3 = true;
                                       }

                                       if (0 != (batterY_STATUS.PowerState & 4)) {
                                          bl2 = true;
                                       }

                                       j = batterY_STATUS.Capacity;
                                       d3 = batterY_STATUS.Voltage > 0 ? batterY_STATUS.Voltage / 1000.0 : batterY_STATUS.Voltage;
                                       i = batterY_STATUS.Rate;
                                       if (d3 > 0.0) {
                                          d4 = i / d3;
                                       }
                                    }
                                 }

                                 string2 = batteryQueryString(handle2, intByReference3.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryDeviceName.ordinal());
                                 string4 = batteryQueryString(handle2, intByReference3.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureName.ordinal());
                                 string5 = batteryQueryString(handle2, intByReference3.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatterySerialNumber.ordinal());
                                 batterY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureDate.ordinal();
                                 batterY_QUERY_INFORMATION.write();
                                 PowrProf.BATTERY_MANUFACTURE_DATE batterY_MANUFACTURE_DATE = new PowrProf.BATTERY_MANUFACTURE_DATE();
                                 if (Kernel32.INSTANCE
                                    .DeviceIoControl(handle2, 2703428, batterY_QUERY_INFORMATION.getPointer(), batterY_QUERY_INFORMATION.size(), batterY_MANUFACTURE_DATE.getPointer(), batterY_MANUFACTURE_DATE.size(), intByReference4, null)) {
                                    batterY_MANUFACTURE_DATE.read();
                                    if (batterY_MANUFACTURE_DATE.Year > 1900 && batterY_MANUFACTURE_DATE.Month > 0 && batterY_MANUFACTURE_DATE.Day > 0) {
                                       localDate = LocalDate.of(batterY_MANUFACTURE_DATE.Year, batterY_MANUFACTURE_DATE.Month, batterY_MANUFACTURE_DATE.Day);
                                    }
                                 }

                                 batterY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryTemperature.ordinal();
                                 batterY_QUERY_INFORMATION.write();
                                 IntByReference intByReference5 = new IntByReference();
                                 if (Kernel32.INSTANCE.DeviceIoControl(handle2, 2703428, batterY_QUERY_INFORMATION.getPointer(), batterY_QUERY_INFORMATION.size(), intByReference5.getPointer(), 4, intByReference4, null)) {
                                    d5 = intByReference5.getValue() / 10.0 - 273.15;
                                 }

                                 batterY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryEstimatedTime.ordinal();
                                 if (i != 0) {
                                    batterY_QUERY_INFORMATION.AtRate = i;
                                 }

                                 batterY_QUERY_INFORMATION.write();
                                 IntByReference intByReference6 = new IntByReference();
                                 if (Kernel32.INSTANCE.DeviceIoControl(handle2, 2703428, batterY_QUERY_INFORMATION.getPointer(), batterY_QUERY_INFORMATION.size(), intByReference6.getPointer(), 4, intByReference4, null)) {
                                    d2 = intByReference6.getValue();
                                 }

                                 if (d2 < 0.0 && i != 0) {
                                    d2 = (k - j) * 3600.0 / i;
                                    if (d2 < 0.0) {
                                       d2 *= -1.0;
                                    }
                                 }
                                 break;
                              }
                           }
                        }

                        Kernel32.INSTANCE.CloseHandle(handle2);
                     }
                  }
               }
            } else if (259 == Kernel32.INSTANCE.GetLastError()) {
               break;
            }
         }

         SetupApi.INSTANCE.SetupDiDestroyDeviceInfoList(handle);
      }

      return new WindowsPowerSource(
         string, string2, d, e, d2, i, d3, d4, bl, bl2, bl3, capacityUnits, j, k, l, m, string3, localDate, string4, string5, d5
      );
   }

   private static String batteryQueryString(HANDLE hBattery, int tag, int infoLevel) {
      PowrProf.BATTERY_QUERY_INFORMATION batterY_QUERY_INFORMATION = new PowrProf.BATTERY_QUERY_INFORMATION();
      batterY_QUERY_INFORMATION.BatteryTag = tag;
      batterY_QUERY_INFORMATION.InformationLevel = infoLevel;
      batterY_QUERY_INFORMATION.write();
      IntByReference intByReference = new IntByReference();
      boolean bl = false;
      long l = 0L;

      Memory memory;
      do {
         l += 256L;
         memory = new Memory(l);
         bl = Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, batterY_QUERY_INFORMATION.getPointer(), batterY_QUERY_INFORMATION.size(), memory, (int)memory.size(), intByReference, null);
      } while (!bl && l < 4096L);

      return CHAR_WIDTH > 1 ? memory.getWideString(0L) : memory.getString(0L);
   }
}
