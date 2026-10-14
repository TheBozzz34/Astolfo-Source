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

   private FormatUtil() {
   }

   public static String formatBytes(long bytes) {
      if (bytes == 1L) {
         return String.format("%d byte", bytes);
      } else if (bytes < 1024L) {
         return String.format("%d bytes", bytes);
      } else if (bytes < 1048576L) {
         return formatUnits(bytes, 1024L, "KiB");
      } else if (bytes < 1073741824L) {
         return formatUnits(bytes, 1048576L, "MiB");
      } else if (bytes < 1099511627776L) {
         return formatUnits(bytes, 1073741824L, "GiB");
      } else if (bytes < 1125899906842624L) {
         return formatUnits(bytes, 1099511627776L, "TiB");
      } else {
         return bytes < 1152921504606846976L ? formatUnits(bytes, 1125899906842624L, "PiB") : formatUnits(bytes, 1152921504606846976L, "EiB");
      }
   }

   private static String formatUnits(long value, long prefix, String unit) {
      return value % prefix == 0L ? String.format("%d %s", value / prefix, unit) : String.format("%.1f %s", (double)value / prefix, unit);
   }

   public static String formatBytesDecimal(long bytes) {
      if (bytes == 1L) {
         return String.format("%d byte", bytes);
      } else {
         return bytes < 1000L ? String.format("%d bytes", bytes) : formatValue(bytes, "B");
      }
   }

   public static String formatHertz(long hertz) {
      return formatValue(hertz, "Hz");
   }

   public static String formatValue(long value, String unit) {
      if (value < 1000L) {
         return String.format("%d %s", value, unit);
      } else if (value < 1000000L) {
         return formatUnits(value, 1000L, "K" + unit);
      } else if (value < 1000000000L) {
         return formatUnits(value, 1000000L, "M" + unit);
      } else if (value < 1000000000000L) {
         return formatUnits(value, 1000000000L, "G" + unit);
      } else if (value < 1000000000000000L) {
         return formatUnits(value, 1000000000000L, "T" + unit);
      } else {
         return value < 1000000000000000000L ? formatUnits(value, 1000000000000000L, "P" + unit) : formatUnits(value, 1000000000000000000L, "E" + unit);
      }
   }

   public static String formatElapsedSecs(long secs) {
      long l = secs;
      long m = TimeUnit.SECONDS.toDays(l);
      l -= TimeUnit.DAYS.toSeconds(m);
      long n = TimeUnit.SECONDS.toHours(l);
      l -= TimeUnit.HOURS.toSeconds(n);
      long l2 = TimeUnit.SECONDS.toMinutes(l);
      l -= TimeUnit.MINUTES.toSeconds(l2);
      long l3 = l;
      return String.format("%d days, %02d:%02d:%02d", m, n, l2, l3);
   }

   public static float round(float d, int decimalPlace) {
      BigDecimal bd = new BigDecimal(Float.toString(d)).setScale(decimalPlace, RoundingMode.HALF_UP);
      return bd.floatValue();
   }

   public static long getUnsignedInt(int x) {
      return x & 4294967295L;
   }

   public static String toUnsignedString(int i) {
      return i >= 0 ? Integer.toString(i) : Long.toString(getUnsignedInt(i));
   }

   public static String toUnsignedString(long l) {
      return l >= 0L ? Long.toString(l) : BigInteger.valueOf(l).add(TWOS_COMPLEMENT_REF).toString();
   }

   public static String formatError(int errorCode) {
      return String.format("0x%08X", errorCode);
   }
}
