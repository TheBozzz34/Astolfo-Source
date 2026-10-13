package bypass.util.collection;

import java.util.Map.Entry;

public class ShapedMapEntryIterator<K, V> extends ShapedMapIterator<K, V, Entry<K, V>> {
   public ShapedMapEntryIterator(ShapedMap<K, V> var1) {
      super(var1);
   }

   public Entry<K, V> next() {
      return this.D();
   }
}
