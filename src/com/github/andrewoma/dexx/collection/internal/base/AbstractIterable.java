package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Iterable;

public abstract class AbstractIterable<E> extends AbstractTraversable<E> implements Iterable<E> {
   private static final long b = 139221054812707L;

   @Override
   public <U> void forEach(Function<E, U> var1) {
      for (Object var6 : this) {
         var1.invoke((E)(var6));
      }
   }
}
