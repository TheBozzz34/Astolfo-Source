package bypass.auth.fingerprint;

import java.util.Objects;
import java.util.function.Supplier;

public class ValueFingerprintEntry<T> implements FingerprintEntry<T> {
   private Supplier<T> H;
   public String E;
   public T l;
   private static final long a = 101741983376718L;

   public ValueFingerprintEntry(String string, T object) {
      this.E = string;
      this.l = (T)object;
   }

   public ValueFingerprintEntry(String string, Supplier<T> supplier) {
      this(string, (T)supplier.get());
      this.H = supplier;
   }

   @Override
   public String c() {
      return this.E;
   }

   @Override
   public int o() {
      return Objects.hash(this.E, this.l);
   }

   @Override
   public T z() {
      return this.l;
   }

   @Override
   public void A() {
      if (this.H != null) {
         this.l = this.H.get();
      }
   }
}
