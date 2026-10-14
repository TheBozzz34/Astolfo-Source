package oshi.hardware.platform.linux;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public final class LinuxPowerSource extends AbstractPowerSource {
   public LinuxPowerSource(
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

   public static PowerSource[] x() {
      double d = -1.0;
      double e = -1.0;
      double d2 = -1.0;
      double d3 = 0.0;
      double d4 = -1.0;
      double d5 = 0.0;
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
      int i = -1;
      int j = -1;
      byte b = -1;
      int k = -1;
      Object object = null;
      double d6 = 0.0;
      File file = new File("/sys/class/power_supply/");
      String[] strings = file.list();
      if (strings == null) {
         strings = new String[0];
      }

      ArrayList arrayList = new ArrayList(strings.length);

      for (String string : strings) {
         if (!string.startsWith("ADP") && !string.startsWith("AC")) {
            List list2 = FileUtil.readFile("/sys/class/power_supply/" + string + "/uevent", false);
            if (!list2.isEmpty()) {
               HashMap hashMap = new HashMap();

               for (Object object2 : list2) {
                  String string2 = (String)object2;
                  String[] strings2 = string2.split("=");
                  if (strings2.length > 1 && !strings2[1].isEmpty()) {
                     hashMap.put(strings2[0], strings2[1]);
                  }
               }

               String string3 = (java.lang.String)(hashMap.getOrDefault("POWER_SUPPLY_NAME", string));
               String string4 = (String)hashMap.get("POWER_SUPPLY_STATUS");
               bl2 = "Charging".equals(string4);
               bl3 = "Discharging".equals(string4);
               if (hashMap.containsKey("POWER_SUPPLY_CAPACITY")) {
                  d = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CAPACITY"), -100) / 100.0;
               }

               if (hashMap.containsKey("POWER_SUPPLY_ENERGY_NOW")) {
                  i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_ENERGY_NOW"), -1);
               } else if (hashMap.containsKey("POWER_SUPPLY_CHARGE_NOW")) {
                  i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CHARGE_NOW"), -1);
               }

               if (hashMap.containsKey("POWER_SUPPLY_ENERGY_FULL")) {
                  i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_ENERGY_FULL"), 1);
               } else if (hashMap.containsKey("POWER_SUPPLY_CHARGE_FULL")) {
                  i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CHARGE_FULL"), 1);
               }

               if (hashMap.containsKey("POWER_SUPPLY_ENERGY_FULL_DESIGN")) {
                  j = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
               } else if (hashMap.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN")) {
                  j = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1);
               }

               if (hashMap.containsKey("POWER_SUPPLY_VOLTAGE_NOW")) {
                  d4 = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_VOLTAGE_NOW"), -1);
               }

               if (hashMap.containsKey("POWER_SUPPLY_POWER_NOW")) {
                  d3 = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_POWER_NOW"), -1);
               }

               if (d4 > 0.0) {
                  d5 = d3 / d4;
               }

               if (hashMap.containsKey("POWER_SUPPLY_CYCLE_COUNT")) {
                  k = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CYCLE_COUNT"), -1);
               }

               String string5 = (java.lang.String)(hashMap.getOrDefault("POWER_SUPPLY_TECHNOLOGY", "unknown"));
               String string6 = (java.lang.String)(hashMap.getOrDefault("POWER_SUPPLY_MODEL_NAME", "unknown"));
               String string7 = (java.lang.String)(hashMap.getOrDefault("POWER_SUPPLY_MANUFACTURER", "unknown"));
               String string8 = (java.lang.String)(hashMap.getOrDefault("POWER_SUPPLY_SERIAL_NUMBER", "unknown"));
               if (ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_PRESENT"), 1) > 0) {
                  arrayList.add(
                     new LinuxPowerSource(
                        string3,
                        string6,
                        d,
                        e,
                        d2,
                        d3,
                        d4,
                        d5,
                        bl,
                        bl2,
                        bl3,
                        capacityUnits,
                        i,
                        j,
                        b,
                        k,
                        string5,
                        (LocalDate)object,
                        string7,
                        string8,
                        d6
                     )
                  );
               }
            }
         }
      }

      return (oshi.hardware.PowerSource[])(arrayList.toArray(new LinuxPowerSource[0]));
   }
}
