package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class IntegerCacheScramblerStrings {
   public static String j = "ARCFOUR";
   public static String A = "PtpvDfZKhTXrx8Qi";
   private static final long a = 5369934497344L;

   public static String z(String string) {
      String string2 = "PJc/L4cQHOGvRKrnof+rsQ==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(j);
         SecretKeySpec secretKeySpec = new SecretKeySpec(A.getBytes(), j);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, j);
         cipher.init(2, secretKeySpec2);
         byte[] bytes3 = new BASE64Decoder().decodeBuffer(string);
         byte[] bytes4 = cipher.doFinal(bytes3);
         string3 = new String(bytes4);
      } catch (Exception exception) {
         string3 = "";
      }

      return string3;
   }
}
