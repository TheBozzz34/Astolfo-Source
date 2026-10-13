package bypass.util;

public class HexUtil {
   private static final char[] x = "0123456789abcdef".toCharArray();
   private static final long a = 3256426206891L;

   public static String Q(byte... var0) {
      if (var0 == null) {
         return null;
      }

      char[] var4 = new char[var0.length * 2];
      int var5 = 0;

      for (byte var9 : var0) {
         var4[var5++] = x[(var9 & 255) >> 4];
         var4[var5++] = x[var9 & 0xFF & 15];
      }

      return new String(var4);
   }

   public static byte[] e(String var0) {
      if (var0 == null) {
         return null;
      }

      int var4 = var0.length();
      byte[] var5 = new byte[var4 >> 1];

      for (int var6 = 0; var6 < var4; var6 += 2) {
         var5[var6 >> 1] = (byte)((Character.digit((int)var0.charAt(var6), 16) << 4) + Character.digit((int)var0.charAt(var6 + 1), 16));
      }

      return var5;
   }
}
