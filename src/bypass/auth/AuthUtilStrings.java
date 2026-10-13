package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AuthUtilStrings {
   public static String f = "ARCFOUR";
   public static String T = "ojC1QErhI5tL7NKQ";
   private static final long a = 126257719304853L;

   public static String U(String var0) {
      String var4 = "kXwv2mOiJc3Cs8IPnsSIkg==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(f);
         SecretKeySpec var6 = new SecretKeySpec(T.getBytes(), f);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, f);
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
