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
      return new SolarisPowerSource[]{getPowerSource("BAT0")};
   }

   private static SolarisPowerSource getPowerSource(String name) {
      String string = name;
      String string2 = "unknown";
      double d = 1.0;
      double e = -1.0;
      double d2 = 0.0;
      double d3 = 0.0;
      double d4 = -1.0;
      double d5 = 0.0;
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
      int i = 0;
      int j = 1;
      byte b = 1;
      byte b2 = -1;
      String string3 = "unknown";
      Object object = null;
      String string4 = "unknown";
      String string5 = "unknown";
      double d6 = 0.0;
      if (KSTAT_BATT_IDX > 0) {
         try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
            Kstat kstat = kstatChain.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BIF0");
            if (kstat != null) {
               long l = KstatUtil.dataLookupLong(kstat, "bif_last_cap");
               if (l == 7175793319950120722L || l <= 0L) {
                  l = KstatUtil.dataLookupLong(kstat, "bif_design_cap");
               }

               if (l != 7175793319950120722L && l > 0L) {
                  j = (int)l;
               }

               long m = KstatUtil.dataLookupLong(kstat, "bif_unit");
               if (m == 0L) {
                  capacityUnits = PowerSource.CapacityUnits.MWH;
               } else if (m == 1L) {
                  capacityUnits = PowerSource.CapacityUnits.MAH;
               }

               string2 = KstatUtil.dataLookupString(kstat, "bif_model");
               string5 = KstatUtil.dataLookupString(kstat, "bif_serial");
               string3 = KstatUtil.dataLookupString(kstat, "bif_type");
               string4 = KstatUtil.dataLookupString(kstat, "bif_oem_info");
            }

            kstat = kstatChain.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BST0");
            if (kstat != null) {
               long n = KstatUtil.dataLookupLong(kstat, "bst_rem_cap");
               if (n >= 0L) {
                  i = (int)n;
               }

               long l2 = KstatUtil.dataLookupLong(kstat, "bst_rate");
               if (l2 == 7175793319950120722L) {
                  l2 = 0L;
               }

               boolean bl4 = (KstatUtil.dataLookupLong(kstat, "bst_state") & 16L) > 0L;
               if (!bl4) {
                  e = l2 > 0L ? 3600.0 * n / l2 : -1.0;
               }

               long l3 = KstatUtil.dataLookupLong(kstat, "bst_voltage");
               if (l3 > 0L) {
                  d4 = l3 / 1000.0;
                  d5 = d3 * 1000.0 / l3;
               }
            }
         }
      }

      return new SolarisPowerSource(
         string,
         string2,
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
         b2,
         string3,
         (LocalDate)object,
         string4,
         string5,
         d6
      );
   }

   static {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         if (kstatChain.lookup(KSTAT_BATT_MOD[1], 0, null) != null) {
            KSTAT_BATT_IDX = 1;
         } else if (kstatChain.lookup(KSTAT_BATT_MOD[2], 0, null) != null) {
            KSTAT_BATT_IDX = 2;
         } else {
            KSTAT_BATT_IDX = 0;
         }
      }
   }
}
