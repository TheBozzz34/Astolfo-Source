package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class IntegerCacheScramblerStrings {
   public static String j = "ARCFOUR";
   public static String A = "PtpvDfZKhTXrx8Qi";
   private static final long a = 5369934497344L;

   public static String z(String var0) {
      String var4 = "PJc/L4cQHOGvRKrnof+rsQ==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(j);
         SecretKeySpec var6 = new SecretKeySpec(A.getBytes(), j);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, j);
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
