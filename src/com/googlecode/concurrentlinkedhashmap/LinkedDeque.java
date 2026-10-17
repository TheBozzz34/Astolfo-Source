package com.googlecode.concurrentlinkedhashmap;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LinkedDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E> {
   public E first;
   public E last;

   public void linkFirst(E e) {
      Linked linked = this.first;
      this.first = (E)e;
      if (linked == null) {
         this.last = (E)e;
      } else {
         linked.setPrevious(e);
         e.setNext((E)(linked));
      }
   }

   public void linkLast(E e) {
      Linked linked = this.last;
      this.last = (E)e;
      if (linked == null) {
         this.first = (E)e;
      } else {
         linked.setNext(e);
         e.setPrevious((E)(linked));
      }
   }

   public E unlinkFirst() {
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

   public E unlinkLast() {
      Linked linked = this.last;
      Linked linked2 = linked.getPrevious();
      linked.setPrevious(null);
      this.last = (E)linked2;
      if (linked2 == null) {
         this.first = null;
      } else {
         linked2.setNext(null);
      }

      return (E)linked;
   }

   public void unlink(E e) {
      Linked linked = e.getPrevious();
      Linked linked2 = e.getNext();
      if (linked == null) {
         this.first = (E)linked2;
      } else {
         linked.setNext(linked2);
         e.setPrevious(null);
      }

      if (linked2 == null) {
         this.last = (E)linked;
      } else {
         linked2.setPrevious(linked);
         e.setNext(null);
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
      return o instanceof Linked && this.contains((Linked<?>)o);
   }

   public boolean contains(Linked<?> e) {
      return e.getPrevious() != null || e.getNext() != null || e == this.first;
   }

   public void moveToFront(E e) {
      if (e != this.first) {
         this.unlink(e);
         this.linkFirst(e);
      }
   }

   public void moveToBack(E e) {
      if (e != this.last) {
         this.unlink(e);
         this.linkLast(e);
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

   public boolean offer(E e) {
      return this.offerLast(e);
   }

   public boolean offerFirst(E e) {
      if (this.contains(e)) {
         return false;
      }

      this.linkFirst(e);
      return true;
   }

   public boolean offerLast(E e) {
      if (this.contains(e)) {
         return false;
      }

      this.linkLast(e);
      return true;
   }

   public boolean add(E e) {
      return this.offerLast(e);
   }

   public void addFirst(E e) {
      if (!this.offerFirst(e)) {
         throw new IllegalArgumentException();
      }
   }

   public void addLast(E e) {
      if (!this.offerLast(e)) {
         throw new IllegalArgumentException();
      }
   }

   public E poll() {
      return this.pollFirst();
   }

   public E pollFirst() {
      return this.isEmpty() ? null : this.unlinkFirst();
   }

   public E pollLast() {
      return this.isEmpty() ? null : this.unlinkLast();
   }

   public E remove() {
      return this.removeFirst();
   }

   @Override
   public boolean remove(Object o) {
      return o instanceof Linked && this.remove((E)o);
   }

   public boolean remove(E e) {
      if (this.contains(e)) {
         this.unlink(e);
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

   public void push(E e) {
      this.addFirst(e);
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
      public Linked cursor;
      public final LinkedDeque deque;

      public AbstractLinkedIterator(LinkedDeque linkedDeque, Linked startIn) {
         this.deque = linkedDeque;
         this.cursor = startIn;
      }

      @Override
      public boolean hasNext() {
         return this.cursor != null;
      }

      public Linked next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         Linked linked = this.cursor;
         this.cursor = this.computeNext();
         return linked;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      public abstract Linked computeNext();
   }

   public class AscendingIterator extends LinkedDeque.AbstractLinkedIterator {
      public AscendingIterator(Linked linked) {
         super(LinkedDeque.this, linked);
      }

      @Override
      public E computeNext() {
         return (E)this.cursor.getNext();
      }
   }

   public class DescendingIterator extends LinkedDeque.AbstractLinkedIterator {
      public DescendingIterator(Linked linked) {
         super(LinkedDeque.this, linked);
      }

      @Override
      public E computeNext() {
         return (E)this.cursor.getPrevious();
      }
   }
}
