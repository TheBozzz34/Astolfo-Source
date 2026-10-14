package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher2 {
   public static String R = "ARCFOUR";
   public static String H = "lhD4ABtttNP970BQ";
   private static final long a = 80159833704612L;

   public static String p(String string) {
      String string2 = "lP+JGuYVJIOeVCuHl0drkA==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(R);
         SecretKeySpec secretKeySpec = new SecretKeySpec(H.getBytes(), R);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, R);
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
