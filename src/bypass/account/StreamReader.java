package bypass.account;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class StreamReader {
   private static final StreamReader e = new StreamReader();
   private static final int z = 8192;
   private static final int D = 2147483639;

   public byte[] M(InputStream inputStream) throws IOException {
      byte[] bytes = new byte[8192];
      int i = bytes.length;
      int j = 0;

      while (true) {
         int k;
         for (; (k = inputStream.read(bytes, j, i - j)) <= 0; bytes = Arrays.copyOf(bytes, i)) {
            if (k < 0) {
               return i == j ? bytes : Arrays.copyOf(bytes, j);
            }

            if (i <= 2147483639 - i) {
               i <<= 1;
            } else {
               if (i == 2147483639) {
                  throw new OutOfMemoryError("Required array size too large");
               }

               i = 2147483639;
            }
         }

         j += k;
      }
   }

   public static StreamReader o() {
      return e;
   }
}
