package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AuthUtilStrings {
   public static String algorithm = "ARCFOUR";
   public static String outerKey = "ojC1QErhI5tL7NKQ";

   public static String decrypt(String string) {
      String string2 = "kXwv2mOiJc3Cs8IPnsSIkg==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(algorithm);
         SecretKeySpec secretKeySpec = new SecretKeySpec(outerKey.getBytes(), algorithm);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, algorithm);
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
