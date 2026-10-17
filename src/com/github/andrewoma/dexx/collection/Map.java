package com.github.andrewoma.dexx.collection;

public interface Map<K, V> extends Iterable<Pair<K, V>> {
   Map<K, V> put(K object, V object2);

   V get(K object);

   Map<K, V> remove(K object);

   Iterable<K> keys();

   Iterable<V> values();

   boolean containsKey(K object);

   java.util.Map<K, V> asMap();
}
