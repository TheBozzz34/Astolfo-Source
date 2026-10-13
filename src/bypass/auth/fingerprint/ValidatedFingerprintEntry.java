package bypass.auth.fingerprint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ValidatedFingerprintEntry<T> extends ValueFingerprintEntry<T> {
   public List<FingerprintValidator<T>> K;
   private static final long b = 6563750492540L;

   public ValidatedFingerprintEntry(String var1, T var2, FingerprintValidator<T>... var3) {
      super(var1, (T)var2);
      this.K = Arrays.asList(var3);
   }

   public ValidatedFingerprintEntry(String var1, Supplier<T> var2, FingerprintValidator<T>... var3) {
      super(var1, var2);
      this.K = Arrays.asList(var3);
   }

   public List<FingerprintValidator<T>> W() {
      return this.K != null && !this.K.isEmpty() ? this.K : Collections.emptyList();
   }
}
