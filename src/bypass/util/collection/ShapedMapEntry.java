package bypass.util.collection;

import java.util.Map.Entry;

public class ShapedMapEntry<K, V> implements Entry<K, V> {
   public final K b;
   public V f;
   private final ShapedMap<K, V> B;
   private static final long a = 120791272448374L;

   public ShapedMapEntry(ShapedMap<K, V> var1, K var2, V var3) {
      this.B = var1;
      this.b = (K)var2;
      this.f = (V)var3;
   }

   @Override
   public K getKey() {
      return (K)MapShape.f(this.B.shape, this.b);
   }

   @Override
   public V getValue() {
      return this.f;
   }

   @Override
   public V setValue(V var1) {
      this.f = (V)var1;
      return this.B.put(this.b, (V)var1);
   }

   private static boolean t(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      }

      Entry var5 = (Entry)var1;
      return t(this.getKey(), var5.getKey()) && t(this.f, var5.getValue());
   }

   @Override
   public int hashCode() {
      return (this.b == MapShape.getNULL_KEY() ? 0 : this.b.hashCode()) ^ (this.f == null ? 0 : this.f.hashCode());
   }

   @Override
   public String toString() {
      return MapShape.f(this.B.shape, this.b) + "=" + this.f;
   }
}
