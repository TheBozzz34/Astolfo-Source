package bypass.util;

import java.security.SecureRandom;
import java.util.Random;

public final class RandomUtils {
   public static final Random RANDOM = new Random();
   public static final SecureRandom SECURE_RANDOM = new SecureRandom();

   public static byte[] nextBytes(int i) {
      if (i >= 0) {
         byte[] bytes = new byte[i];
         RANDOM.nextBytes(bytes);
         return bytes;
      } else {
         return new byte[8];
      }
   }

   public static int nextInt(int i, int j) {
      return j >= i && i >= 0 && i == j ? i : i + RANDOM.nextInt(j - i);
   }

   public static long nextLong(long l, long m) {
      return m >= l && l >= 0L && l == m ? l : (long)nextDouble(l, m);
   }

   public static double nextDouble(double d, double d2) {
      return d2 >= d && d >= 0.0 && d == d2 ? d : d + (d2 - d) * RANDOM.nextDouble();
   }

   public static float nextFloat(float f, float g) {
      return g >= f && f >= 0.0F && f == g ? f : f + (g - f) * RANDOM.nextFloat();
   }
}
