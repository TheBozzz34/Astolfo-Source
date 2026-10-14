package oshi.hardware.platform.unix.freebsd;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdPowerSource extends AbstractPowerSource {
   public FreeBsdPowerSource(
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
      return new FreeBsdPowerSource[]{getPowerSource("BAT0")};
   }

   private static FreeBsdPowerSource getPowerSource(String name) {
      String string = name;
      double d = 1.0;
      double e = -1.0;
      double d2 = 0.0;
      int j = -1;
      double d3 = 0.0;
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
      byte b = 0;
      int k = 1;
      int l = 1;
      byte b2 = -1;
      Object object = null;
      double d4 = 0.0;
      int m = BsdSysctlUtil.sysctl("hw.acpi.battery.state", 0);
      if (m == 2) {
         bl2 = true;
      } else {
         int n = BsdSysctlUtil.sysctl("hw.acpi.battery.time", -1);
         e = n < 0 ? -1.0 : 60.0 * n;
         if (m == 1) {
            bl3 = true;
         }
      }

      int i2 = BsdSysctlUtil.sysctl("hw.acpi.battery.life", -1);
      if (i2 > 0) {
         d = i2 / 100.0;
      }

      List list = ExecutingCommand.runNative("acpiconf -i 0");
      HashMap hashMap = new HashMap();

      for (Object object2 : list) {
         String string2 = (String)object2;
         String[] strings = string2.split(":", 2);
         if (strings.length > 1) {
            String string3 = strings[1].trim();
            if (!string3.isEmpty()) {
               hashMap.put(strings[0], string3);
            }
         }
      }

      String string4 = (java.lang.String)(hashMap.getOrDefault("Model number", "unknown"));
      String string5 = (java.lang.String)(hashMap.getOrDefault("Serial number", "unknown"));
      String string6 = (java.lang.String)(hashMap.getOrDefault("Type", "unknown"));
      String string7 = (java.lang.String)(hashMap.getOrDefault("OEM info", "unknown"));
      String string8 = (String)hashMap.get("Design capacity");
      if (string8 != null) {
         l = ParseUtil.getFirstIntValue(string8);
         if (string8.toLowerCase().contains("mah")) {
            capacityUnits = PowerSource.CapacityUnits.MAH;
         } else if (string8.toLowerCase().contains("mwh")) {
            capacityUnits = PowerSource.CapacityUnits.MWH;
         }
      }

      string8 = (String)hashMap.get("Last full capacity");
      if (string8 != null) {
         k = ParseUtil.getFirstIntValue(string8);
      } else {
         k = l;
      }

      double d5 = e;
      String string9 = (String)hashMap.get("Remaining time");
      if (string9 != null) {
         String[] strings2 = string9.split(":");
         if (strings2.length == 2) {
            d5 = 3600.0 * ParseUtil.parseIntOrDefault(strings2[0], 0) + 60.0 * ParseUtil.parseIntOrDefault(strings2[1], 0);
         }
      }

      String string10 = (String)hashMap.get("Present rate");
      if (string10 != null) {
         d2 = ParseUtil.getFirstIntValue(string10);
      }

      String string11 = (String)hashMap.get("Present voltage");
      if (string11 != null) {
         j = ParseUtil.getFirstIntValue(string11);
         if (j != 0) {
            d3 = d2 / j;
         }
      }

      return new FreeBsdPowerSource(
         string,
         string4,
         d,
         e,
         d5,
         d2,
         j,
         d3,
         bl,
         bl2,
         bl3,
         capacityUnits,
         b,
         k,
         l,
         b2,
         string6,
         (LocalDate)object,
         string7,
         string5,
         d4
      );
   }
}
