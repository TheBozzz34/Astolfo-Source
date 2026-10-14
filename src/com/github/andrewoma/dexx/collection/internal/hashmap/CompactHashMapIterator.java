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

   public CompactHashMapIterator(Object[] elems, KeyFunction<K, V> keyFunction) {
      this.current = new CompactHashMapIterator.Pointer(elems, 0);
      this.keyFunction = keyFunction;
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
            Object object = this.current.objects[this.current.pos++];
            if (object instanceof HashTrieMap) {
               this.stack.push(this.current);
               this.current = new CompactHashMapIterator.Pointer(((HashTrieMap)object).getElems(), 0);
            } else if (object instanceof HashMapCollision1) {
               this.subIterator = ((HashMapCollision1)object).iterator(this.keyFunction);
               this.next = this.subIterator.next();
            } else {
               Object object2 = object;
               this.next = new Pair<>(this.keyFunction.key((V)object2), (V)object2);
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

      Pair pair = this.next;
      this.computeNext();
      return pair;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   public static class Pointer {
      public final Object[] objects;
      public int pos;

      public Pointer(Object[] objects, int pos) {
         this.objects = objects;
         this.pos = pos;
      }
   }
}
