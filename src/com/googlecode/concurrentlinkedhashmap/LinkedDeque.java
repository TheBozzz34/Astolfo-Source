package com.googlecode.concurrentlinkedhashmap;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LinkedDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E> {
   public E first;
   public E last;
   private static final long a = 139428197699582L;

   public void D(E var1) {
      Linked var5 = this.first;
      this.first = (E)var1;
      if (var5 == null) {
         this.last = (E)var1;
      } else {
         var5.setPrevious(var1);
         var1.setNext((E)(var5));
      }
   }

   public void B(E var1) {
      Linked var5 = this.last;
      this.last = (E)var1;
      if (var5 == null) {
         this.first = (E)var1;
      } else {
         var5.setNext(var1);
         var1.setPrevious((E)(var5));
      }
   }

   public E o() {
      Linked var4 = this.first;
      Linked var5 = var4.getNext();
      var4.setNext(null);
      this.first = (E)var5;
      if (var5 == null) {
         this.last = null;
      } else {
         var5.setPrevious(null);
      }

      return (E)var4;
   }

   public E u() {
      Linked var4 = this.last;
      Linked var5 = var4.U();
      var4.setPrevious(null);
      this.last = (E)var5;
      if (var5 == null) {
         this.first = null;
      } else {
         var5.setNext(null);
      }

      return (E)var4;
   }

   public void t(E var1) {
      Linked var5 = var1.U();
      Linked var6 = var1.getNext();
      if (var5 == null) {
         this.first = (E)var6;
      } else {
         var5.setNext(var6);
         var1.setPrevious(null);
      }

      if (var6 == null) {
         this.last = (E)var5;
      } else {
         var6.setPrevious(var5);
         var1.setNext(null);
      }
   }

   @Override
   public boolean isEmpty() {
      return this.first == null;
   }

   public void checkNotEmpty() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      }
   }

   @Override
   public int size() {
      int var4 = 0;

      for (Linked var5 = this.first; var5 != null; var5 = var5.getNext()) {
         var4++;
      }

      return var4;
   }

   @Override
   public void clear() {
      Linked var4 = this.first;

      while (var4 != null) {
         Linked var5 = var4.getNext();
         var4.setPrevious(null);
         var4.setNext(null);
         var4 = var5;
      }

      this.first = this.last = null;
   }

   @Override
   public boolean contains(Object var1) {
      return var1 instanceof Linked && this.Z((Linked<?>)var1);
   }

   public boolean Z(Linked<?> var1) {
      return var1.U() != null || var1.getNext() != null || var1 == this.first;
   }

   public void g(E var1) {
      if (var1 != this.first) {
         this.t((E)var1);
         this.D((E)var1);
      }
   }

   public void R(E var1) {
      if (var1 != this.last) {
         this.t((E)var1);
         this.B((E)var1);
      }
   }

   public E peek() {
      return this.peekFirst();
   }

   public E peekFirst() {
      return this.first;
   }

   public E peekLast() {
      return this.last;
   }

   public E getFirst() {
      this.checkNotEmpty();
      return this.peekFirst();
   }

   public E getLast() {
      this.checkNotEmpty();
      return this.peekLast();
   }

   public E element() {
      return this.getFirst();
   }

   public boolean offer(E var1) {
      return this.offerLast((E)var1);
   }

   public boolean offerFirst(E var1) {
      if (this.Z(var1)) {
         return false;
      }

      this.D((E)var1);
      return true;
   }

   public boolean offerLast(E var1) {
      if (this.Z(var1)) {
         return false;
      }

      this.B((E)var1);
      return true;
   }

   public boolean add(E var1) {
      return this.offerLast((E)var1);
   }

   public void addFirst(E var1) {
      if (!this.offerFirst((E)var1)) {
         throw new IllegalArgumentException();
      }
   }

   public void addLast(E var1) {
      if (!this.offerLast((E)var1)) {
         throw new IllegalArgumentException();
      }
   }

   public E poll() {
      return this.pollFirst();
   }

   public E pollFirst() {
      return this.isEmpty() ? null : this.o();
   }

   public E pollLast() {
      return this.isEmpty() ? null : this.u();
   }

   public E remove() {
      return this.removeFirst();
   }

   @Override
   public boolean remove(Object var1) {
      return var1 instanceof Linked && this.remove((E)var1);
   }

   public boolean remove(E var1) {
      if (this.Z(var1)) {
         this.t((E)var1);
         return true;
      } else {
         return false;
      }
   }

   public E removeFirst() {
      this.checkNotEmpty();
      return this.pollFirst();
   }

   @Override
   public boolean removeFirstOccurrence(Object var1) {
      return this.remove(var1);
   }

   public E removeLast() {
      this.checkNotEmpty();
      return this.pollLast();
   }

   @Override
   public boolean removeLastOccurrence(Object var1) {
      return this.remove(var1);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      boolean var5 = false;

      for (Object var7 : var1) {
         var5 |= this.remove(var7);
      }

      return var5;
   }

   public void push(E var1) {
      this.addFirst((E)var1);
   }

   public E pop() {
      return this.removeFirst();
   }

   @Override
   public Iterator<E> iterator() {
      return new LinkedDeque.AscendingIterator(this.first);
   }

   @Override
   public Iterator<E> descendingIterator() {
      return new LinkedDeque.DescendingIterator(this.last);
   }

   public abstract static class AbstractLinkedIterator implements Iterator<Linked> {
      public Linked N;
      public final LinkedDeque X;
      private static final long a = 21846915225262L;

      public AbstractLinkedIterator(LinkedDeque var1, Linked var2) {
         this.X = var1;
         this.N = var2;
      }

      @Override
      public boolean hasNext() {
         return this.N != null;
      }

      public Linked next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         Linked var4 = this.N;
         this.N = this.g();
         return var4;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      public abstract Linked g();
   }

   public class AscendingIterator extends LinkedDeque.AbstractLinkedIterator {
      public AscendingIterator(Linked var2) {
         super(LinkedDeque.this, var2);
      }

      @Override
      public E g() {
         return (E)this.N.getNext();
      }
   }

   public class DescendingIterator extends LinkedDeque.AbstractLinkedIterator {
      public DescendingIterator(Linked var2) {
         super(LinkedDeque.this, var2);
      }

      @Override
      public E g() {
         return (E)this.N.U();
      }
   }
}
