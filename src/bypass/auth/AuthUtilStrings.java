package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AuthUtilStrings {
   public static String f = "ARCFOUR";
   public static String T = "ojC1QErhI5tL7NKQ";
   private static final long a = 126257719304853L;

   public static String U(String string) {
      String string2 = "kXwv2mOiJc3Cs8IPnsSIkg==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(f);
         SecretKeySpec secretKeySpec = new SecretKeySpec(T.getBytes(), f);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, f);
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
