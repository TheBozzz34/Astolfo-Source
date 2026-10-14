package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class JvmArgumentsCheckStrings {
   public static String m = "ARCFOUR";
   public static String S = "oR7dsJ1oVZ4aDYPR";
   private static final long a = 131374134622570L;

   public static String i(String string) {
      String string2 = "DiVanYz9SZ87dzHMIw+kGA==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(m);
         SecretKeySpec secretKeySpec = new SecretKeySpec(S.getBytes(), m);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, m);
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
