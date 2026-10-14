package bypass.util;

import java.nio.charset.StandardCharsets;

public class StringChecksum {
   public static int Y(String string) {
      return U(string.getBytes(StandardCharsets.UTF_8));
   }

   private static int U(byte[] bytes) {
      byte b = 0;

      for (byte b2 : bytes) {
         b += b2;
      }

      return b % 127;
   }

   public static byte[] j(int i, String string) {
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

      for (int k = 0; k < bytes.length; k++) {
         bytes[k] = (byte)(bytes[k] ^ i);
      }

      return bytes;
   }
}
