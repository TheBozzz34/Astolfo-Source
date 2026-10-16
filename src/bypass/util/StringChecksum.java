package bypass.util;

import java.nio.charset.StandardCharsets;

public class StringChecksum {
   public static int checksum(String string) {
      return sumChecksum(string.getBytes(StandardCharsets.UTF_8));
   }

   private static int sumChecksum(byte[] bytes) {
      byte b = 0;

      for (byte b2 : bytes) {
         b += b2;
      }

      return b % 127;
   }

   public static byte[] xorEncode(int i, String string) {
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

      for (int j = 0; j < bytes.length; j++) {
         bytes[j] = (byte)(bytes[j] ^ i);
      }

      return bytes;
   }
}
