package bypass.auth.fingerprint;

import java.util.Objects;
import java.util.function.Supplier;

public class ValueFingerprintEntry<T> implements FingerprintEntry<T> {
   private Supplier<T> H;
   public String E;
   public T l;
   private static final long a = 101741983376718L;

   public ValueFingerprintEntry(String var1, T var2) {
      this.E = var1;
      this.l = (T)var2;
   }

   public ValueFingerprintEntry(String var1, Supplier<T> var2) {
      this(var1, (T)var2.get());
      this.H = var2;
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
