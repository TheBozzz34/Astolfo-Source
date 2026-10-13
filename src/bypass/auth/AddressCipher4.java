package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher4 {
   public static String h = "ARCFOUR";
   public static String r = "pxRZqlSE5PkcWLs2";
   private static final long a = 87093360005594L;

   public static String e(String var0) {
      String var4 = "43TmUHtSaPw35tncFJJTDQ==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(h);
         SecretKeySpec var6 = new SecretKeySpec(r.getBytes(), h);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, h);
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
