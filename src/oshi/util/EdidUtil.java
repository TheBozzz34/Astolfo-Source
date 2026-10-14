package oshi.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class EdidUtil {
   private EdidUtil() {
   }

   public static String getManufacturerID(byte[] edid) {
      String string = String.format("%8s%8s", Integer.toBinaryString(edid[8] & 255), Integer.toBinaryString(edid[9] & 255)).replace(' ', '0');
      return String.format(
            "%s%s%s",
            (char)(64 + Integer.parseInt(string.substring(1, 6), 2)),
            (char)(64 + Integer.parseInt(string.substring(7, 11), 2)),
            (char)(64 + Integer.parseInt(string.substring(12, 16), 2))
         )
         .replace("@", "");
   }

   public static String getProductID(byte[] edid) {
      return Integer.toHexString(ByteBuffer.wrap(Arrays.copyOfRange(edid, 10, 12)).order(ByteOrder.LITTLE_ENDIAN).getShort() & 65535);
   }

   public static String getSerialNo(byte[] edid) {
      return String.format(
         "%s%s%s%s", getAlphaNumericOrHex(edid[15]), getAlphaNumericOrHex(edid[14]), getAlphaNumericOrHex(edid[13]), getAlphaNumericOrHex(edid[12])
      );
   }

   private static String getAlphaNumericOrHex(byte b) {
      return Character.isLetterOrDigit((char)b) ? String.format("%s", (char)b) : String.format("%02X", b);
   }

   public static byte getWeek(byte[] edid) {
      return edid[16];
   }

   public static int getYear(byte[] edid) {
      byte b = edid[17];
      return b + 1990;
   }

   public static String getVersion(byte[] edid) {
      return edid[18] + "." + edid[19];
   }

   public static boolean isDigital(byte[] edid) {
      return 1 == (edid[20] & 255) >> 7;
   }

   public static int getHcm(byte[] edid) {
      return edid[21];
   }

   public static int getVcm(byte[] edid) {
      return edid[22];
   }

   public static byte[][] getDescriptors(byte[] edid) {
      byte[][] bytess = new byte[4][18];

      for (int i = 0; i < bytess.length; i++) {
         System.arraycopy(edid, 54 + 18 * i, bytess[i], 0, 18);
      }

      return bytess;
   }

   public static int getDescriptorType(byte[] desc) {
      return ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 4)).getInt();
   }

   public static String getTimingDescriptor(byte[] desc) {
      int i = ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;
      int j = (desc[2] & 255) + ((desc[4] & 240) << 4);
      int k = (desc[5] & 255) + ((desc[7] & 240) << 4);
      return String.format("Clock %dMHz, Active Pixels %dx%d ", i, j, k);
   }

   public static String getDescriptorRangeLimits(byte[] desc) {
      return String.format("Field Rate %d-%d Hz vertical, %d-%d Hz horizontal, Max clock: %d MHz", desc[5], desc[6], desc[7], desc[8], desc[9] * 10);
   }

   public static String getDescriptorText(byte[] desc) {
      return new String(Arrays.copyOfRange(desc, 4, 18), StandardCharsets.US_ASCII).trim();
   }

   public static String toString(byte[] edid) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("  Manuf. ID=").append(getManufacturerID(edid));
      stringBuilder.append(", Product ID=").append(getProductID(edid));
      stringBuilder.append(", ").append(isDigital(edid) ? "Digital" : "Analog");
      stringBuilder.append(", Serial=").append(getSerialNo(edid));
      stringBuilder.append(", ManufDate=").append(getWeek(edid) * 12 / 52 + 1).append('/').append(getYear(edid));
      stringBuilder.append(", EDID v").append(getVersion(edid));
      int i = getHcm(edid);
      int j = getVcm(edid);
      stringBuilder.append(String.format("%n  %d x %d cm (%.1f x %.1f in)", i, j, i / 2.54, j / 2.54));
      byte[][] bytess = getDescriptors(edid);

      for (byte[] bytes : bytess) {
         switch (getDescriptorType(bytes)) {
            case 250:
               stringBuilder.append("\n  Standard Timing ID: ").append(ParseUtil.byteArrayToHexString(bytes));
               break;
            case 251:
               stringBuilder.append("\n  White Point Data: ").append(ParseUtil.byteArrayToHexString(bytes));
               break;
            case 252:
               stringBuilder.append("\n  Monitor Name: ").append(getDescriptorText(bytes));
               break;
            case 253:
               stringBuilder.append("\n  Range Limits: ").append(getDescriptorRangeLimits(bytes));
               break;
            case 254:
               stringBuilder.append("\n  Unspecified Text: ").append(getDescriptorText(bytes));
               break;
            case 255:
               stringBuilder.append("\n  Serial Number: ").append(getDescriptorText(bytes));
               break;
            default:
               if (getDescriptorType(bytes) <= 15 && getDescriptorType(bytes) >= 0) {
                  stringBuilder.append("\n  Manufacturer Data: ").append(ParseUtil.byteArrayToHexString(bytes));
               } else {
                  stringBuilder.append("\n  Preferred Timing: ").append(getTimingDescriptor(bytes));
               }
         }
      }

      return stringBuilder.toString();
   }
}
