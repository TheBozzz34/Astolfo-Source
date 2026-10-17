package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Iterable;
import java.util.Iterator;

public class MappedIterable<T, F> extends AbstractIterable<T> {
   private final Function<F, T> mapFunction;
   private final Iterable<F> from;

   public MappedIterable(Iterable<F> from, Function<F, T> mapFunction) {
      this.mapFunction = mapFunction;
      this.from = from;
   }

   @Override
   public Iterator<T> iterator() {
      final Iterator<F> iterator = this.from.iterator();
      return new Iterator<T>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public T next() {
            return (T)MappedIterable.accessMapFunction(MappedIterable.this).invoke(iterator.next());
         }

         @Override
         public void remove() {
            iterator.remove();
         }
      };
   }

   public static Function accessMapFunction(MappedIterable mappedIterable) {
      return mappedIterable.mapFunction;
   }
}
