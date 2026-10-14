package bypass.util.collection;

import java.util.Map.Entry;

public class ShapedMapEntry<K, V> implements Entry<K, V> {
   public final K b;
   public V f;
   private final ShapedMap<K, V> B;
   private static final long a = 120791272448374L;

   public ShapedMapEntry(ShapedMap<K, V> shapedMap, K object, V object2) {
      this.B = shapedMap;
      this.b = (K)object;
      this.f = (V)object2;
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
   public V setValue(V object) {
      this.f = (V)object;
      return this.B.put(this.b, (V)object);
   }

   private static boolean t(Object object, Object object2) {
      return object == null ? object2 == null : object.equals(object2);
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof Entry)) {
         return false;
      }

      Entry entry = (Entry)object;
      return t(this.getKey(), entry.getKey()) && t(this.f, entry.getValue());
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
