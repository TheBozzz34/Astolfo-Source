package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class ShapedMapValues<K, V> extends AbstractSet<V> {
   private final ShapedMap<K, V> e;

   public ShapedMapValues(ShapedMap<K, V> var1) {
      this.e = var1;
   }

   @Override
   public int size() {
      return this.e.size();
   }

   @Override
   public Iterator<V> iterator() {
      return new ShapedMapValueIterator<>(this.e);
   }

   @Override
   public void clear() {
      this.e.clear();
   }
}
