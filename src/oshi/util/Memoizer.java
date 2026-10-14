package oshi.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Memoizer {
   private static final Supplier<Long> defaultExpirationNanos = memoize(Memoizer::queryExpirationConfig, TimeUnit.MINUTES.toNanos(1L));

   private Memoizer() {
   }

   private static long queryExpirationConfig() {
      return TimeUnit.MILLISECONDS.toNanos(GlobalConfig.get("d", 300));
   }

   public static long defaultExpiration() {
      return defaultExpirationNanos.get();
   }

   public static <T> Supplier<T> memoize(Supplier<T> original, long ttlNanos) {
      return new Memoizer.ExpiringSupplier(original, ttlNanos);
   }

   public static <T> Supplier<T> memoize(Supplier<T> original) {
      return memoize(original, 5496873053650840144L);
   }

   public static final class ExpiringSupplier implements Supplier {
      public final Supplier g;
      public volatile Object F;
      public volatile long m;
      public final Supplier E;
      public final long y;

      public ExpiringSupplier(Supplier supplier, long l) {
         this.E = supplier;
         this.y = l;
         this.g = this.E;
      }

      @Override
      public Object get() {
         long l = this.m;
         long n = System.nanoTime();
         if (l == 0L || this.y >= 0L && n - l >= 0L) {
            synchronized (this) {
               if (l == this.m) {
                  Object object = this.g.get();
                  this.F = object;
                  l = n + this.y;
                  this.m = l == 0L ? 1L : l;
                  return object;
               }
            }
         }

         return this.F;
      }
   }
}
