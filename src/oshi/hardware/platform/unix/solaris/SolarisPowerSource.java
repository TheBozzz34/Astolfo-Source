package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import java.time.LocalDate;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisPowerSource extends AbstractPowerSource {
   private static final String[] KSTAT_BATT_MOD = new String[]{null, "battery", "acpi_drv"};
   private static final int KSTAT_BATT_IDX;
   private static final long e = 104726701934365L;

   public SolarisPowerSource(
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
      return new SolarisPowerSource[]{getPowerSource("BAT0")};
   }

   private static SolarisPowerSource getPowerSource(String var0) {
      String var4 = var0;
      String var5 = "unknown";
      double var6 = 1.0;
      double var8 = -1.0;
      double var10 = 0.0;
      double var12 = 0.0;
      double var14 = -1.0;
      double var16 = 0.0;
      boolean var18 = false;
      boolean var19 = false;
      boolean var20 = false;
      PowerSource.CapacityUnits var21 = PowerSource.CapacityUnits.RELATIVE;
      int var22 = 0;
      int var23 = 1;
      byte var24 = 1;
      byte var25 = -1;
      String var26 = "unknown";
      Object var27 = null;
      String var28 = "unknown";
      String var29 = "unknown";
      double var30 = 0.0;
      if (KSTAT_BATT_IDX > 0) {
         try (KstatUtil.KstatChain var32 = KstatUtil.openChain()) {
            Kstat var34 = var32.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BIF0");
            if (var34 != null) {
               long var35 = KstatUtil.dataLookupLong(var34, "bif_last_cap");
               if (var35 == 7175793319950120722L || var35 <= 0L) {
                  var35 = KstatUtil.dataLookupLong(var34, "bif_design_cap");
               }

               if (var35 != 7175793319950120722L && var35 > 0L) {
                  var23 = (int)var35;
               }

               long var37 = KstatUtil.dataLookupLong(var34, "bif_unit");
               if (var37 == 0L) {
                  var21 = PowerSource.CapacityUnits.MWH;
               } else if (var37 == 1L) {
                  var21 = PowerSource.CapacityUnits.MAH;
               }

               var5 = KstatUtil.dataLookupString(var34, "bif_model");
               var29 = KstatUtil.dataLookupString(var34, "bif_serial");
               var26 = KstatUtil.dataLookupString(var34, "bif_type");
               var28 = KstatUtil.dataLookupString(var34, "bif_oem_info");
            }

            var34 = var32.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BST0");
            if (var34 != null) {
               long var52 = KstatUtil.dataLookupLong(var34, "bst_rem_cap");
               if (var52 >= 0L) {
                  var22 = (int)var52;
               }

               long var53 = KstatUtil.dataLookupLong(var34, "bst_rate");
               if (var53 == 7175793319950120722L) {
                  var53 = 0L;
               }

               boolean var39 = (KstatUtil.dataLookupLong(var34, "bst_state") & 16L) > 0L;
               if (!var39) {
                  var8 = var53 > 0L ? 3600.0 * var52 / var53 : -1.0;
               }

               long var40 = KstatUtil.dataLookupLong(var34, "bst_voltage");
               if (var40 > 0L) {
                  var14 = var40 / 1000.0;
                  var16 = var12 * 1000.0 / var40;
               }
            }
         }
      }

      return new SolarisPowerSource(
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
      );
   }

   static {
      try (KstatUtil.KstatChain var17 = KstatUtil.openChain()) {
         if (var17.lookup(KSTAT_BATT_MOD[1], 0, null) != null) {
            KSTAT_BATT_IDX = 1;
         } else if (var17.lookup(KSTAT_BATT_MOD[2], 0, null) != null) {
            KSTAT_BATT_IDX = 2;
         } else {
            KSTAT_BATT_IDX = 0;
         }
      }
   }
}
