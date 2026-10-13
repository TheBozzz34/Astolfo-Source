package bypass.gui.screen;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class LoginScreenStrings {
   public static String s = "ARCFOUR";
   public static String Y = "32ijUUtMct7Iry1a";
   private static final long a = 74312188851271L;

   public static String s(String var0) {
      String var4 = "cB7F/oJYbmasxgH3CbDfRA==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(s);
         SecretKeySpec var6 = new SecretKeySpec(Y.getBytes(), s);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, s);
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
