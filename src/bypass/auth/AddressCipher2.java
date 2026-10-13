package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher2 {
   public static String R = "ARCFOUR";
   public static String H = "lhD4ABtttNP970BQ";
   private static final long a = 80159833704612L;

   public static String p(String var0) {
      String var4 = "lP+JGuYVJIOeVCuHl0drkA==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(R);
         SecretKeySpec var6 = new SecretKeySpec(H.getBytes(), R);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, R);
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
