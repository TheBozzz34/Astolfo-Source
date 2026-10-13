package com.github.andrewoma.dexx.collection;

import java.util.Iterator;

public interface Iterable<E> extends Traversable<E>, java.lang.Iterable<E> {
   @Override
   Iterator<E> iterator();
}
