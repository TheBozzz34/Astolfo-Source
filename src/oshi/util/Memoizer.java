package oshi.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Memoizer {
   private static final Supplier<Long> defaultExpirationNanos = memoize(Memoizer::queryExpirationConfig, TimeUnit.MINUTES.toNanos(1L));
   private static final long a = 29827839104500L;
   private static final long b = -6758284087120625364L;

   private Memoizer() {
   }

   private static long queryExpirationConfig() {
      return TimeUnit.MILLISECONDS.toNanos(GlobalConfig.get("d", (int)b));
   }

   public static long defaultExpiration() {
      return defaultExpirationNanos.get();
   }

   public static <T> Supplier<T> memoize(Supplier<T> var0, long var1) {
      return new Memoizer.ExpiringSupplier(var0, var1);
   }

   public static <T> Supplier<T> memoize(Supplier<T> var0) {
      return memoize(var0, 5496873053650840144L);
   }

   public static final class ExpiringSupplier implements Supplier {
      public final Supplier g;
      public volatile Object F;
      public volatile long m;
      public final Supplier E;
      public final long y;
      private static final long a = 68859335861244L;

      public ExpiringSupplier(Supplier var1, long var2) {
         this.E = var1;
         this.y = var2;
         this.g = this.E;
      }

      @Override
      public Object get() {
         long var4 = this.m;
         long var6 = System.nanoTime();
         if (var4 == 0L || this.y >= 0L && var6 - var4 >= 0L) {
            synchronized (this) {
               if (var4 == this.m) {
                  Object var9 = this.g.get();
                  this.F = var9;
                  var4 = var6 + this.y;
                  this.m = var4 == 0L ? 1L : var4;
                  return var9;
               }
            }
         }

         return this.F;
      }
   }
}
