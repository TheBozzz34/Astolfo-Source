package com.googlecode.concurrentlinkedhashmap;

public interface Linked<T extends Linked<T>> {
   T U();

   void setPrevious(T var1);

   T getNext();

   void setNext(T var1);
}
