package com.github.andrewoma.dexx.collection.internal.adapter;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Pair;
import com.github.andrewoma.dexx.collection.internal.base.MappedIterable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class MapAdapter<K, V> extends AbstractMap<K, V> {
   private final Map<K, V> map;

   public MapAdapter(Map<K, V> var1) {
      this.map = var1;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      try {
         return this.map.containsKey((K)var1);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   @Override
   public boolean containsValue(Object var1) {
      return Adapters.contains(this.map.values(), var1);
   }

   @Override
   public V get(Object var1) {
      try {
         return this.map.get((K)var1);
      } catch (ClassCastException var3) {
         return null;
      } catch (NullPointerException var4) {
         return null;
      }
   }

   @Override
   public V put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(java.util.Map<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<K> keySet() {
      return new MapAdapter.AdaptedKeySet();
   }

   @Override
   public Collection<V> values() {
      return new AbstractCollection<V>() {
         @Override
         public int size() {
            return MapAdapter.m(MapAdapter.this).size();
         }

         @Override
         public boolean isEmpty() {
            return MapAdapter.m(MapAdapter.this).isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            return MapAdapter.this.containsValue(var1);
         }

         @Override
         public Iterator<V> iterator() {
            return MapAdapter.m(MapAdapter.this).values().iterator();
         }

         @Override
         public Object[] toArray() {
            return MapAdapter.m(MapAdapter.this).values().toArray();
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            return (T[])Adapters.toArray(MapAdapter.m(MapAdapter.this).values(), var1);
         }

         @Override
         public boolean add(V var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean containsAll(Collection<?> var1) {
            return Adapters.containsAll(MapAdapter.m(MapAdapter.this).values(), var1);
         }

         @Override
         public boolean addAll(Collection<? extends V> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new MapAdapter.AdaptedEntrySet();
   }

   public static Map m(MapAdapter var0) {
      return var0.map;
   }

   public class AdaptedEntrySet extends AbstractSet<Entry<K, V>> {
      @Override
      public int size() {
         return MapAdapter.m(MapAdapter.this).size();
      }

      @Override
      public boolean isEmpty() {
         return MapAdapter.m(MapAdapter.this).isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         try {
            Entry var2 = (Entry)var1;
            Pair var3 = new Pair<>(var2.getKey(), var2.getValue());
            return Adapters.contains(MapAdapter.m(MapAdapter.this), var3);
         } catch (ClassCastException var4) {
            return false;
         }
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return this.b().iterator();
      }

      private MappedIterable<Entry<K, V>, Pair<K, V>> b() {
         return new MappedIterable<>(MapAdapter.m(MapAdapter.this), new MapAdapter.AdaptedEntrySet.PairToEntry());
      }

      @Override
      public Object[] toArray() {
         return this.b().toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])Adapters.toArray(this.b(), var1);
      }

      public boolean add(Entry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Entry<K, V>> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      public class PairToEntry implements Function<Pair<K, V>, Entry<K, V>> {
         public Entry<K, V> invoke(Pair<K, V> var1) {
            return new MapAdapter.PairEntry(this, var1);
         }
      }
   }

   public class AdaptedKeySet extends AbstractSet<K> {
      @Override
      public int size() {
         return MapAdapter.m(MapAdapter.this).size();
      }

      @Override
      public boolean isEmpty() {
         return MapAdapter.m(MapAdapter.this).isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return MapAdapter.this.containsKey(var1);
      }

      @Override
      public Iterator<K> iterator() {
         return MapAdapter.m(MapAdapter.this).H().iterator();
      }

      @Override
      public Object[] toArray() {
         return MapAdapter.m(MapAdapter.this).H().toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])Adapters.toArray(MapAdapter.m(MapAdapter.this).H(), var1);
      }

      @Override
      public boolean add(K var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return Adapters.containsAll(MapAdapter.m(MapAdapter.this).H(), var1);
      }

      @Override
      public boolean addAll(Collection<? extends K> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }
   }

   public static class PairEntry implements Entry {
      public final Pair B;
      public final MapAdapter.AdaptedEntrySet.PairToEntry W;
      private static final long a = 96575077283798L;

      public PairEntry(MapAdapter.AdaptedEntrySet.PairToEntry var1, Pair var2) {
         this.W = var1;
         this.B = var2;
      }

      @Override
      public Object getKey() {
         return this.B.component1();
      }

      @Override
      public Object getValue() {
         return this.B.component2();
      }

      @Override
      public Object setValue(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         }

         Entry var5 = (Entry)var1;
         return (this.getKey() != null ? this.getKey().equals(var5.getKey()) : var5.getKey() == null)
            && (this.getValue() != null ? this.getValue().equals(var5.getValue()) : var5.getValue() == null);
      }

      @Override
      public int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }

      @Override
      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
