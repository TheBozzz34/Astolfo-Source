package bypass.gui.screen;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class LoginScreenStrings {
   public static String cipherAlgorithm = "ARCFOUR";
   public static String masterKey = "32ijUUtMct7Iry1a";

   public static String decrypt(String string) {
      String string2 = "cB7F/oJYbmasxgH3CbDfRA==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(cipherAlgorithm);
         SecretKeySpec secretKeySpec = new SecretKeySpec(masterKey.getBytes(), cipherAlgorithm);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, cipherAlgorithm);
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
