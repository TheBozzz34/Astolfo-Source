package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Iterable;
import java.util.Iterator;

public class MappedIterable<T, F> extends AbstractIterable<T> {
   private final Function<F, T> mapFunction;
   private final Iterable<F> from;

   public MappedIterable(Iterable<F> var1, Function<F, T> var2) {
      this.mapFunction = var2;
      this.from = var1;
   }

   @Override
   public Iterator<T> iterator() {
      final Iterator var1 = this.from.iterator();
      return new Iterator<T>() {
         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         @Override
         public T next() {
            return (T)MappedIterable.i(MappedIterable.this).invoke(var1.next());
         }

         @Override
         public void remove() {
            var1.remove();
         }
      };
   }

   public static Function i(MappedIterable var0) {
      return var0.mapFunction;
   }
}
