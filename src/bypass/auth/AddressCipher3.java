package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher3 {
   public static String o = "ARCFOUR";
   public static String i = "SSO4hzH6QdbHs8aB";
   private static final long a = 61516549032484L;

   public static String y(String string) {
      String string2 = "mmPmBPB41drPdH94ZCoucQ==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(o);
         SecretKeySpec secretKeySpec = new SecretKeySpec(i.getBytes(), o);
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
