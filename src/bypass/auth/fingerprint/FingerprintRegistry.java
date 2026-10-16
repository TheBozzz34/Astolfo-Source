package bypass.auth.fingerprint;

public interface FingerprintRegistry {
   int VALID = 1;
   int INVALID = -1;
   int NOT_VALIDATED = 0;

   void init();

   int countValidatedEntries();

   int getEntryHash(String string);

   int getCombinedHash();

   void register(String string, FingerprintEntry<?> fingerprintEntry);

   void registerSupplied(String string, FingerprintEntrySupplier fingerprintEntrySupplier);

   int validate(String string);

   int validateAll();

   boolean remove(String string);

   void invalidateAll();
}
