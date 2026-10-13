package bypass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class CrashReportEncryptionStrings {
   public static String i = "ARCFOUR";
   public static String o = "1CnjkruPP9O9OrHt";
   private static final long a = 119429342183651L;

   public static String R(String var0) {
      String var4 = "n7bPbNlJA9Pemq0fAIxjjg==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(i);
         SecretKeySpec var6 = new SecretKeySpec(o.getBytes(), i);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, i);
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
