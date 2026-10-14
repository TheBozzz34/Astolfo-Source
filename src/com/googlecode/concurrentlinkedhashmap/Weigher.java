package com.googlecode.concurrentlinkedhashmap;

public interface Weigher<V> {
   int weightOf(V object);
}
