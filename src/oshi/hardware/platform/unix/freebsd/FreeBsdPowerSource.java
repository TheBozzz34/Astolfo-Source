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
   private static final long e = 3870967911739L;

   public FreeBsdPowerSource(
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
      return new FreeBsdPowerSource[]{getPowerSource("BAT0")};
   }

   private static FreeBsdPowerSource getPowerSource(String var0) {
      String var4 = var0;
      double var5 = 1.0;
      double var7 = -1.0;
      double var9 = 0.0;
      int var11 = -1;
      double var12 = 0.0;
      boolean var14 = false;
      boolean var15 = false;
      boolean var16 = false;
      PowerSource.CapacityUnits var17 = PowerSource.CapacityUnits.RELATIVE;
      byte var18 = 0;
      int var19 = 1;
      int var20 = 1;
      byte var21 = -1;
      Object var22 = null;
      double var23 = 0.0;
      int var25 = BsdSysctlUtil.sysctl("hw.acpi.battery.state", 0);
      if (var25 == 2) {
         var15 = true;
      } else {
         int var26 = BsdSysctlUtil.sysctl("hw.acpi.battery.time", -1);
         var7 = var26 < 0 ? -1.0 : 60.0 * var26;
         if (var25 == 1) {
            var16 = true;
         }
      }

      int var40 = BsdSysctlUtil.sysctl("hw.acpi.battery.life", -1);
      if (var40 > 0) {
         var5 = var40 / 100.0;
      }

      List var27 = ExecutingCommand.runNative("acpiconf -i 0");
      HashMap var28 = new HashMap();

      for (Object var30$raw : var27) {
         String var30 = (String)var30$raw;
         String[] var31 = var30.split(":", 2);
         if (var31.length > 1) {
            String var32 = var31[1].trim();
            if (!var32.isEmpty()) {
               var28.put(var31[0], var32);
            }
         }
      }

      String var41 = (java.lang.String)(var28.getOrDefault("Model number", "unknown"));
      String var42 = (java.lang.String)(var28.getOrDefault("Serial number", "unknown"));
      String var43 = (java.lang.String)(var28.getOrDefault("Type", "unknown"));
      String var44 = (java.lang.String)(var28.getOrDefault("OEM info", "unknown"));
      String var33 = (String)var28.get("Design capacity");
      if (var33 != null) {
         var20 = ParseUtil.getFirstIntValue(var33);
         if (var33.toLowerCase().contains("mah")) {
            var17 = PowerSource.CapacityUnits.MAH;
         } else if (var33.toLowerCase().contains("mwh")) {
            var17 = PowerSource.CapacityUnits.MWH;
         }
      }

      var33 = (String)var28.get("Last full capacity");
      if (var33 != null) {
         var19 = ParseUtil.getFirstIntValue(var33);
      } else {
         var19 = var20;
      }

      double var34 = var7;
      String var36 = (String)var28.get("Remaining time");
      if (var36 != null) {
         String[] var37 = var36.split(":");
         if (var37.length == 2) {
            var34 = 3600.0 * ParseUtil.parseIntOrDefault(var37[0], 0) + 60.0 * ParseUtil.parseIntOrDefault(var37[1], 0);
         }
      }

      String var46 = (String)var28.get("Present rate");
      if (var46 != null) {
         var9 = ParseUtil.getFirstIntValue(var46);
      }

      String var38 = (String)var28.get("Present voltage");
      if (var38 != null) {
         var11 = ParseUtil.getFirstIntValue(var38);
         if (var11 != 0) {
            var12 = var9 / var11;
         }
      }

      return new FreeBsdPowerSource(
         var4,
         var41,
         var5,
         var7,
         var34,
         var9,
         var11,
         var12,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var21,
         var43,
         (LocalDate)var22,
         var44,
         var42,
         var23
      );
   }
}
