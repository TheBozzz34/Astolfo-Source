package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Iterable;
import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Pair;
import com.github.andrewoma.dexx.collection.internal.adapter.MapAdapter;

public abstract class AbstractMap<K, V> extends AbstractIterable<Pair<K, V>> implements Map<K, V> {
   private static final long c = 36517016750624L;
   private static final long m = 3378153866074783775L;

   @Override
   public Iterable<K> H() {
      return new MappedIterable<>(this, new AbstractMap.rts());
   }

   @Override
   public Iterable<V> values() {
      return new MappedIterable<>(this, new AbstractMap.u2());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Map)) {
         return false;
      }

      Map var5 = (Map)var1;
      if (var5.size() != this.size()) {
         return false;
      }

      try {
         for (Pair var7 : this) {
            Object var8 = var7.component1();
            Object var9 = var7.component2();
            if (var9 == null) {
               if (var5.get(var8) != null || !var5.containsKey(var8)) {
                  return false;
               }
            } else if (!var9.equals(var5.get(var8))) {
               return false;
            }
         }

         return true;
      } catch (ClassCastException var10) {
         return false;
      } catch (NullPointerException var11) {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var4 = 0;

      for (Pair var6 : this) {
         var4 += (int)m * var6.hashCode();
      }

      return var4;
   }

   @Override
   public java.util.Map<K, V> asMap() {
      return new MapAdapter<>(this);
   }

   public class rts implements Function<Pair<K, V>, K> {
      public K invoke(Pair<K, V> var1) {
         return (K)var1.component1();
      }
   }

   public class u2 implements Function<Pair<K, V>, V> {
      public V invoke(Pair<K, V> var1) {
         return (V)var1.component2();
      }
   }
}
