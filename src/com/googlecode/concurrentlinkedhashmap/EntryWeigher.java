package com.googlecode.concurrentlinkedhashmap;

public interface EntryWeigher<K, V> {
   int weightOf(K object, V object2);
}
