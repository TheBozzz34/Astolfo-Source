package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class ShapedMapValues<K, V> extends AbstractSet<V> {
   private final ShapedMap<K, V> map;

   public ShapedMapValues(ShapedMap<K, V> shapedMap) {
      this.map = shapedMap;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public Iterator<V> iterator() {
      return new ShapedMapValueIterator<>(this.map);
   }

   @Override
   public void clear() {
      this.map.clear();
   }
}
