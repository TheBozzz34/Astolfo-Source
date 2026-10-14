package com.github.andrewoma.dexx.collection;

public interface Traversable<E> {
   <U> void forEach(Function<E, U> function);

   int size();

   boolean isEmpty();

   String makeString(String string);

   String makeString(String string, String string2, String string3, int i, String string4);

   <R extends Traversable<E>> R to(Builder<E, R> builder);

   Object[] toArray();

   E[] toArray(E[] objects);
}
