package com.github.andrewoma.dexx.collection;

import java.util.Iterator;

public interface Builder<E, R> {
   Builder<E, R> add(E var1);

   Builder<E, R> addAll(Traversable<E> var1);

   Builder<E, R> addAll(java.lang.Iterable<E> var1);

   Builder<E, R> addAll(Iterator<E> var1);

   Builder<E, R> addAll(E var1, E var2, E... var3);

   R build();
}
