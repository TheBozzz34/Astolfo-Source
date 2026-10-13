package com.github.andrewoma.dexx.collection;

import com.github.andrewoma.dexx.collection.internal.base.AbstractMap;
import com.github.andrewoma.dexx.collection.internal.builder.AbstractSelfBuilder;
import com.github.andrewoma.dexx.collection.internal.hashmap.CompactHashMap;
import java.util.Iterator;

public class DerivedKeyHashMap<K, V> extends AbstractMap<K, V> {
   private final KeyFunction<K, V> keyFunction;
   private final CompactHashMap<K, V> compactHashMap;
   private static final long a = 66016478668345L;

   public static <K, V> BuilderFactory<Pair<K, V>, DerivedKeyHashMap<K, V>> factory(final KeyFunction<K, V> var0) {
      return new BuilderFactory<Pair<K, V>, DerivedKeyHashMap<K, V>>() {
         @Override
         public Builder<Pair<K, V>, DerivedKeyHashMap<K, V>> newBuilder() {
            return new AbstractSelfBuilder<Pair<K, V>, DerivedKeyHashMap<K, V>>(new DerivedKeyHashMap(var0)) {
               public Builder<Pair<K, V>, DerivedKeyHashMap<K, V>> add(Pair<K, V> var1) {
                  this.result = this.result.put((K)var1.component1(), (V)var1.component2());
                  return this;
               }
            };
         }
      };
   }

   public DerivedKeyHashMap(KeyFunction<K, V> var1) {
      this.keyFunction = var1;
      this.compactHashMap = CompactHashMap.empty();
   }

   private DerivedKeyHashMap(KeyFunction<K, V> var1, CompactHashMap<K, V> var2) {
      this.keyFunction = var1;
      this.compactHashMap = var2;
   }

   @Override
   public boolean containsKey(K var1) {
      return this.get((K)var1) != null;
   }

   public DerivedKeyHashMap<K, V> put(K var1, V var2) {
      return new DerivedKeyHashMap<>(this.keyFunction, this.compactHashMap.put((K)var1, (V)var2, this.keyFunction));
   }

   @Override
   public V get(K var1) {
      return this.compactHashMap.get((K)var1, this.keyFunction);
   }

   public DerivedKeyHashMap<K, V> remove(K var1) {
      return new DerivedKeyHashMap<>(this.keyFunction, this.compactHashMap.remove((K)var1, this.keyFunction));
   }

   @Override
   public int size() {
      return this.compactHashMap.size();
   }

   @Override
   public <U> void forEach(final Function<Pair<K, V>, U> var1) {
      this.compactHashMap.forEach(new Function<Pair<K, V>, Object>() {
         public Object invoke(Pair<K, V> var1x) {
            var1.invoke(var1x);
            return null;
         }
      }, this.keyFunction);
   }

   @Override
   public Iterator<Pair<K, V>> iterator() {
      return this.compactHashMap.iterator(this.keyFunction);
   }
}
