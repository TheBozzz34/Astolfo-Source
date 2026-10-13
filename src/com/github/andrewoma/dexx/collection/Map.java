package com.github.andrewoma.dexx.collection;

public interface Map<K, V> extends Iterable<Pair<K, V>> {
   Map<K, V> put(K var1, V var2);

   V get(K var1);

   Map<K, V> remove(K var1);

   Iterable<K> H();

   Iterable<V> values();

   boolean containsKey(K var1);

   java.util.Map<K, V> asMap();
}
