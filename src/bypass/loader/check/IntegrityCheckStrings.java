package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class IntegrityCheckStrings {
   public static String Q = "ARCFOUR";
   public static String J = "vybaTyBAMHXa8byx";
   private static final long a = 33831282967968L;

   public static String W(String string) {
      String string2 = "ExRqUUtvhGVeyDhwm1aPng==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(Q);
         SecretKeySpec secretKeySpec = new SecretKeySpec(J.getBytes(), Q);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, Q);
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
