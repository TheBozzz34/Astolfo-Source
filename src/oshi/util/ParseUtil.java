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
   private static final Pattern HERTZ_PATTERN = Pattern.compile("(\\d+(.\\d+)?) ?([kMGT]?Hz).*");
   private static final Pattern VALID_HEX = Pattern.compile("[0-9a-fA-F]+");
   private static final Pattern DHMS = Pattern.compile("(?:(\\d+)-)?(?:(\\d+):)??(?:(\\d+):)?(\\d+)(?:\\.(\\d+))?");
   private static final Pattern UUID_PATTERN = Pattern.compile(".*([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}).*");
   private static final Pattern VENDOR_PRODUCT_ID = Pattern.compile(".*(?:VID|VEN)_(\\p{XDigit}{4})&(?:PID|DEV)_(\\p{XDigit}{4}).*");
   private static final Pattern LSPCI_MACHINE_READABLE = Pattern.compile("(.+)\\s\\[(.*?)\\]");
   private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
   private static final Map<String, Long> multipliers = new HashMap<>();
   private static final int TZ_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());
   public static final Pattern whitespacesColonWhitespace = Pattern.compile("\\s+:\\s");
   public static final Pattern whitespaces = Pattern.compile("\\s+");
   public static final Pattern notDigits = Pattern.compile("[^0-9]+");
   public static final Pattern startWithNotDigits = Pattern.compile("^[^0-9]*");
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

   private ParseUtil() {
   }

   public static long parseHertz(String hertz) {
      Matcher matcher2 = HERTZ_PATTERN.matcher(hertz.trim());
      if (matcher2.find() && matcher2.groupCount() == 3) {
         double d = Double.valueOf(matcher2.group(1)) * multipliers.getOrDefault(matcher2.group(3), -1L).longValue();
         if (d >= 0.0) {
            return (long)d;
         }
      }

      return -1L;
   }

   public static int parseLastInt(String s, int i) {
      try {
         String string = parseLastString(s);
         return string.toLowerCase().startsWith("0x") ? Integer.decode(string) : Integer.parseInt(string);
      } catch (NumberFormatException numberFormatException) {
         return i;
      }
   }

   public static long parseLastLong(String s, long li) {
      try {
         String string = parseLastString(s);
         return string.toLowerCase().startsWith("0x") ? Long.decode(string) : Long.parseLong(string);
      } catch (NumberFormatException numberFormatException) {
         return li;
      }
   }

   public static double parseLastDouble(String s, double d) {
      try {
         return Double.parseDouble(parseLastString(s));
      } catch (NumberFormatException numberFormatException) {
         return d;
      }
   }

   public static String parseLastString(String s) {
      String[] strings = whitespaces.split(s);
      return strings.length < 1 ? s : strings[strings.length - 1];
   }

   public static String byteArrayToHexString(byte[] bytes) {
      char[] chars = new char[bytes.length * 2];

      for (int i = 0; i < bytes.length; i++) {
         int j = bytes[i] & 255;
         chars[i * 2] = HEX_ARRAY[j >>> 4];
         chars[i * 2 + 1] = HEX_ARRAY[j & 15];
      }

      return new String(chars);
   }

   public static byte[] hexStringToByteArray(String digits) {
      int i = digits.length();
      if (VALID_HEX.matcher(digits).matches() && (i & 1) == 0) {
         byte[] bytes = new byte[i / 2];

         for (int j = 0; j < i; j += 2) {
            bytes[j / 2] = (byte)(Character.digit(digits.charAt(j), 16) << 4 | Character.digit(digits.charAt(j + 1), 16));
         }

         return bytes;
      } else {
         return new byte[0];
      }
   }

   public static byte[] asciiStringToByteArray(String text, int length) {
      return Arrays.copyOf(text.getBytes(StandardCharsets.US_ASCII), length);
   }

   public static byte[] longToByteArray(long value, int valueSize, int length) {
      long l = value;
      byte[] bytes = new byte[8];

      for (int i = 7; i >= 0 && l != 0L; i--) {
         bytes[i] = (byte)l;
         l >>>= 8;
      }

      return Arrays.copyOfRange(bytes, 8 - valueSize, 8 + length - valueSize);
   }

   public static long strToLong(String str, int size) {
      return byteArrayToLong(str.getBytes(StandardCharsets.US_ASCII), size);
   }

   public static long byteArrayToLong(byte[] bytes, int size) {
      if (size > 8) {
         throw new IllegalArgumentException("Can't convert more than 8 bytes.");
      }

      if (size > bytes.length) {
         throw new IllegalArgumentException("Size can't be larger than array length.");
      }

      long l = 0L;

      for (int i = 0; i < size; i++) {
         l = l << 8 | bytes[i] & 0xFF;
      }

      return l;
   }

   public static float byteArrayToFloat(byte[] bytes, int size, int fpBits) {
      return (float)byteArrayToLong(bytes, size) / (1 << fpBits);
   }

   public static long unsignedIntToLong(int unsignedValue) {
      long longValue = unsignedValue;
      return longValue & 4294967295L;
   }

   public static long unsignedLongToSignedLong(long unsignedValue) {
      return unsignedValue & Long.MAX_VALUE;
   }

   public static String hexStringToString(String hexString) {
      if (hexString.length() % 2 > 0) {
         return hexString;
      }

      StringBuilder stringBuilder = new StringBuilder();

      try {
         for (int i = 0; i < hexString.length(); i += 2) {
            int j = Integer.parseInt(hexString.substring(i, i + 2), 16);
            if (j < 32 || j > 127) {
               return hexString;
            }

            stringBuilder.append((char)j);
         }
      } catch (NumberFormatException numberFormatException) {
         return hexString;
      }

      return stringBuilder.toString();
   }

   public static int parseIntOrDefault(String s, int defaultInt) {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException numberFormatException) {
         return defaultInt;
      }
   }

   public static long parseLongOrDefault(String s, long defaultLong) {
      try {
         return Long.parseLong(s);
      } catch (NumberFormatException numberFormatException) {
         return defaultLong;
      }
   }

   public static long parseUnsignedLongOrDefault(String s, long defaultLong) {
      try {
         return new BigInteger(s).longValue();
      } catch (NumberFormatException numberFormatException) {
         return defaultLong;
      }
   }

   public static double parseDoubleOrDefault(String s, double defaultDouble) {
      try {
         return Double.parseDouble(s);
      } catch (NumberFormatException numberFormatException) {
         return defaultDouble;
      }
   }

   public static long parseDHMSOrDefault(String s, long defaultLong) {
      Matcher matcher2 = DHMS.matcher(s);
      if (matcher2.matches()) {
         long l = 0L;
         if (matcher2.group(1) != null) {
            l += parseLongOrDefault(matcher2.group(1), 0L) * 86400000L;
         }

         if (matcher2.group(2) != null) {
            l += parseLongOrDefault(matcher2.group(2), 0L) * 3600000L;
         }

         if (matcher2.group(3) != null) {
            l += parseLongOrDefault(matcher2.group(3), 0L) * 60000L;
         }

         l += parseLongOrDefault(matcher2.group(4), 0L) * 1000L;
         return l + (long)(1000.0 * parseDoubleOrDefault("0." + matcher2.group(5), 0.0));
      } else {
         return defaultLong;
      }
   }

   public static String parseUuidOrDefault(String s, String defaultStr) {
      Matcher matcher2 = UUID_PATTERN.matcher(s.toLowerCase());
      return matcher2.matches() ? matcher2.group(1) : defaultStr;
   }

   public static String getSingleQuoteStringValue(String line) {
      return getStringBetween(line, '\'');
   }

   public static String getDoubleQuoteStringValue(String line) {
      return getStringBetween(line, '"');
   }

   public static String getStringBetween(String line, char c) {
      int i = line.indexOf(c);
      return i < 0 ? "" : line.substring(i + 1, line.lastIndexOf(c)).trim();
   }

   public static int getFirstIntValue(String line) {
      return getNthIntValue(line, 1);
   }

   public static int getNthIntValue(String line, int n) {
      String[] strings = notDigits.split(startWithNotDigits.matcher(line).replaceFirst(""));
      return strings.length >= n ? parseIntOrDefault(strings[n - 1], 0) : 0;
   }

   public static String removeMatchingString(String original, String toRemove) {
      if (original != null && !original.isEmpty() && toRemove != null && !toRemove.isEmpty()) {
         int i = original.indexOf(toRemove, 0);
         if (i == -1) {
            return original;
         }

         StringBuilder stringBuilder = new StringBuilder(original.length() - toRemove.length());
         int j = 0;

         do {
            stringBuilder.append(original.substring(j, i));
            j = i + toRemove.length();
            i = original.indexOf(toRemove, j);
         } while (i != -1);

         stringBuilder.append(original.substring(j));
         return stringBuilder.toString();
      } else {
         return original;
      }
   }

   public static long[] parseStringToLongArray(String s, int[] indices, int length, char delimiter) {
      long[] longs = new long[indices.length];
      int i = s.length();
      int j = indices.length - 1;
      int k = length - 1;
      int l = 0;
      boolean bl = false;
      boolean bl2 = true;
      boolean bl3 = false;
      boolean bl4 = false;

      while (--i > 0 && j >= 0) {
         char c = s.charAt(i);
         if (c == delimiter) {
            if (!bl3 && bl2) {
               bl3 = true;
            }

            if (!bl) {
               if (bl3 && indices[j] == k--) {
                  j--;
               }

               bl = true;
               l = 0;
               bl4 = false;
               bl2 = true;
            }
         } else if (indices[j] != k || c == '+' || !bl2) {
            bl = false;
         } else if (c >= '0' && c <= '9' && !bl4) {
            if (l <= 18 && (l != 17 || c != '9' || longs[j] <= 223372036854775807L)) {
               longs[j] += (c - '0') * POWERS_OF_TEN[l++];
            } else {
               longs[j] = Long.MAX_VALUE;
            }

            bl = false;
         } else if (c == '-') {
            longs[j] *= -1L;
            bl = false;
            bl4 = true;
         } else {
            if (bl3) {
               return new long[indices.length];
            }

            longs[j] = 0L;
            bl2 = false;
         }
      }

      return j > 0 ? new long[indices.length] : longs;
   }

   public static int countStringToLongArray(String s, char delimiter) {
      int i = s.length();
      int j = 0;
      boolean bl = false;
      boolean bl2 = true;
      boolean bl3 = false;

      while (--i > 0) {
         char c = s.charAt(i);
         if (c == delimiter) {
            if (!bl) {
               if (bl2) {
                  j++;
               }

               bl = true;
               bl3 = false;
               bl2 = true;
            }
         } else if (c == '+' || !bl2) {
            bl = false;
         } else if (c >= '0' && c <= '9' && !bl3) {
            bl = false;
         } else if (c == '-') {
            bl = false;
            bl3 = true;
         } else {
            if (j > 0) {
               return j;
            }

            bl2 = false;
         }
      }

      return j + 1;
   }

   public static String getTextBetweenStrings(String text, String before, String after) {
      String string = "";
      if (text.indexOf(before) >= 0 && text.indexOf(after) >= 0) {
         string = text.substring(text.indexOf(before) + before.length(), text.length());
         string = string.substring(0, string.indexOf(after));
      }

      return string;
   }

   public static long filetimeToUtcMs(long filetime, boolean local) {
      return filetime / 10000L - 11644473600000L - (local ? TZ_OFFSET : 0L);
   }

   public static String parseMmDdYyyyToYyyyMmDD(String dateString) {
      try {
         return String.format("%s-%s-%s", dateString.substring(6, 10), dateString.substring(0, 2), dateString.substring(3, 5));
      } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
         return dateString;
      }
   }

   public static OffsetDateTime parseCimDateTimeToOffset(String cimDateTime) {
      try {
         int i = Integer.parseInt(cimDateTime.substring(22));
         LocalTime localTime = LocalTime.MIDNIGHT.plusMinutes(i);
         return OffsetDateTime.parse(cimDateTime.substring(0, 22) + localTime.format(DateTimeFormatter.ISO_LOCAL_TIME), CIM_FORMAT);
      } catch (IndexOutOfBoundsException | NumberFormatException | DateTimeParseException value) {
         return Constants.UNIX_EPOCH;
      }
   }

   public static boolean filePathStartsWith(List<String> prefixList, String path) {
      for (String string : prefixList) {
         if (path.equals(string) || path.startsWith(string + "/")) {
            return true;
         }
      }

      return false;
   }

   public static long parseDecimalMemorySizeToBinary(String size) {
      String[] strings = whitespaces.split(size);
      long l = parseLongOrDefault(strings[0], 0L);
      if (strings.length == 2 && strings[1].length() > 1) {
         switch (strings[1].charAt(0)) {
            case 'G':
               l <<= 30;
               break;
            case 'K':
            case 'k':
               l <<= 10;
               break;
            case 'M':
               l <<= 20;
               break;
            case 'T':
               l <<= 40;
         }
      }

      return l;
   }

   public static Pair<String, String> parsePnPDeviceIdToVendorProductId(String pnpDeviceId) {
      Matcher matcher2 = VENDOR_PRODUCT_ID.matcher(pnpDeviceId);
      if (matcher2.matches()) {
         String string = "0x" + matcher2.group(1).toLowerCase();
         String string2 = "0x" + matcher2.group(2).toLowerCase();
         return new Pair<>(string, string2);
      } else {
         return null;
      }
   }

   public static long parseLshwResourceString(String resources) {
      long l = 0L;
      String[] strings = whitespaces.split(resources);

      for (String string : strings) {
         if (string.startsWith("memory:")) {
            String[] strings2 = string.substring(7).split("-");
            if (strings2.length == 2) {
               try {
                  l += Long.parseLong(strings2[1], 16) - Long.parseLong(strings2[0], 16) + 1L;
               } catch (NumberFormatException numberFormatException) {
               }
            }
         }
      }

      return l;
   }

   public static Pair<String, String> parseLspciMachineReadable(String line) {
      Matcher matcher2 = LSPCI_MACHINE_READABLE.matcher(line);
      return matcher2.matches() ? new Pair<>(matcher2.group(1), matcher2.group(2)) : null;
   }

   public static long parseLspciMemorySize(String line) {
      Matcher matcher2 = LSPCI_MEMORY_SIZE.matcher(line);
      return matcher2.matches() ? parseDecimalMemorySizeToBinary(matcher2.group(1) + " " + matcher2.group(2) + "B") : 0L;
   }

   static {
      multipliers.put("Hz", 1L);
      multipliers.put("kHz", 1000L);
      multipliers.put("MHz", 1000000L);
      multipliers.put("GHz", 1000000000L);
      multipliers.put("THz", 1000000000000L);
      multipliers.put("PHz", 1000000000000000L);
   }
}
