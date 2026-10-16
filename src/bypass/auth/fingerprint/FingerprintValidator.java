package bypass.auth.fingerprint;

import java.util.function.Predicate;

public interface FingerprintValidator<T> extends Predicate<FingerprintEntry<T>> {
   FingerprintValidator NON_EMPTY = new NonEmptyValidator();
   FingerprintValidator USER_DIRECTORY = new UserDirectoryValidator();

   boolean test(FingerprintEntry<T> fingerprintEntry);
}
