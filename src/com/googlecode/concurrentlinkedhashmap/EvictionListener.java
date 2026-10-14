package com.googlecode.concurrentlinkedhashmap;

public interface EvictionListener<K, V> {
   void onEviction(K object, V object2);
}
