package bypass.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class ShapedMapEntrySet<K, V> extends AbstractSet<Entry<K, V>> {
   private final ShapedMap<K, V> map;

   public ShapedMapEntrySet(ShapedMap<K, V> shapedMap) {
      this.map = shapedMap;
   }

   @Override
   public Iterator<Entry<K, V>> iterator() {
      return new ShapedMapEntryIterator<>(this.map);
   }

   @Override
   public boolean contains(Object object) {
      if (!(object instanceof Entry)) {
         return false;
      }

      Entry entry = (Entry)object;
      Object object2 = entry.getKey();
      Object object3 = entry.getValue();
      Object object4 = this.map.get(object2);
      return object3 != null ? object3.equals(object4) : object4 == null && this.map.containsKey(object2);
   }

   @Override
   public boolean remove(Object object) {
      if (!(object instanceof Entry)) {
         return false;
      }

      Entry entry = (Entry)object;
      return this.map.remove(entry.getKey()) != null;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public void clear() {
      this.map.clear();
   }
}
