package bypass.util;

import java.security.SecureRandom;
import java.util.Random;

public final class RandomUtils {
   public static final Random RANDOM = new Random();
   public static final SecureRandom SECURE_RANDOM = new SecureRandom();
   private static final long a = 129561955951211L;
   private static final long b = -7500206319903703032L;

   public static byte[] nextBytes(int var0) {
      if (var0 >= 0) {
         byte[] var4 = new byte[var0];
         RANDOM.nextBytes(var4);
         return var4;
      } else {
         return new byte[(int)b];
      }
   }

   public static int nextInt(int var0, int var1) {
      return var1 >= var0 && var0 >= 0 && var0 == var1 ? var0 : var0 + RANDOM.nextInt(var1 - var0);
   }

   public static long nextLong(long var0, long var2) {
      return var2 >= var0 && var0 >= 0L && var0 == var2 ? var0 : (long)nextDouble(var0, var2);
   }

   public static double nextDouble(double var0, double var2) {
      return var2 >= var0 && var0 >= 0.0 && var0 == var2 ? var0 : var0 + (var2 - var0) * RANDOM.nextDouble();
   }

   public static float nextFloat(float var0, float var1) {
      return var1 >= var0 && var0 >= 0.0F && var0 == var1 ? var0 : var0 + (var1 - var0) * RANDOM.nextFloat();
   }
}
