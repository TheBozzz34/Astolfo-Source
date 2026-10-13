package bypass.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class AddressCipher1 {
   public static String J = "ARCFOUR";
   public static String G = "2NZewLRIB1HWVnDj";
   private static final long a = 26645090906762L;

   public static String O(String var0) {
      String var4 = "zMb/dV/oz8GQ9N3FbjADPg==";

      String var3;
      try {
         Cipher var5 = Cipher.getInstance(J);
         SecretKeySpec var6 = new SecretKeySpec(G.getBytes(), J);
         var5.init(2, var6);
         byte[] var7 = new BASE64Decoder().decodeBuffer(var4);
         byte[] var8 = var5.doFinal(var7);
         SecretKeySpec var9 = new SecretKeySpec(var8, J);
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
