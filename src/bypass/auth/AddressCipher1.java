package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher1 {
   public static String algorithm = "ARCFOUR";
   public static String outerKey = "2NZewLRIB1HWVnDj";

   public static String decrypt(String string) {
      String string2 = "zMb/dV/oz8GQ9N3FbjADPg==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(algorithm);
         SecretKeySpec secretKeySpec = new SecretKeySpec(outerKey.getBytes(), algorithm);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, algorithm);
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
