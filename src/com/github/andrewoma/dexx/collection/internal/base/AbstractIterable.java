package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Iterable;

public abstract class AbstractIterable<E> extends AbstractTraversable<E> implements Iterable<E> {
   @Override
   public <U> void forEach(Function<E, U> f) {
      for (Object object : this) {
         f.invoke((E)(object));
      }
   }
}
