package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class CompactHashMapIterator<K, V> implements Iterator<Pair<K, V>> {
   private final KeyFunction<K, V> keyFunction;
   private final Stack<CompactHashMapIterator.Pointer> stack = new Stack<>();
   private Iterator<Pair<K, V>> subIterator;
   private CompactHashMapIterator.Pointer current;
   private Pair<K, V> next;
   private static final long a = 105276426639923L;

   public CompactHashMapIterator(Object[] var1, KeyFunction<K, V> var2) {
      this.current = new CompactHashMapIterator.Pointer(var1, 0);
      this.keyFunction = var2;
      this.computeNext();
   }

   private void computeNext() {
      this.next = null;
      if (this.subIterator != null) {
         if (this.subIterator.hasNext()) {
            this.next = this.subIterator.next();
            return;
         }

         this.subIterator = null;
      }

      while (this.next == null) {
         if (this.current.pos == this.current.objects.length) {
            if (this.stack.isEmpty()) {
               return;
            }

            this.current = this.stack.pop();
         } else {
            Object var4 = this.current.objects[this.current.pos++];
            if (var4 instanceof HashTrieMap) {
               this.stack.push(this.current);
               this.current = new CompactHashMapIterator.Pointer(((HashTrieMap)var4).getElems(), 0);
            } else if (var4 instanceof HashMapCollision1) {
               this.subIterator = ((HashMapCollision1)var4).iterator(this.keyFunction);
               this.next = this.subIterator.next();
            } else {
               Object var5 = var4;
               this.next = new Pair<>(this.keyFunction.key((V)var5), (V)var5);
            }
         }
      }
   }

   @Override
   public boolean hasNext() {
      return this.next != null;
   }

   public Pair<K, V> next() {
      if (this.next == null) {
         throw new NoSuchElementException();
      }

      Pair var4 = this.next;
      this.computeNext();
      return var4;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   public static class Pointer {
      public final Object[] objects;
      public int pos;

      public Pointer(Object[] var1, int var2) {
         this.objects = var1;
         this.pos = var2;
      }
   }
}
