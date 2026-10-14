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

   public void D(E linked) {
      Linked linked2 = this.first;
      this.first = (E)linked;
      if (linked2 == null) {
         this.last = (E)linked;
      } else {
         linked2.setPrevious(linked);
         linked.setNext((E)(linked2));
      }
   }

   public void B(E linked) {
      Linked linked2 = this.last;
      this.last = (E)linked;
      if (linked2 == null) {
         this.first = (E)linked;
      } else {
         linked2.setNext(linked);
         linked.setPrevious((E)(linked2));
      }
   }

   public E o() {
      Linked linked = this.first;
      Linked linked2 = linked.getNext();
      linked.setNext(null);
      this.first = (E)linked2;
      if (linked2 == null) {
         this.last = null;
      } else {
         linked2.setPrevious(null);
      }

      return (E)linked;
   }

   public E u() {
      Linked linked = this.last;
      Linked linked2 = linked.U();
      linked.setPrevious(null);
      this.last = (E)linked2;
      if (linked2 == null) {
         this.first = null;
      } else {
         linked2.setNext(null);
      }

      return (E)linked;
   }

   public void t(E linked) {
      Linked linked2 = linked.U();
      Linked linked3 = linked.getNext();
      if (linked2 == null) {
         this.first = (E)linked3;
      } else {
         linked2.setNext(linked3);
         linked.setPrevious(null);
      }

      if (linked3 == null) {
         this.last = (E)linked2;
      } else {
         linked3.setPrevious(linked2);
         linked.setNext(null);
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
      int i = 0;

      for (Linked linked = this.first; linked != null; linked = linked.getNext()) {
         i++;
      }

      return i;
   }

   @Override
   public void clear() {
      Linked linked = this.first;

      while (linked != null) {
         Linked linked2 = linked.getNext();
         linked.setPrevious(null);
         linked.setNext(null);
         linked = linked2;
      }

      this.first = this.last = null;
   }

   @Override
   public boolean contains(Object o) {
      return o instanceof Linked && this.Z((Linked<?>)o);
   }

   public boolean Z(Linked<?> linked) {
      return linked.U() != null || linked.getNext() != null || linked == this.first;
   }

   public void g(E linked) {
      if (linked != this.first) {
         this.t((E)linked);
         this.D((E)linked);
      }
   }

   public void R(E linked) {
      if (linked != this.last) {
         this.t((E)linked);
         this.B((E)linked);
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

   public boolean offer(E eIn) {
      return this.offerLast(eIn);
   }

   public boolean offerFirst(E eIn) {
      if (this.Z(eIn)) {
         return false;
      }

      this.D(eIn);
      return true;
   }

   public boolean offerLast(E eIn) {
      if (this.Z(eIn)) {
         return false;
      }

      this.B(eIn);
      return true;
   }

   public boolean add(E eIn) {
      return this.offerLast(eIn);
   }

   public void addFirst(E eIn) {
      if (!this.offerFirst(eIn)) {
         throw new IllegalArgumentException();
      }
   }

   public void addLast(E eIn) {
      if (!this.offerLast(eIn)) {
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
   public boolean remove(Object o) {
      return o instanceof Linked && this.remove((E)o);
   }

   public boolean remove(E eIn) {
      if (this.Z(eIn)) {
         this.t(eIn);
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
   public boolean removeFirstOccurrence(Object o) {
      return this.remove(o);
   }

   public E removeLast() {
      this.checkNotEmpty();
      return this.pollLast();
   }

   @Override
   public boolean removeLastOccurrence(Object o) {
      return this.remove(o);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean bl = false;

      for (Object object : c) {
         bl |= this.remove(object);
      }

      return bl;
   }

   public void push(E eIn) {
      this.addFirst(eIn);
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

      public AbstractLinkedIterator(LinkedDeque linkedDeque, Linked startIn) {
         this.X = linkedDeque;
         this.N = startIn;
      }

      @Override
      public boolean hasNext() {
         return this.N != null;
      }

      public Linked next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         Linked linked = this.N;
         this.N = this.g();
         return linked;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      public abstract Linked g();
   }

   public class AscendingIterator extends LinkedDeque.AbstractLinkedIterator {
      public AscendingIterator(Linked linked) {
         super(LinkedDeque.this, linked);
      }

      @Override
      public E g() {
         return (E)this.N.getNext();
      }
   }

   public class DescendingIterator extends LinkedDeque.AbstractLinkedIterator {
      public DescendingIterator(Linked linked) {
         super(LinkedDeque.this, linked);
      }

      @Override
      public E g() {
         return (E)this.N.U();
      }
   }
}
