package bypass.gui.screen;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class LoginScreenStrings {
   public static String s = "ARCFOUR";
   public static String Y = "32ijUUtMct7Iry1a";
   private static final long a = 74312188851271L;

   public static String s(String string) {
      String string2 = "cB7F/oJYbmasxgH3CbDfRA==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(s);
         SecretKeySpec secretKeySpec = new SecretKeySpec(Y.getBytes(), s);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, s);
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
