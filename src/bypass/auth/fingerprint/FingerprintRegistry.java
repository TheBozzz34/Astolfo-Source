package bypass.auth.fingerprint;

public interface FingerprintRegistry {
   int Z = 1;
   int F = -1;
   int r = 0;

   void e();

   int l();

   int o(String var1);

   int C();

   void Q(String var1, FingerprintEntry<?> var2);

   void s(String var1, FingerprintEntrySupplier var2);

   int F(String var1);

   int B();

   boolean f(String var1);

   void invalidateAll();
}
