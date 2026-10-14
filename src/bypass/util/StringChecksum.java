package bypass.util;

import java.nio.charset.StandardCharsets;

public class StringChecksum {
   private static final long a = 121449699069703L;
   private static final long b = 2433289489343316095L;

   public static int Y(String string) {
      return U(string.getBytes(StandardCharsets.UTF_8));
   }

   private static int U(byte[] bytes) {
      byte b2 = 0;

      for (byte b3 : bytes) {
         b2 += b3;
      }

      return b2 % (int)b;
   }

   public static byte[] j(int i, String string) {
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

      for (int k = 0; k < bytes.length; k++) {
         bytes[k] = (byte)(bytes[k] ^ i);
      }

      return bytes;
   }
}
