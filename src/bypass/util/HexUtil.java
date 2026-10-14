package bypass.util;

public class HexUtil {
   private static final char[] x = "0123456789abcdef".toCharArray();
   private static final long a = 3256426206891L;

   public static String Q(byte... bytes) {
      if (bytes == null) {
         return null;
      }

      char[] chars = new char[bytes.length * 2];
      int i = 0;

      for (byte b : bytes) {
         chars[i++] = x[(b & 255) >> 4];
         chars[i++] = x[b & 0xFF & 15];
      }

      return new String(chars);
   }

   public static byte[] e(String string) {
      if (string == null) {
         return null;
      }

      int i = string.length();
      byte[] bytes = new byte[i >> 1];

      for (int j = 0; j < i; j += 2) {
         bytes[j >> 1] = (byte)((Character.digit((int)string.charAt(j), 16) << 4) + Character.digit((int)string.charAt(j + 1), 16));
      }

      return bytes;
   }
}
