package bypass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class CrashReportEncryptionStrings {
   public static String cipherAlgorithm = "ARCFOUR";
   public static String rc4Key = "1CnjkruPP9O9OrHt";

   public static String decrypt(String string) {
      String string2 = "n7bPbNlJA9Pemq0fAIxjjg==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(cipherAlgorithm);
         SecretKeySpec secretKeySpec = new SecretKeySpec(rc4Key.getBytes(), cipherAlgorithm);
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
