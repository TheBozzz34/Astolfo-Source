package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class JvmArgumentsPropertyStrings {
   public static String e = "ARCFOUR";
   public static String f = "drivrdIDy6WHVtuC";

   public static String J(String string) {
      String string2 = "MtKrtxSGr32WNymM4+xjlw==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(e);
         SecretKeySpec secretKeySpec = new SecretKeySpec(f.getBytes(), e);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, e);
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
