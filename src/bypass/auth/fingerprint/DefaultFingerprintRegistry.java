package bypass.auth.fingerprint;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultFingerprintRegistry implements FingerprintRegistry {
   private static final Map<String, FingerprintEntry<?>> k = new ConcurrentHashMap<>();
   private static final long a = 86113207517039L;

   @Override
   public void e() {
   }

   @Override
   public int l() {
      byte var4 = 0;

      for (FingerprintEntry var6 : k.values()) {
         var4 += var6 instanceof ValidatedFingerprintEntry ? 1 : 0;
      }

      return var4;
   }

   @Override
   public int o(String var1) {
      return k.containsKey(var1) ? k.get(var1).o() : -1;
   }

   @Override
   public int C() {
      int var4 = 0;

      for (FingerprintEntry var6 : k.values()) {
         var4 += var6.o();
      }

      return var4;
   }

   @Override
   public void Q(String var1, FingerprintEntry<?> var2) {
      if (!k.containsKey(var1)) {
         k.put(var1, var2);
      }
   }

   @Override
   public void s(String var1, FingerprintEntrySupplier var2) {
      if (!k.containsKey(var1)) {
         k.put(var1, var2.get());
      }
   }

   @Override
   public int F(String var1) {
      FingerprintEntry var5 = k.get(var1);
      if (!(var5 instanceof ValidatedFingerprintEntry)) {
         return 0;
      }

      ValidatedFingerprintEntry var6 = (ValidatedFingerprintEntry)var5;
      List var7 = var6.W();
      boolean var8 = false;
      if (!var7.isEmpty()) {
         for (Object var10$raw : var7) {
            FingerprintValidator var10 = (FingerprintValidator)var10$raw;
            var8 = var10.test(var5);
            if (!var8) {
               break;
            }
         }
      }

      return var8 ? 1 : -1;
   }

   @Override
   public int B() {
      int var5 = 0;

      for (String var7 : k.keySet()) {
         var5 += this.F(var7);
      }

      return var5;
   }

   @Override
   public boolean f(String var1) {
      return k.containsKey(var1) ? k.remove(var1) != null : false;
   }

   @Override
   public void invalidateAll() {
      k.replaceAll((var0, var1) -> new EmptyFingerprintEntry());
   }
}
