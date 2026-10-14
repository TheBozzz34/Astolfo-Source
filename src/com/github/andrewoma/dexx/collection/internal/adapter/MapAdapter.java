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

   public MapAdapter(Map<K, V> map) {
      this.map = map;
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
   public boolean containsKey(Object key) {
      try {
         return this.map.containsKey((K)key);
      } catch (ClassCastException eIn) {
         return false;
      } catch (NullPointerException eIn) {
         return false;
      }
   }

   @Override
   public boolean containsValue(Object value) {
      return Adapters.contains(this.map.values(), value);
   }

   @Override
   public V get(Object key) {
      try {
         return this.map.get((K)key);
      } catch (ClassCastException eIn) {
         return null;
      } catch (NullPointerException eIn) {
         return null;
      }
   }

   @Override
   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(java.util.Map<? extends K, ? extends V> m) {
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
         public boolean contains(Object o) {
            return MapAdapter.this.containsValue(o);
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
         public <T> T[] toArray(T[] a) {
            return (T[])Adapters.toArray(MapAdapter.m(MapAdapter.this).values(), a);
         }

         @Override
         public boolean add(V v) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object o) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean containsAll(Collection<?> c) {
            return Adapters.containsAll(MapAdapter.m(MapAdapter.this).values(), c);
         }

         @Override
         public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean retainAll(Collection<?> c) {
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

   public static Map m(MapAdapter mapAdapter) {
      return mapAdapter.map;
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
      public boolean contains(Object object) {
         try {
            Entry entry = (Entry)object;
            Pair pair = new Pair<>(entry.getKey(), entry.getValue());
            return Adapters.contains(MapAdapter.m(MapAdapter.this), pair);
         } catch (ClassCastException classCastException) {
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
      public <T> T[] toArray(T[] objects) {
         return (T[])Adapters.toArray(this.b(), objects);
      }

      public boolean add(Entry<K, V> entry) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object object) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Entry<K, V>> collection) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> collection) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> collection) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      public class PairToEntry implements Function<Pair<K, V>, Entry<K, V>> {
         public Entry<K, V> invoke(Pair<K, V> pair) {
            return new MapAdapter.PairEntry(this, pair);
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
      public boolean contains(Object object) {
         return MapAdapter.this.containsKey(object);
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
      public <T> T[] toArray(T[] objects) {
         return (T[])Adapters.toArray(MapAdapter.m(MapAdapter.this).H(), objects);
      }

      @Override
      public boolean add(K object) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object object) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsAll(Collection<?> collection) {
         return Adapters.containsAll(MapAdapter.m(MapAdapter.this).H(), collection);
      }

      @Override
      public boolean addAll(Collection<? extends K> collection) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> collection) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> collection) {
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

      public PairEntry(MapAdapter.AdaptedEntrySet.PairToEntry pairToEntry, Pair pair) {
         this.W = pairToEntry;
         this.B = pair;
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
      public Object setValue(Object object) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object object) {
         if (!(object instanceof Entry)) {
            return false;
         }

         Entry entry = (Entry)object;
         return (this.getKey() != null ? this.getKey().equals(entry.getKey()) : entry.getKey() == null)
            && (this.getValue() != null ? this.getValue().equals(entry.getValue()) : entry.getValue() == null);
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
