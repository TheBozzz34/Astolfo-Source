package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class ShapedMapKeySet<K, V> extends AbstractSet<K> {
   private final ShapedMap<K, V> r;
   private static final long a = 28695063322883L;

   public ShapedMapKeySet(ShapedMap<K, V> var1) {
      this.r = var1;
   }

   @Override
   public int size() {
      return this.r.size();
   }

   @Override
   public boolean contains(Object var1) {
      return this.r.containsKey(var1);
   }

   @Override
   public boolean remove(Object var1) {
      return this.r.remove(var1) != null;
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
