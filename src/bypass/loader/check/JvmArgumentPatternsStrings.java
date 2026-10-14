package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class JvmArgumentPatternsStrings {
   public static String J = "ARCFOUR";
   public static String D = "JQikA228jUduWzPV";

   public static String N(String string) {
      String string2 = "v6uAv319MNIj796GBxilAw==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(J);
         SecretKeySpec secretKeySpec = new SecretKeySpec(D.getBytes(), J);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, J);
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
