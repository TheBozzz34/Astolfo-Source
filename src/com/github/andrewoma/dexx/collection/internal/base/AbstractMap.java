package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Iterable;
import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Pair;
import com.github.andrewoma.dexx.collection.internal.adapter.MapAdapter;

public abstract class AbstractMap<K, V> extends AbstractIterable<Pair<K, V>> implements Map<K, V> {
   @Override
   public Iterable<K> keys() {
      return new MappedIterable<>(this, new AbstractMap.rts());
   }

   @Override
   public Iterable<V> values() {
      return new MappedIterable<>(this, new AbstractMap.u2());
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof Map)) {
         return false;
      }

      Map map = (Map)o;
      if (map.size() != this.size()) {
         return false;
      }

      try {
         for (Pair pair : this) {
            Object object = pair.component1();
            Object object2 = pair.component2();
            if (object2 == null) {
               if (map.get(object) != null || !map.containsKey(object)) {
                  return false;
               }
            } else if (!object2.equals(map.get(object))) {
               return false;
            }
         }

         return true;
      } catch (ClassCastException classCastException) {
         return false;
      } catch (NullPointerException nullPointerException) {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int i = 0;

      for (Pair pair : this) {
         i += 31 * pair.hashCode();
      }

      return i;
   }

   @Override
   public java.util.Map<K, V> asMap() {
      return new MapAdapter<>(this);
   }

   public class rts implements Function<Pair<K, V>, K> {
      public K invoke(Pair<K, V> pair) {
         return (K)pair.component1();
      }
   }

   public class u2 implements Function<Pair<K, V>, V> {
      public V invoke(Pair<K, V> pair) {
         return (V)pair.component2();
      }
   }
}
