package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class IntegrityCheckStrings {
   public static String Q = "ARCFOUR";
   public static String J = "vybaTyBAMHXa8byx";
   private static final long a = 33831282967968L;

   public static String W(String var0) {
      String var4 = "ExRqUUtvhGVeyDhwm1aPng==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(Q);
         SecretKeySpec var6 = new SecretKeySpec(J.getBytes(), Q);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, Q);
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
