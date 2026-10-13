package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class ProcessNamesStrings {
   public static String v = "ARCFOUR";
   public static String p = "Zf1Ma8jiWK52a9o5";
   private static final long a = 73842149703416L;

   public static String c(String var0) {
      String var4 = "22TQFKRfe4GoC/rP+Sfz4Q==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(v);
         SecretKeySpec var6 = new SecretKeySpec(p.getBytes(), v);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, v);
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
