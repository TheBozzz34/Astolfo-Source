package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class ShapedMapEntrySet<K, V> extends AbstractSet<Entry<K, V>> {
   private final ShapedMap<K, V> K;
   private static final long a = 50974752418489L;

   public ShapedMapEntrySet(ShapedMap<K, V> var1) {
      this.K = var1;
   }

   @Override
   public Iterator<Entry<K, V>> iterator() {
      return new ShapedMapEntryIterator<>(this.K);
   }

   @Override
   public boolean contains(Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      }

      Entry var5 = (Entry)var1;
      Object var6 = var5.getKey();
      Object var7 = var5.getValue();
      Object var8 = this.K.get(var6);
      return var7 != null ? var7.equals(var8) : var8 == null && this.K.containsKey(var6);
   }

   @Override
   public boolean remove(Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      }

      Entry var5 = (Entry)var1;
      return this.K.remove(var5.getKey()) != null;
   }

   @Override
   public int size() {
      return this.K.size();
   }

   @Override
   public void clear() {
      this.K.clear();
   }
}
