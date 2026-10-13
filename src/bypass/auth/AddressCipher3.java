package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher3 {
   public static String o = "ARCFOUR";
   public static String i = "SSO4hzH6QdbHs8aB";
   private static final long a = 61516549032484L;

   public static String y(String var0) {
      String var4 = "mmPmBPB41drPdH94ZCoucQ==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(o);
         SecretKeySpec var6 = new SecretKeySpec(i.getBytes(), o);
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
