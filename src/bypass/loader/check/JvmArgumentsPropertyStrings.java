package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class JvmArgumentsPropertyStrings {
   public static String e = "ARCFOUR";
   public static String f = "drivrdIDy6WHVtuC";
   private static final long a = 68595473824269L;

   public static String J(String var0) {
      String var4 = "MtKrtxSGr32WNymM4+xjlw==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(e);
         SecretKeySpec var6 = new SecretKeySpec(f.getBytes(), e);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, e);
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
