package bypass.auth.fingerprint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ValidatedFingerprintEntry<T> extends ValueFingerprintEntry<T> {
   public List<FingerprintValidator<T>> K;

   public ValidatedFingerprintEntry(String string, T object, FingerprintValidator<T>... fingerprintValidators) {
      super(string, (T)object);
      this.K = Arrays.asList(fingerprintValidators);
   }

   public ValidatedFingerprintEntry(String string, Supplier<T> supplier, FingerprintValidator<T>... fingerprintValidators) {
      super(string, supplier);
      this.K = Arrays.asList(fingerprintValidators);
   }

   public List<FingerprintValidator<T>> W() {
      return this.K != null && !this.K.isEmpty() ? this.K : Collections.emptyList();
   }
}
