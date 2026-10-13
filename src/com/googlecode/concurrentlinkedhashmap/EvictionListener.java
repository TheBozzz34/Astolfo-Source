package com.googlecode.concurrentlinkedhashmap;

public interface EvictionListener<K, V> {
   void onEviction(K var1, V var2);
}
