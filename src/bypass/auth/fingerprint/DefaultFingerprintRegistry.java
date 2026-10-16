package bypass.auth.fingerprint;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultFingerprintRegistry implements FingerprintRegistry {
   private static final Map<String, FingerprintEntry<?>> entries = new ConcurrentHashMap<>();

   @Override
   public void init() {
   }

   @Override
   public int countValidatedEntries() {
      byte b = 0;

      for (FingerprintEntry fingerprintEntry : entries.values()) {
         b += fingerprintEntry instanceof ValidatedFingerprintEntry ? 1 : 0;
      }

      return b;
   }

   @Override
   public int getEntryHash(String string) {
      return entries.containsKey(string) ? entries.get(string).getHash() : -1;
   }

   @Override
   public int getCombinedHash() {
      int i = 0;

      for (FingerprintEntry fingerprintEntry : entries.values()) {
         i += fingerprintEntry.getHash();
      }

      return i;
   }

   @Override
   public void register(String string, FingerprintEntry<?> fingerprintEntry) {
      if (!entries.containsKey(string)) {
         entries.put(string, fingerprintEntry);
      }
   }

   @Override
   public void registerSupplied(String string, FingerprintEntrySupplier fingerprintEntrySupplier) {
      if (!entries.containsKey(string)) {
         entries.put(string, fingerprintEntrySupplier.get());
      }
   }

   @Override
   public int validate(String string) {
      FingerprintEntry fingerprintEntry = entries.get(string);
      if (!(fingerprintEntry instanceof ValidatedFingerprintEntry)) {
         return 0;
      }

      ValidatedFingerprintEntry validatedFingerprintEntry = (ValidatedFingerprintEntry)fingerprintEntry;
      List list = validatedFingerprintEntry.getValidators();
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
   public int validateAll() {
      int i = 0;

      for (String string : entries.keySet()) {
         i += this.validate(string);
      }

      return i;
   }

   @Override
   public boolean remove(String string) {
      return entries.containsKey(string) ? entries.remove(string) != null : false;
   }

   @Override
   public void invalidateAll() {
      entries.replaceAll((string, fingerprintEntry) -> new EmptyFingerprintEntry());
   }
}
