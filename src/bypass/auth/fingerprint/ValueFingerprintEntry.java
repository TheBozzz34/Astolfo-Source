package bypass.auth.fingerprint;

import java.util.Objects;
import java.util.function.Supplier;

public class ValueFingerprintEntry<T> implements FingerprintEntry<T> {
   private Supplier<T> supplier;
   public String name;
   public T value;

   public ValueFingerprintEntry(String string, T object) {
      this.name = string;
      this.value = (T)object;
   }

   public ValueFingerprintEntry(String string, Supplier<T> supplier) {
      this(string, (T)supplier.get());
      this.supplier = supplier;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public int getHash() {
      return Objects.hash(this.name, this.value);
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public void refresh() {
      if (this.supplier != null) {
         this.value = this.supplier.get();
      }
   }
}
