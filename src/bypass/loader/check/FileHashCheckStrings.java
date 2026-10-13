package bypass.loader.check;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class FileHashCheckStrings {
   public static String l = "ARCFOUR";
   public static String h = "Aw2OgNgtvup2XlDe";
   private static final long a = 95438398428852L;

   public static String s(String var0) {
      String var4 = "yoeWyJ6Pn2HzUCR1QgtbTw==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(l);
         SecretKeySpec var6 = new SecretKeySpec(h.getBytes(), l);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, l);
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
