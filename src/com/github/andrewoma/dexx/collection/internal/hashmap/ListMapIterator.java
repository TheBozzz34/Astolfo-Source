package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Pair;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListMapIterator<K, V> implements Iterator<Pair<K, V>> {
   private ListMap<K, V> listMap;
   private static final long a = 32862615900565L;

   public ListMapIterator(ListMap<K, V> var1) {
      this.listMap = var1;
   }

   @Override
   public boolean hasNext() {
      return !this.listMap.isEmpty();
   }

   public Pair<K, V> next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException("next on empty iterator");
      }

      Pair var3 = new Pair<>(this.listMap.getKey(), this.listMap.getValue());
      this.listMap = this.listMap.tail();
      return var3;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
