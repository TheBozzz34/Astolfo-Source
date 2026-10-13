package oshi.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class FormatUtil {
   private static final BigInteger TWOS_COMPLEMENT_REF = BigInteger.ONE.shiftLeft(64);
   public static final String HEX_ERROR = "0x%08X";
   private static final long a = 20026014873899L;

   private FormatUtil() {
   }

   public static String formatBytes(long var0) {
      if (var0 == 1L) {
         return String.format("%d byte", var0);
      } else if (var0 < 1024L) {
         return String.format("%d bytes", var0);
      } else if (var0 < 1048576L) {
         return formatUnits(var0, 1024L, "KiB");
      } else if (var0 < 1073741824L) {
         return formatUnits(var0, 1048576L, "MiB");
      } else if (var0 < 1099511627776L) {
         return formatUnits(var0, 1073741824L, "GiB");
      } else if (var0 < 1125899906842624L) {
         return formatUnits(var0, 1099511627776L, "TiB");
      } else {
         return var0 < 1152921504606846976L ? formatUnits(var0, 1125899906842624L, "PiB") : formatUnits(var0, 1152921504606846976L, "EiB");
      }
   }

   private static String formatUnits(long var0, long var2, String var4) {
      return var0 % var2 == 0L ? String.format("%d %s", var0 / var2, var4) : String.format("%.1f %s", (double)var0 / var2, var4);
   }

   public static String formatBytesDecimal(long var0) {
      if (var0 == 1L) {
         return String.format("%d byte", var0);
      } else {
         return var0 < 1000L ? String.format("%d bytes", var0) : formatValue(var0, "B");
      }
   }

   public static String formatHertz(long var0) {
      return formatValue(var0, "Hz");
   }

   public static String formatValue(long var0, String var2) {
      if (var0 < 1000L) {
         return String.format("%d %s", var0, var2);
      } else if (var0 < 1000000L) {
         return formatUnits(var0, 1000L, "K" + var2);
      } else if (var0 < 1000000000L) {
         return formatUnits(var0, 1000000L, "M" + var2);
      } else if (var0 < 1000000000000L) {
         return formatUnits(var0, 1000000000L, "G" + var2);
      } else if (var0 < 1000000000000000L) {
         return formatUnits(var0, 1000000000000L, "T" + var2);
      } else {
         return var0 < 1000000000000000000L ? formatUnits(var0, 1000000000000000L, "P" + var2) : formatUnits(var0, 1000000000000000000L, "E" + var2);
      }
   }

   public static String formatElapsedSecs(long var0) {
      long var4 = var0;
      long var6 = TimeUnit.SECONDS.toDays(var4);
      var4 -= TimeUnit.DAYS.toSeconds(var6);
      long var8 = TimeUnit.SECONDS.toHours(var4);
      var4 -= TimeUnit.HOURS.toSeconds(var8);
      long var10 = TimeUnit.SECONDS.toMinutes(var4);
      var4 -= TimeUnit.MINUTES.toSeconds(var10);
      long var12 = var4;
      return String.format("%d days, %02d:%02d:%02d", var6, var8, var10, var12);
   }

   public static float round(float var0, int var1) {
      BigDecimal var2 = new BigDecimal(Float.toString(var0)).setScale(var1, RoundingMode.HALF_UP);
      return var2.floatValue();
   }

   public static long getUnsignedInt(int var0) {
      return var0 & 4294967295L;
   }

   public static String toUnsignedString(int var0) {
      return var0 >= 0 ? Integer.toString(var0) : Long.toString(getUnsignedInt(var0));
   }

   public static String toUnsignedString(long var0) {
      return var0 >= 0L ? Long.toString(var0) : BigInteger.valueOf(var0).add(TWOS_COMPLEMENT_REF).toString();
   }

   public static String formatError(int var0) {
      return String.format("0x%08X", var0);
   }
}
