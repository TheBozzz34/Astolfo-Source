package bypass.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ShapedMap<K, V> implements Map<K, V>, Serializable {
   public MapShape<K, V> shape = MapShape.EMPTY;
   public Object w;
   public Object D;
   public Object M;

   @Override
   public int size() {
      return this.shape.P(this);
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean containsKey(Object object) {
      return this.shape.O(this, object);
   }

   @Override
   public boolean containsValue(Object object) {
      return this.values().contains(object);
   }

   @Override
   public V get(Object object) {
      return this.shape.k(this, (K)object);
   }

   @Override
   public V put(K object, V object2) {
      return this.shape.n(this, (K)object, object2);
   }

   public V F(K object, Object object2) {
      return this.shape.n(this, (K)object, object2);
   }

   @Override
   public V remove(Object object) {
      return this.shape.n(this, (K)object, MapShape.REMOVED);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      for (Entry entry : map.entrySet()) {
         this.put((K)entry.getKey(), (V)entry.getValue());
      }
   }

   @Override
   public void clear() {
      this.shape = MapShape.EMPTY;
      this.w = this.D = this.M = null;
   }

   @Override
   public Set<K> keySet() {
      return this.shape.J(this);
   }

   @Override
   public Collection<V> values() {
      return this.shape.i(this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.shape.f(this);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof Map)) {
         return false;
      }

      Map map = (Map)object;
      if (map.size() != this.size()) {
         return false;
      }

      for (Entry entry : this.entrySet()) {
         Object object2 = entry.getKey();
         Object object3 = entry.getValue();
         if (object3 == null) {
            if (map.get(object2) != null || !map.containsKey(object2)) {
               return false;
            }
         } else if (!object3.equals(map.get(object2))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String toString() {
      Iterator iterator2 = this.entrySet().iterator();
      if (!iterator2.hasNext()) {
         return "{}";
      }

      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('{');

      while (iterator2.hasNext()) {
         Entry entry = (Entry)iterator2.next();
         Object object = entry.getKey();
         Object object2 = entry.getValue();
         stringBuilder.append(object).append('=').append(object2);
         stringBuilder.append(',').append(' ');
      }

      stringBuilder.setLength(stringBuilder.length() - 2);
      return stringBuilder.append('}').toString();
   }

   @Override
   public int hashCode() {
      int i = 0;

      for (Entry entry : this.entrySet()) {
         i += entry.hashCode();
      }

      return i;
   }
}
