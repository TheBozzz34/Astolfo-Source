package com.googlecode.concurrentlinkedhashmap;

public interface EntryWeigher<K, V> {
   int weightOf(K var1, V var2);
}
