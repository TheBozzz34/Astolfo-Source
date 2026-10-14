package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class FileHashCheckStrings {
   public static String l = "ARCFOUR";
   public static String h = "Aw2OgNgtvup2XlDe";
   private static final long a = 95438398428852L;

   public static String s(String string) {
      String string2 = "yoeWyJ6Pn2HzUCR1QgtbTw==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(l);
         SecretKeySpec secretKeySpec = new SecretKeySpec(h.getBytes(), l);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, l);
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
