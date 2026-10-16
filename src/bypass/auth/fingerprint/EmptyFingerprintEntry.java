package bypass.auth.fingerprint;

public final class EmptyFingerprintEntry<T> implements FingerprintEntry<T> {
   private static final Object NULL_VALUE = null;

   @Override
   public String getName() {
      return "NULL";
   }

   @Override
   public int getHash() {
      return -1;
   }

   @Override
   public T getValue() {
      return (T)NULL_VALUE;
   }

   @Override
   public void refresh() {
   }
}
