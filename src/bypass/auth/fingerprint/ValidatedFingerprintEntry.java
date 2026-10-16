package bypass.auth.fingerprint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ValidatedFingerprintEntry<T> extends ValueFingerprintEntry<T> {
   public List<FingerprintValidator<T>> validators;

   public ValidatedFingerprintEntry(String string, T object, FingerprintValidator<T>... fingerprintValidators) {
      super(string, (T)object);
      this.validators = Arrays.asList(fingerprintValidators);
   }

   public ValidatedFingerprintEntry(String string, Supplier<T> supplier, FingerprintValidator<T>... fingerprintValidators) {
      super(string, supplier);
      this.validators = Arrays.asList(fingerprintValidators);
   }

   public List<FingerprintValidator<T>> getValidators() {
      return this.validators != null && !this.validators.isEmpty() ? this.validators : Collections.emptyList();
   }
}
