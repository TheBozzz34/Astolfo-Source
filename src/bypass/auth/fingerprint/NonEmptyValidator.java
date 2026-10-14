package bypass.auth.fingerprint;

public final class NonEmptyValidator implements FingerprintValidator<String> {
   private static final long a = 46250285583161L;

   @Override
   public boolean test(FingerprintEntry fingerprintEntry) {
      if (fingerprintEntry instanceof EmptyFingerprintEntry) {
         return false;
      } else {
         Object object = fingerprintEntry.z();
         if (object instanceof String) {
            String string = (String)object;
            return !string.isEmpty();
         } else {
            return object != null;
         }
      }
   }
}
