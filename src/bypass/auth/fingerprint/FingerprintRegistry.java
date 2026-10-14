package bypass.auth.fingerprint;

public interface FingerprintRegistry {
   int Z = 1;
   int F = -1;
   int r = 0;

   void e();

   int l();

   int o(String string);

   int C();

   void Q(String string, FingerprintEntry<?> fingerprintEntry);

   void s(String string, FingerprintEntrySupplier fingerprintEntrySupplier);

   int F(String string);

   int B();

   boolean f(String string);

   void invalidateAll();
}
