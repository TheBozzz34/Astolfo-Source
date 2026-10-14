package com.github.andrewoma.dexx.collection;

import com.github.andrewoma.dexx.collection.internal.base.AbstractMap;
import com.github.andrewoma.dexx.collection.internal.builder.AbstractSelfBuilder;
import com.github.andrewoma.dexx.collection.internal.hashmap.CompactHashMap;
import java.util.Iterator;

public class HashMap<K, V> extends AbstractMap<K, V> {
   private static final HashMap EMPTY = new HashMap();
   private static final KeyFunction keyFunction = new KeyFunction<Object, Pair>() {
      public Object key(Pair value) {
         return value.component1();
      }
   };
   private final CompactHashMap<K, Pair<K, V>> compactHashMap;

   public static <K, V> HashMap<K, V> empty() {
      return EMPTY;
   }

   public static <K, V> BuilderFactory<Pair<K, V>, HashMap<K, V>> factory() {
      return new BuilderFactory<Pair<K, V>, HashMap<K, V>>() {
         @Override
         public Builder<Pair<K, V>, HashMap<K, V>> newBuilder() {
            return new AbstractSelfBuilder<Pair<K, V>, HashMap<K, V>>(HashMap.empty()) {
               public Builder<Pair<K, V>, HashMap<K, V>> add(Pair<K, V> element) {
                  this.result = this.result.put(element.component1(), element.component2());
                  return this;
               }
            };
         }
      };
   }

   private KeyFunction<K, Pair<K, V>> keyFunction() {
      return keyFunction;
   }

   @Override
   public boolean containsKey(K key) {
      return this.compactHashMap.get(key, this.keyFunction()) != null;
   }

   public HashMap() {
      this(CompactHashMap.empty());
   }

   private HashMap(CompactHashMap<K, Pair<K, V>> compactHashMap) {
      this.compactHashMap = compactHashMap;
   }

   public HashMap<K, V> put(K key, V value) {
      return new HashMap<>(this.compactHashMap.put(key, new Pair<>(key, value), this.keyFunction()));
   }

   @Override
   public V get(K key) {
      Pair pair = this.compactHashMap.get((K)key, this.keyFunction());
      return (V)(pair == null ? null : pair.component2());
   }

   public HashMap<K, V> remove(K key) {
      return new HashMap<>(this.compactHashMap.remove(key, this.keyFunction()));
   }

   @Override
   public int size() {
      return this.compactHashMap.size();
   }

   @Override
   public <U> void forEach(final Function<Pair<K, V>, U> f) {
      this.compactHashMap.forEach(new Function<Pair<K, Pair<K, V>>, Object>() {
         public Object invoke(Pair<K, Pair<K, V>> pair) {
            f.invoke(pair.component2());
            return null;
         }
      }, this.keyFunction());
   }

   @Override
   public Iterator<Pair<K, V>> iterator() {
      return new HashMap.Itr<>(this.compactHashMap.iterator(this.keyFunction()));
   }

   public static class Itr<K, V> implements Iterator<Pair<K, V>> {
      public final Iterator<Pair<K, Pair<K, V>>> iterator;

      public Itr(Iterator<Pair<K, Pair<K, V>>> iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Pair<K, V> next() {
         Pair<K, Pair<K, V>> nextIn = this.iterator.next();
         return nextIn.component2();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
