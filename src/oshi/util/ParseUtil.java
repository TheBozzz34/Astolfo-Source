package oshi.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ParseUtil {
   private static final Pattern f = Pattern.compile("(\\d+(.\\d+)?) ?([kMGT]?Hz).*");
   private static final Pattern q = Pattern.compile("[0-9a-fA-F]+");
   private static final Pattern n = Pattern.compile("(?:(\\d+)-)?(?:(\\d+):)??(?:(\\d+):)?(\\d+)(?:\\.(\\d+))?");
   private static final Pattern O = Pattern.compile(".*([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}).*");
   private static final Pattern d = Pattern.compile(".*(?:VID|VEN)_(\\p{XDigit}{4})&(?:PID|DEV)_(\\p{XDigit}{4}).*");
   private static final Pattern h = Pattern.compile("(.+)\\s\\[(.*?)\\]");
   private static final Pattern s = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
   private static final Map<String, Long> multipliers = new HashMap<>();
   private static final int TZ_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());
   public static final Pattern H = Pattern.compile("\\s+:\\s");
   public static final Pattern Q = Pattern.compile("\\s+");
   public static final Pattern T = Pattern.compile("[^0-9]+");
   public static final Pattern L = Pattern.compile("^[^0-9]*");
   private static final long[] POWERS_OF_TEN = new long[]{
      1L,
      10L,
      100L,
      1000L,
      10000L,
      100000L,
      1000000L,
      10000000L,
      100000000L,
      1000000000L,
      10000000000L,
      100000000000L,
      1000000000000L,
      10000000000000L,
      100000000000000L,
      1000000000000000L,
      10000000000000000L,
      100000000000000000L,
      1000000000000000000L
   };
   private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
   private static final DateTimeFormatter CIM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSSZZZZZ", Locale.US);
   private static final long EPOCH_DIFF = 21515821039742L;

   private ParseUtil() {
   }

   public static long parseHertz(String var0) {
      Matcher var4 = f.matcher(var0.trim());
      if (var4.find() && var4.groupCount() == 3) {
         double var5 = Double.valueOf(var4.group(1)) * multipliers.getOrDefault(var4.group(3), -1L).longValue();
         if (var5 >= 0.0) {
            return (long)var5;
         }
      }

      return -1L;
   }

   public static int parseLastInt(String var0, int var1) {
      try {
         String var5 = parseLastString(var0);
         return var5.toLowerCase().startsWith("0x") ? Integer.decode(var5) : Integer.parseInt(var5);
      } catch (NumberFormatException var6) {
         return var1;
      }
   }

   public static long parseLastLong(String var0, long var1) {
      try {
         String var6 = parseLastString(var0);
         return var6.toLowerCase().startsWith("0x") ? Long.decode(var6) : Long.parseLong(var6);
      } catch (NumberFormatException var7) {
         return var1;
      }
   }

   public static double parseLastDouble(String var0, double var1) {
      try {
         return Double.parseDouble(parseLastString(var0));
      } catch (NumberFormatException var4) {
         return var1;
      }
   }

   public static String parseLastString(String var0) {
      String[] var4 = Q.split(var0);
      return var4.length < 1 ? var0 : var4[var4.length - 1];
   }

   public static String byteArrayToHexString(byte[] var0) {
      char[] var4 = new char[var0.length * 2];

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6 = var0[var5] & 255;
         var4[var5 * 2] = HEX_ARRAY[var6 >>> 4];
         var4[var5 * 2 + 1] = HEX_ARRAY[var6 & 15];
      }

      return new String(var4);
   }

   public static byte[] hexStringToByteArray(String var0) {
      int var4 = var0.length();
      if (q.matcher(var0).matches() && (var4 & 1) == 0) {
         byte[] var5 = new byte[var4 / 2];

         for (int var6 = 0; var6 < var4; var6 += 2) {
            var5[var6 / 2] = (byte)(Character.digit(var0.charAt(var6), 16) << 4 | Character.digit(var0.charAt(var6 + 1), 16));
         }

         return var5;
      } else {
         return new byte[0];
      }
   }

   public static byte[] asciiStringToByteArray(String var0, int var1) {
      return Arrays.copyOf(var0.getBytes(StandardCharsets.US_ASCII), var1);
   }

   public static byte[] longToByteArray(long var0, int var2, int var3) {
      long var7 = var0;
      byte[] var9 = new byte[8];

      for (int var10 = 7; var10 >= 0 && var7 != 0L; var10--) {
         var9[var10] = (byte)var7;
         var7 >>>= 8;
      }

      return Arrays.copyOfRange(var9, 8 - var2, 8 + var3 - var2);
   }

   public static long strToLong(String var0, int var1) {
      return byteArrayToLong(var0.getBytes(StandardCharsets.US_ASCII), var1);
   }

   public static long byteArrayToLong(byte[] var0, int var1) {
      if (var1 > 8) {
         throw new IllegalArgumentException("Can't convert more than 8 bytes.");
      }

      if (var1 > var0.length) {
         throw new IllegalArgumentException("Size can't be larger than array length.");
      }

      long var5 = 0L;

      for (int var7 = 0; var7 < var1; var7++) {
         var5 = var5 << 8 | var0[var7] & 0xFF;
      }

      return var5;
   }

   public static float byteArrayToFloat(byte[] var0, int var1, int var2) {
      return (float)byteArrayToLong(var0, var1) / (1 << var2);
   }

   public static long unsignedIntToLong(int var0) {
      long var1 = var0;
      return var1 & 4294967295L;
   }

   public static long unsignedLongToSignedLong(long var0) {
      return var0 & Long.MAX_VALUE;
   }

   public static String hexStringToString(String var0) {
      if (var0.length() % 2 > 0) {
         return var0;
      }

      StringBuilder var5 = new StringBuilder();

      try {
         for (int var6 = 0; var6 < var0.length(); var6 += 2) {
            int var4 = Integer.parseInt(var0.substring(var6, var6 + 2), 16);
            if (var4 < 32 || var4 > 127) {
               return var0;
            }

            var5.append((char)var4);
         }
      } catch (NumberFormatException var7) {
         return var0;
      }

      return var5.toString();
   }

   public static int parseIntOrDefault(String var0, int var1) {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var3) {
         return var1;
      }
   }

   public static long parseLongOrDefault(String var0, long var1) {
      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException var4) {
         return var1;
      }
   }

   public static long parseUnsignedLongOrDefault(String var0, long var1) {
      try {
         return new BigInteger(var0).longValue();
      } catch (NumberFormatException var4) {
         return var1;
      }
   }

   public static double parseDoubleOrDefault(String var0, double var1) {
      try {
         return Double.parseDouble(var0);
      } catch (NumberFormatException var4) {
         return var1;
      }
   }

   public static long parseDHMSOrDefault(String var0, long var1) {
      Matcher var6 = n.matcher(var0);
      if (var6.matches()) {
         long var7 = 0L;
         if (var6.group(1) != null) {
            var7 += parseLongOrDefault(var6.group(1), 0L) * 86400000L;
         }

         if (var6.group(2) != null) {
            var7 += parseLongOrDefault(var6.group(2), 0L) * 3600000L;
         }

         if (var6.group(3) != null) {
            var7 += parseLongOrDefault(var6.group(3), 0L) * 60000L;
         }

         var7 += parseLongOrDefault(var6.group(4), 0L) * 1000L;
         return var7 + (long)(1000.0 * parseDoubleOrDefault("0." + var6.group(5), 0.0));
      } else {
         return var1;
      }
   }

   public static String parseUuidOrDefault(String var0, String var1) {
      Matcher var5 = O.matcher(var0.toLowerCase());
      return var5.matches() ? var5.group(1) : var1;
   }

   public static String getSingleQuoteStringValue(String var0) {
      return getStringBetween(var0, '\'');
   }

   public static String getDoubleQuoteStringValue(String var0) {
      return getStringBetween(var0, '"');
   }

   public static String getStringBetween(String var0, char var1) {
      int var4 = var0.indexOf(var1);
      return var4 < 0 ? "" : var0.substring(var4 + 1, var0.lastIndexOf(var1)).trim();
   }

   public static int getFirstIntValue(String var0) {
      return getNthIntValue(var0, 1);
   }

   public static int getNthIntValue(String var0, int var1) {
      String[] var5 = T.split(L.matcher(var0).replaceFirst(""));
      return var5.length >= var1 ? parseIntOrDefault(var5[var1 - 1], 0) : 0;
   }

   public static String removeMatchingString(String var0, String var1) {
      if (var0 != null && !var0.isEmpty() && var1 != null && !var1.isEmpty()) {
         int var5 = var0.indexOf(var1, 0);
         if (var5 == -1) {
            return var0;
         }

         StringBuilder var6 = new StringBuilder(var0.length() - var1.length());
         int var7 = 0;

         do {
            var6.append(var0.substring(var7, var5));
            var7 = var5 + var1.length();
            var5 = var0.indexOf(var1, var7);
         } while (var5 != -1);

         var6.append(var0.substring(var7));
         return var6.toString();
      } else {
         return var0;
      }
   }

   public static long[] parseStringToLongArray(String var0, int[] var1, int var2, char var3) {
      long[] var7 = new long[var1.length];
      int var8 = var0.length();
      int var9 = var1.length - 1;
      int var10 = var2 - 1;
      int var11 = 0;
      boolean var13 = false;
      boolean var14 = true;
      boolean var15 = false;
      boolean var16 = false;

      while (--var8 > 0 && var9 >= 0) {
         char var12 = var0.charAt(var8);
         if (var12 == var3) {
            if (!var15 && var14) {
               var15 = true;
            }

            if (!var13) {
               if (var15 && var1[var9] == var10--) {
                  var9--;
               }

               var13 = true;
               var11 = 0;
               var16 = false;
               var14 = true;
            }
         } else if (var1[var9] != var10 || var12 == '+' || !var14) {
            var13 = false;
         } else if (var12 >= '0' && var12 <= '9' && !var16) {
            if (var11 <= 18 && (var11 != 17 || var12 != '9' || var7[var9] <= 223372036854775807L)) {
               var7[var9] += (var12 - '0') * POWERS_OF_TEN[var11++];
            } else {
               var7[var9] = Long.MAX_VALUE;
            }

            var13 = false;
         } else if (var12 == '-') {
            var7[var9] *= -1L;
            var13 = false;
            var16 = true;
         } else {
            if (var15) {
               return new long[var1.length];
            }

            var7[var9] = 0L;
            var14 = false;
         }
      }

      return var9 > 0 ? new long[var1.length] : var7;
   }

   public static int countStringToLongArray(String var0, char var1) {
      int var4 = var0.length();
      int var5 = 0;
      boolean var7 = false;
      boolean var8 = true;
      boolean var9 = false;

      while (--var4 > 0) {
         char var6 = var0.charAt(var4);
         if (var6 == var1) {
            if (!var7) {
               if (var8) {
                  var5++;
               }

               var7 = true;
               var9 = false;
               var8 = true;
            }
         } else if (var6 == '+' || !var8) {
            var7 = false;
         } else if (var6 >= '0' && var6 <= '9' && !var9) {
            var7 = false;
         } else if (var6 == '-') {
            var7 = false;
            var9 = true;
         } else {
            if (var5 > 0) {
               return var5;
            }

            var8 = false;
         }
      }

      return var5 + 1;
   }

   public static String getTextBetweenStrings(String var0, String var1, String var2) {
      String var6 = "";
      if (var0.indexOf(var1) >= 0 && var0.indexOf(var2) >= 0) {
         var6 = var0.substring(var0.indexOf(var1) + var1.length(), var0.length());
         var6 = var6.substring(0, var6.indexOf(var2));
      }

      return var6;
   }

   public static long filetimeToUtcMs(long var0, boolean var2) {
      return var0 / 10000L - 11644473600000L - (var2 ? TZ_OFFSET : 0L);
   }

   public static String parseMmDdYyyyToYyyyMmDD(String var0) {
      try {
         return String.format("%s-%s-%s", var0.substring(6, 10), var0.substring(0, 2), var0.substring(3, 5));
      } catch (StringIndexOutOfBoundsException var4) {
         return var0;
      }
   }

   public static OffsetDateTime parseCimDateTimeToOffset(String var0) {
      try {
         int var3 = Integer.parseInt(var0.substring(22));
         LocalTime var4 = LocalTime.MIDNIGHT.plusMinutes(var3);
         return OffsetDateTime.parse(var0.substring(0, 22) + var4.format(DateTimeFormatter.ISO_LOCAL_TIME), CIM_FORMAT);
      } catch (IndexOutOfBoundsException | NumberFormatException | DateTimeParseException var5) {
         return Constants.UNIX_EPOCH;
      }
   }

   public static boolean filePathStartsWith(List<String> var0, String var1) {
      for (String var6 : var0) {
         if (var1.equals(var6) || var1.startsWith(var6 + "/")) {
            return true;
         }
      }

      return false;
   }

   public static long parseDecimalMemorySizeToBinary(String var0) {
      String[] var4 = Q.split(var0);
      long var5 = parseLongOrDefault(var4[0], 0L);
      if (var4.length == 2 && var4[1].length() > 1) {
         switch (var4[1].charAt(0)) {
            case 'G':
               var5 <<= 30;
               break;
            case 'K':
            case 'k':
               var5 <<= 10;
               break;
            case 'M':
               var5 <<= 20;
               break;
            case 'T':
               var5 <<= 40;
         }
      }

      return var5;
   }

   public static Pair<String, String> parsePnPDeviceIdToVendorProductId(String var0) {
      Matcher var3 = d.matcher(var0);
      if (var3.matches()) {
         String var4 = "0x" + var3.group(1).toLowerCase();
         String var5 = "0x" + var3.group(2).toLowerCase();
         return new Pair<>(var4, var5);
      } else {
         return null;
      }
   }

   public static long parseLshwResourceString(String var0) {
      long var4 = 0L;
      String[] var6 = Q.split(var0);

      for (String var10 : var6) {
         if (var10.startsWith("memory:")) {
            String[] var11 = var10.substring(7).split("-");
            if (var11.length == 2) {
               try {
                  var4 += Long.parseLong(var11[1], 16) - Long.parseLong(var11[0], 16) + 1L;
               } catch (NumberFormatException var13) {
               }
            }
         }
      }

      return var4;
   }

   public static Pair<String, String> parseLspciMachineReadable(String var0) {
      Matcher var3 = h.matcher(var0);
      return var3.matches() ? new Pair<>(var3.group(1), var3.group(2)) : null;
   }

   public static long parseLspciMemorySize(String var0) {
      Matcher var3 = s.matcher(var0);
      return var3.matches() ? parseDecimalMemorySizeToBinary(var3.group(1) + " " + var3.group(2) + "B") : 0L;
   }

   static {
      long var20 = EPOCH_DIFF ^ 121445239247611L;
      long var10000 = 1324656337635860659L ^ var20;
      multipliers.put("Hz", 1L);
      multipliers.put("kHz", 1000L);
      multipliers.put("MHz", 1000000L);
      multipliers.put("GHz", 1000000000L);
      multipliers.put("THz", 1000000000000L);
      multipliers.put("PHz", 1000000000000000L);
   }
}
