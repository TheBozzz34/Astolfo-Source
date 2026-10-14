package bypass.unused;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class UnusedStrings {
   public static String o = "ARCFOUR";
   public static String j = "o2vtyP4UjkjWWCQc";
   private static final long a = 776894313864L;

   public static String o(String string) {
      String string2 = "VoBvIjDOl3xXzxlUfETSfQ==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(o);
         SecretKeySpec secretKeySpec = new SecretKeySpec(j.getBytes(), o);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, o);
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
