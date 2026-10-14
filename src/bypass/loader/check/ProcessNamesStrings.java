package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class ProcessNamesStrings {
   public static String v = "ARCFOUR";
   public static String p = "Zf1Ma8jiWK52a9o5";

   public static String c(String string) {
      String string2 = "22TQFKRfe4GoC/rP+Sfz4Q==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(v);
         SecretKeySpec secretKeySpec = new SecretKeySpec(p.getBytes(), v);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, v);
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
