package bypass.auth.fingerprint;

import java.util.function.Supplier;

public interface FingerprintEntrySupplier<T> extends Supplier<FingerprintEntry<T>> {
   String MISSING = "MISSING";

   FingerprintEntry<T> get();
}
