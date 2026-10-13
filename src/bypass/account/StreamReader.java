package bypass.account;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class StreamReader {
   private static final StreamReader e = new StreamReader();
   private static final int z = 8192;
   private static final int D = 2147483639;
   private static final long a = 76113226641445L;

   public byte[] M(InputStream var1) throws IOException {
      byte[] var6 = new byte[8192];
      int var7 = var6.length;
      int var8 = 0;

      while (true) {
         int var9;
         for (; (var9 = var1.read(var6, var8, var7 - var8)) <= 0; var6 = Arrays.copyOf(var6, var7)) {
            if (var9 < 0) {
               return var7 == var8 ? var6 : Arrays.copyOf(var6, var8);
            }

            if (var7 <= 2147483639 - var7) {
               var7 <<= 1;
            } else {
               if (var7 == 2147483639) {
                  throw new OutOfMemoryError("Required array size too large");
               }

               var7 = 2147483639;
            }
         }

         var8 += var9;
      }
   }

   public static StreamReader o() {
      return e;
   }

   static {
      long var14 = a ^ 8113273731682L;
      long var10000 = var14 ^ 4084096115636478129L;
      var10000 = var14 ^ 6931698921408695831L;
   }
}
