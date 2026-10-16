package bypass.util.collection;

import java.util.Map.Entry;

public class ShapedMapEntry<K, V> implements Entry<K, V> {
   public final K key;
   public V value;
   private final ShapedMap<K, V> map;

   public ShapedMapEntry(ShapedMap<K, V> shapedMap, K object, V object2) {
      this.map = shapedMap;
      this.key = (K)object;
      this.value = (V)object2;
   }

   @Override
   public K getKey() {
      return (K)MapShape.unmaskNullStatic(this.map.shape, this.key);
   }

   @Override
   public V getValue() {
      return this.value;
   }

   @Override
   public V setValue(V object) {
      this.value = (V)object;
      return this.map.put(this.key, (V)object);
   }

   private static boolean objectsEqual(Object object, Object object2) {
      return object == null ? object2 == null : object.equals(object2);
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof Entry)) {
         return false;
      }

      Entry entry = (Entry)object;
      return objectsEqual(this.getKey(), entry.getKey()) && objectsEqual(this.value, entry.getValue());
   }

   @Override
   public int hashCode() {
      return (this.key == MapShape.getNULL_KEY() ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
   }

   @Override
   public String toString() {
      return MapShape.unmaskNullStatic(this.map.shape, this.key) + "=" + this.value;
   }
}
