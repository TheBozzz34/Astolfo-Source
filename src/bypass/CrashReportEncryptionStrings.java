package bypass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class CrashReportEncryptionStrings {
   public static String i = "ARCFOUR";
   public static String o = "1CnjkruPP9O9OrHt";
   private static final long a = 119429342183651L;

   public static String R(String string) {
      String string2 = "n7bPbNlJA9Pemq0fAIxjjg==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(i);
         SecretKeySpec secretKeySpec = new SecretKeySpec(o.getBytes(), i);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, i);
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
