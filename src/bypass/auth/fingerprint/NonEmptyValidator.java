package bypass.auth.fingerprint;

public final class NonEmptyValidator implements FingerprintValidator<String> {
   private static final long a = 46250285583161L;

   @Override
   public boolean test(FingerprintEntry var1) {
      if (var1 instanceof EmptyFingerprintEntry) {
         return false;
      } else {
         Object var5 = var1.z();
         if (var5 instanceof String) {
            String var6 = (String)var5;
            return !var6.isEmpty();
         } else {
            return var5 != null;
         }
      }
   }
}
