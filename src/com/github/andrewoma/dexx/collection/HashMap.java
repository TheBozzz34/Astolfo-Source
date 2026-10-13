package com.github.andrewoma.dexx.collection;

import com.github.andrewoma.dexx.collection.internal.base.AbstractMap;
import com.github.andrewoma.dexx.collection.internal.builder.AbstractSelfBuilder;
import com.github.andrewoma.dexx.collection.internal.hashmap.CompactHashMap;
import java.util.Iterator;

public class HashMap<K, V> extends AbstractMap<K, V> {
   private static final HashMap EMPTY = new HashMap();
   private static final KeyFunction keyFunction = new KeyFunction<Object, Pair>() {
      public Object key(Pair var1) {
         return var1.component1();
      }
   };
   private final CompactHashMap<K, Pair<K, V>> compactHashMap;
   private static final long a = 34875112212747L;

   public static <K, V> HashMap<K, V> empty() {
      return EMPTY;
   }

   public static <K, V> BuilderFactory<Pair<K, V>, HashMap<K, V>> factory() {
      return new BuilderFactory<Pair<K, V>, HashMap<K, V>>() {
         @Override
         public Builder<Pair<K, V>, HashMap<K, V>> newBuilder() {
            return new AbstractSelfBuilder<Pair<K, V>, HashMap<K, V>>(HashMap.empty()) {
               public Builder<Pair<K, V>, HashMap<K, V>> add(Pair<K, V> var1) {
                  this.result = this.result.put((K)var1.component1(), (V)var1.component2());
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
   public boolean containsKey(K var1) {
      return this.compactHashMap.get((K)var1, this.keyFunction()) != null;
   }

   public HashMap() {
      this(CompactHashMap.empty());
   }

   private HashMap(CompactHashMap<K, Pair<K, V>> var1) {
      this.compactHashMap = var1;
   }

   public HashMap<K, V> put(K var1, V var2) {
      return new HashMap<>(this.compactHashMap.put((K)var1, new Pair<>((K)var1, (V)var2), this.keyFunction()));
   }

   @Override
   public V get(K var1) {
      Pair var5 = this.compactHashMap.get((K)var1, this.keyFunction());
      return (V)(var5 == null ? null : var5.component2());
   }

   public HashMap<K, V> remove(K var1) {
      return new HashMap<>(this.compactHashMap.remove((K)var1, this.keyFunction()));
   }

   @Override
   public int size() {
      return this.compactHashMap.size();
   }

   @Override
   public <U> void forEach(final Function<Pair<K, V>, U> var1) {
      this.compactHashMap.forEach(new Function<Pair<K, Pair<K, V>>, Object>() {
         public Object invoke(Pair<K, Pair<K, V>> var1x) {
            var1.invoke(var1x.component2());
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

      public Itr(Iterator<Pair<K, Pair<K, V>>> var1) {
         this.iterator = var1;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Pair<K, V> next() {
         Pair var1 = this.iterator.next();
         return (Pair<K, V>)var1.component2();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
