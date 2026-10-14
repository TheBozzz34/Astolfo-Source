package com.github.andrewoma.dexx.collection;

public interface KeyFunction<K, V> {
   K key(V object);
}
