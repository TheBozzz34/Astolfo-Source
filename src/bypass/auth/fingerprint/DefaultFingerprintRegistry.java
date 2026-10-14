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
      byte b = 0;

      for (FingerprintEntry fingerprintEntry : k.values()) {
         b += fingerprintEntry instanceof ValidatedFingerprintEntry ? 1 : 0;
      }

      return b;
   }

   @Override
   public int o(String string) {
      return k.containsKey(string) ? k.get(string).o() : -1;
   }

   @Override
   public int C() {
      int i = 0;

      for (FingerprintEntry fingerprintEntry : k.values()) {
         i += fingerprintEntry.o();
      }

      return i;
   }

   @Override
   public void Q(String string, FingerprintEntry<?> fingerprintEntry) {
      if (!k.containsKey(string)) {
         k.put(string, fingerprintEntry);
      }
   }

   @Override
   public void s(String string, FingerprintEntrySupplier fingerprintEntrySupplier) {
      if (!k.containsKey(string)) {
         k.put(string, fingerprintEntrySupplier.get());
      }
   }

   @Override
   public int F(String string) {
      FingerprintEntry fingerprintEntry = k.get(string);
      if (!(fingerprintEntry instanceof ValidatedFingerprintEntry)) {
         return 0;
      }

      ValidatedFingerprintEntry validatedFingerprintEntry = (ValidatedFingerprintEntry)fingerprintEntry;
      List list = validatedFingerprintEntry.W();
      boolean bl = false;
      if (!list.isEmpty()) {
         for (Object object : list) {
            FingerprintValidator fingerprintValidator = (FingerprintValidator)object;
            bl = fingerprintValidator.test(fingerprintEntry);
            if (!bl) {
               break;
            }
         }
      }

      return bl ? 1 : -1;
   }

   @Override
   public int B() {
      int i = 0;

      for (String string : k.keySet()) {
         i += this.F(string);
      }

      return i;
   }

   @Override
   public boolean f(String string) {
      return k.containsKey(string) ? k.remove(string) != null : false;
   }

   @Override
   public void invalidateAll() {
      k.replaceAll((string, fingerprintEntry) -> new EmptyFingerprintEntry());
   }
}
