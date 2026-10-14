package com.github.andrewoma.dexx.collection;

import com.github.andrewoma.dexx.collection.internal.base.AbstractMap;
import com.github.andrewoma.dexx.collection.internal.builder.AbstractSelfBuilder;
import com.github.andrewoma.dexx.collection.internal.hashmap.CompactHashMap;
import java.util.Iterator;

public class DerivedKeyHashMap<K, V> extends AbstractMap<K, V> {
   private final KeyFunction<K, V> keyFunction;
   private final CompactHashMap<K, V> compactHashMap;

   public static <K, V> BuilderFactory<Pair<K, V>, DerivedKeyHashMap<K, V>> factory(final KeyFunction<K, V> keyFunction) {
      return new BuilderFactory<Pair<K, V>, DerivedKeyHashMap<K, V>>() {
         @Override
         public Builder<Pair<K, V>, DerivedKeyHashMap<K, V>> newBuilder() {
            return new AbstractSelfBuilder<Pair<K, V>, DerivedKeyHashMap<K, V>>(new DerivedKeyHashMap<>(keyFunction)) {
               public Builder<Pair<K, V>, DerivedKeyHashMap<K, V>> add(Pair<K, V> element) {
                  this.result = this.result.put(element.component1(), element.component2());
                  return this;
               }
            };
         }
      };
   }

   public DerivedKeyHashMap(KeyFunction<K, V> keyFunction) {
      this.keyFunction = keyFunction;
      this.compactHashMap = CompactHashMap.empty();
   }

   private DerivedKeyHashMap(KeyFunction<K, V> keyFunction, CompactHashMap<K, V> compactHashMap) {
      this.keyFunction = keyFunction;
      this.compactHashMap = compactHashMap;
   }

   @Override
   public boolean containsKey(K key) {
      return this.get(key) != null;
   }

   public DerivedKeyHashMap<K, V> put(K key, V value) {
      return new DerivedKeyHashMap<>(this.keyFunction, this.compactHashMap.put(key, value, this.keyFunction));
   }

   @Override
   public V get(K key) {
      return this.compactHashMap.get(key, this.keyFunction);
   }

   public DerivedKeyHashMap<K, V> remove(K key) {
      return new DerivedKeyHashMap<>(this.keyFunction, this.compactHashMap.remove(key, this.keyFunction));
   }

   @Override
   public int size() {
      return this.compactHashMap.size();
   }

   @Override
   public <U> void forEach(final Function<Pair<K, V>, U> f) {
      this.compactHashMap.forEach(new Function<Pair<K, V>, Object>() {
         public Object invoke(Pair<K, V> parameter) {
            f.invoke(parameter);
            return null;
         }
      }, this.keyFunction);
   }

   @Override
   public Iterator<Pair<K, V>> iterator() {
      return this.compactHashMap.iterator(this.keyFunction);
   }
}
