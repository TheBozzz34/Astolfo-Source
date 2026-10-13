package oshi.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class EdidUtil {
   private static final long a = 6239473398662L;

   private EdidUtil() {
   }

   public static String getManufacturerID(byte[] var0) {
      String var4 = String.format("%8s%8s", Integer.toBinaryString(var0[8] & 255), Integer.toBinaryString(var0[9] & 255)).replace(' ', '0');
      return String.format(
            "%s%s%s",
            (char)(64 + Integer.parseInt(var4.substring(1, 6), 2)),
            (char)(64 + Integer.parseInt(var4.substring(7, 11), 2)),
            (char)(64 + Integer.parseInt(var4.substring(12, 16), 2))
         )
         .replace("@", "");
   }

   public static String getProductID(byte[] var0) {
      return Integer.toHexString(ByteBuffer.wrap(Arrays.copyOfRange(var0, 10, 12)).order(ByteOrder.LITTLE_ENDIAN).getShort() & 65535);
   }

   public static String getSerialNo(byte[] var0) {
      return String.format(
         "%s%s%s%s", getAlphaNumericOrHex(var0[15]), getAlphaNumericOrHex(var0[14]), getAlphaNumericOrHex(var0[13]), getAlphaNumericOrHex(var0[12])
      );
   }

   private static String getAlphaNumericOrHex(byte var0) {
      return Character.isLetterOrDigit((char)var0) ? String.format("%s", (char)var0) : String.format("%02X", var0);
   }

   public static byte getWeek(byte[] var0) {
      return var0[16];
   }

   public static int getYear(byte[] var0) {
      byte var3 = var0[17];
      return var3 + 1990;
   }

   public static String getVersion(byte[] var0) {
      return var0[18] + "." + var0[19];
   }

   public static boolean isDigital(byte[] var0) {
      return 1 == (var0[20] & 255) >> 7;
   }

   public static int getHcm(byte[] var0) {
      return var0[21];
   }

   public static int getVcm(byte[] var0) {
      return var0[22];
   }

   public static byte[][] getDescriptors(byte[] var0) {
      byte[][] var3 = new byte[4][18];

      for (int var4 = 0; var4 < var3.length; var4++) {
         System.arraycopy(var0, 54 + 18 * var4, var3[var4], 0, 18);
      }

      return var3;
   }

   public static int getDescriptorType(byte[] var0) {
      return ByteBuffer.wrap(Arrays.copyOfRange(var0, 0, 4)).getInt();
   }

   public static String getTimingDescriptor(byte[] var0) {
      int var3 = ByteBuffer.wrap(Arrays.copyOfRange(var0, 0, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;
      int var4 = (var0[2] & 255) + ((var0[4] & 240) << 4);
      int var5 = (var0[5] & 255) + ((var0[7] & 240) << 4);
      return String.format("Clock %dMHz, Active Pixels %dx%d ", var3, var4, var5);
   }

   public static String getDescriptorRangeLimits(byte[] var0) {
      return String.format("Field Rate %d-%d Hz vertical, %d-%d Hz horizontal, Max clock: %d MHz", var0[5], var0[6], var0[7], var0[8], var0[9] * 10);
   }

   public static String getDescriptorText(byte[] var0) {
      return new String(Arrays.copyOfRange(var0, 4, 18), StandardCharsets.US_ASCII).trim();
   }

   public static String toString(byte[] var0) {
      StringBuilder var5 = new StringBuilder();
      var5.append("  Manuf. ID=").append(getManufacturerID(var0));
      var5.append(", Product ID=").append(getProductID(var0));
      var5.append(", ").append(isDigital(var0) ? "Digital" : "Analog");
      var5.append(", Serial=").append(getSerialNo(var0));
      var5.append(", ManufDate=").append(getWeek(var0) * 12 / 52 + 1).append('/').append(getYear(var0));
      var5.append(", EDID v").append(getVersion(var0));
      int var6 = getHcm(var0);
      int var7 = getVcm(var0);
      var5.append(String.format("%n  %d x %d cm (%.1f x %.1f in)", var6, var7, var6 / 2.54, var7 / 2.54));
      byte[][] var8 = getDescriptors(var0);

      for (byte[] var12 : var8) {
         switch (getDescriptorType(var12)) {
            case 250:
               var5.append("\n  Standard Timing ID: ").append(ParseUtil.byteArrayToHexString(var12));
               break;
            case 251:
               var5.append("\n  White Point Data: ").append(ParseUtil.byteArrayToHexString(var12));
               break;
            case 252:
               var5.append("\n  Monitor Name: ").append(getDescriptorText(var12));
               break;
            case 253:
               var5.append("\n  Range Limits: ").append(getDescriptorRangeLimits(var12));
               break;
            case 254:
               var5.append("\n  Unspecified Text: ").append(getDescriptorText(var12));
               break;
            case 255:
               var5.append("\n  Serial Number: ").append(getDescriptorText(var12));
               break;
            default:
               if (getDescriptorType(var12) <= 15 && getDescriptorType(var12) >= 0) {
                  var5.append("\n  Manufacturer Data: ").append(ParseUtil.byteArrayToHexString(var12));
               } else {
                  var5.append("\n  Preferred Timing: ").append(getTimingDescriptor(var12));
               }
         }
      }

      return var5.toString();
   }
}
