package com.github.andrewoma.dexx.collection;

import java.util.Iterator;

public interface Builder<E, R> {
   Builder<E, R> add(E object);

   Builder<E, R> addAll(Traversable<E> traversable);

   Builder<E, R> addAll(java.lang.Iterable<E> iterable);

   Builder<E, R> addAll(Iterator<E> iterator);

   Builder<E, R> addAll(E object, E object2, E... objects);

   R build();
}
