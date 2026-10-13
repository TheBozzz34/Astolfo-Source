package bypass.auth.fingerprint;

public final class EmptyFingerprintEntry<T> implements FingerprintEntry<T> {
   private static final Object Q = null;
   private static final long a = 18989005692901L;

   @Override
   public String c() {
      return "NULL";
   }

   @Override
   public int o() {
      return -1;
   }

   @Override
   public T z() {
      return (T)Q;
   }

   @Override
   public void A() {
   }
}
