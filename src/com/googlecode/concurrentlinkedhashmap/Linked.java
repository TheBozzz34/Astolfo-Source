package com.googlecode.concurrentlinkedhashmap;

public interface Linked<T extends Linked<T>> {
   T getPrevious();

   void setPrevious(T linked);

   T getNext();

   void setNext(T linked);
}
