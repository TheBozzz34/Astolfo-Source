package com.github.andrewoma.dexx.collection;

public interface Traversable<E> {
   <U> void forEach(Function<E, U> var1);

   int size();

   boolean isEmpty();

   String makeString(String var1);

   String makeString(String var1, String var2, String var3, int var4, String var5);

   <R extends Traversable<E>> R to(Builder<E, R> var1);

   Object[] toArray();

   E[] toArray(E[] var1);
}
