package bypass.auth.fingerprint;

import java.util.function.Predicate;

public interface FingerprintValidator<T> extends Predicate<FingerprintEntry<T>> {
   FingerprintValidator c = new NonEmptyValidator();
   FingerprintValidator r = new UserDirectoryValidator();

   boolean test(FingerprintEntry<T> fingerprintEntry);
}
