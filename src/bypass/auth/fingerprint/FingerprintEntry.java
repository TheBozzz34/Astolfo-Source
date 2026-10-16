package bypass.auth.fingerprint;

public interface FingerprintEntry<T> {
   String getName();

   int getHash();

   T getValue();

   void refresh();
}
