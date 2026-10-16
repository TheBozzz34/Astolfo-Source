package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class ShapedMapKeySet<K, V> extends AbstractSet<K> {
   private final ShapedMap<K, V> map;

   public ShapedMapKeySet(ShapedMap<K, V> shapedMap) {
      this.map = shapedMap;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public boolean contains(Object object) {
      return this.map.containsKey(object);
   }

   @Override
   public boolean remove(Object object) {
      return this.map.remove(object) != null;
   }

   @Override
   public Iterator<K> iterator() {
      return new ShapedMapKeyIterator<>(this.map);
   }

   @Override
   public void clear() {
      this.map.clear();
   }
}
