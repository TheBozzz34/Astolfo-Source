package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher4 {
   public static String h = "ARCFOUR";
   public static String r = "pxRZqlSE5PkcWLs2";

   public static String e(String string) {
      String string2 = "43TmUHtSaPw35tncFJJTDQ==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(h);
         SecretKeySpec secretKeySpec = new SecretKeySpec(r.getBytes(), h);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, h);
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
