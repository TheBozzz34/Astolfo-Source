package bypass.util;

import java.nio.charset.StandardCharsets;

public class StringChecksum {
   private static final long a = 121449699069703L;
   private static final long b = 2433289489343316095L;

   public static int Y(String var0) {
      return U(var0.getBytes(StandardCharsets.UTF_8));
   }

   private static int U(byte[] var0) {
      byte var4 = 0;

      for (byte var8 : var0) {
         var4 += var8;
      }

      return var4 % (int)b;
   }

   public static byte[] j(int var0, String var1) {
      byte[] var5 = var1.getBytes(StandardCharsets.UTF_8);

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = (byte)(var5[var6] ^ var0);
      }

      return var5;
   }
}
