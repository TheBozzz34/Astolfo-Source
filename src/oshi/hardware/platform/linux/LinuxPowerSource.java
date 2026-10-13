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
   private static final long e = 101142788395625L;
   private static final long n = -4575320590461698148L;

   public LinuxPowerSource(
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

   public static PowerSource[] x() {
      double var6 = -1.0;
      double var8 = -1.0;
      double var10 = -1.0;
      double var12 = 0.0;
      double var14 = -1.0;
      double var16 = 0.0;
      boolean var18 = false;
      boolean var19 = false;
      boolean var20 = false;
      PowerSource.CapacityUnits var21 = PowerSource.CapacityUnits.RELATIVE;
      int var22 = -1;
      int var23 = -1;
      byte var24 = -1;
      int var25 = -1;
      Object var27 = null;
      double var30 = 0.0;
      File var32 = new File("/sys/class/power_supply/");
      String[] var33 = var32.list();
      if (var33 == null) {
         var33 = new String[0];
      }

      ArrayList var34 = new ArrayList(var33.length);

      for (String var38 : var33) {
         if (!var38.startsWith("ADP") && !var38.startsWith("AC")) {
            List var39 = FileUtil.readFile("/sys/class/power_supply/" + var38 + "/uevent", false);
            if (!var39.isEmpty()) {
               HashMap var40 = new HashMap();

               for (Object var42$raw : var39) {
                  String var42 = (String)var42$raw;
                  String[] var43 = var42.split("=");
                  if (var43.length > 1 && !var43[1].isEmpty()) {
                     var40.put(var43[0], var43[1]);
                  }
               }

               String var4 = (java.lang.String)(var40.getOrDefault("POWER_SUPPLY_NAME", var38));
               String var46 = (String)var40.get("POWER_SUPPLY_STATUS");
               var19 = "Charging".equals(var46);
               var20 = "Discharging".equals(var46);
               if (var40.containsKey("POWER_SUPPLY_CAPACITY")) {
                  var6 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_CAPACITY"), (int)n) / 100.0;
               }

               if (var40.containsKey("POWER_SUPPLY_ENERGY_NOW")) {
                  var22 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_ENERGY_NOW"), -1);
               } else if (var40.containsKey("POWER_SUPPLY_CHARGE_NOW")) {
                  var22 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_CHARGE_NOW"), -1);
               }

               if (var40.containsKey("POWER_SUPPLY_ENERGY_FULL")) {
                  var22 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_ENERGY_FULL"), 1);
               } else if (var40.containsKey("POWER_SUPPLY_CHARGE_FULL")) {
                  var22 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_CHARGE_FULL"), 1);
               }

               if (var40.containsKey("POWER_SUPPLY_ENERGY_FULL_DESIGN")) {
                  var23 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
               } else if (var40.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN")) {
                  var23 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1);
               }

               if (var40.containsKey("POWER_SUPPLY_VOLTAGE_NOW")) {
                  var14 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_VOLTAGE_NOW"), -1);
               }

               if (var40.containsKey("POWER_SUPPLY_POWER_NOW")) {
                  var12 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_POWER_NOW"), -1);
               }

               if (var14 > 0.0) {
                  var16 = var12 / var14;
               }

               if (var40.containsKey("POWER_SUPPLY_CYCLE_COUNT")) {
                  var25 = ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_CYCLE_COUNT"), -1);
               }

               String var26 = (java.lang.String)(var40.getOrDefault("POWER_SUPPLY_TECHNOLOGY", "unknown"));
               String var5 = (java.lang.String)(var40.getOrDefault("POWER_SUPPLY_MODEL_NAME", "unknown"));
               String var28 = (java.lang.String)(var40.getOrDefault("POWER_SUPPLY_MANUFACTURER", "unknown"));
               String var29 = (java.lang.String)(var40.getOrDefault("POWER_SUPPLY_SERIAL_NUMBER", "unknown"));
               if (ParseUtil.parseIntOrDefault((String)var40.get("POWER_SUPPLY_PRESENT"), 1) > 0) {
                  var34.add(
                     new LinuxPowerSource(
                        var4,
                        var5,
                        var6,
                        var8,
                        var10,
                        var12,
                        var14,
                        var16,
                        var18,
                        var19,
                        var20,
                        var21,
                        var22,
                        var23,
                        var24,
                        var25,
                        var26,
                        (LocalDate)var27,
                        var28,
                        var29,
                        var30
                     )
                  );
               }
            }
         }
      }

      return (oshi.hardware.PowerSource[])(var34.toArray(new LinuxPowerSource[0]));
   }
}
