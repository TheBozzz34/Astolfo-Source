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
      public final Supplier delegate;
      public volatile Object value;
      public volatile long expirationNanos;
      public final Supplier original;
      public final long y;

      public ExpiringSupplier(Supplier supplier, long l) {
         this.original = supplier;
         this.y = l;
         this.delegate = this.original;
      }

      @Override
      public Object get() {
         long l = this.expirationNanos;
         long m = System.nanoTime();
         if (l == 0L || this.y >= 0L && m - l >= 0L) {
            synchronized (this) {
               if (l == this.expirationNanos) {
                  Object object = this.delegate.get();
                  this.value = object;
                  l = m + this.y;
                  this.expirationNanos = l == 0L ? 1L : l;
                  return object;
               }
            }
         }

         return this.value;
      }
   }
}
