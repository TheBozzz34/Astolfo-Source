package bypass.unused;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class UnusedStrings {
   public static String o = "ARCFOUR";
   public static String j = "o2vtyP4UjkjWWCQc";
   private static final long a = 776894313864L;

   public static String o(String var0) {
      String var4 = "VoBvIjDOl3xXzxlUfETSfQ==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(o);
         SecretKeySpec var6 = new SecretKeySpec(j.getBytes(), o);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, o);
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
