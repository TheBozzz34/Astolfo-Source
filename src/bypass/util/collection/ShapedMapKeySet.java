package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class ShapedMapKeySet<K, V> extends AbstractSet<K> {
   private final ShapedMap<K, V> r;
   private static final long a = 28695063322883L;

   public ShapedMapKeySet(ShapedMap<K, V> shapedMap) {
      this.r = shapedMap;
   }

   @Override
   public int size() {
      return this.r.size();
   }

   @Override
   public boolean contains(Object object) {
      return this.r.containsKey(object);
   }

   @Override
   public boolean remove(Object object) {
      return this.r.remove(object) != null;
   }

   @Override
   public Iterator<K> iterator() {
      return new ShapedMapKeyIterator<>(this.r);
   }

   @Override
   public void clear() {
      this.r.clear();
   }
}
