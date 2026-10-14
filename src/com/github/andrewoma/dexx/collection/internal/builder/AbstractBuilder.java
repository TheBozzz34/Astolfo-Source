package com.github.andrewoma.dexx.collection.internal.builder;

import com.github.andrewoma.dexx.collection.Builder;
import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.util.Iterator;

public abstract class AbstractBuilder<E, R> implements Builder<E, R> {
   private boolean built = false;

   @Override
   public Builder<E, R> addAll(Traversable<E> elements) {
      elements.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E element) {
            AbstractBuilder.this.add(element);
            return null;
         }
      });
      return this;
   }

   @Override
   public Builder<E, R> addAll(Iterable<E> elements) {
      for (Object object : elements) {
         this.add((E)object);
      }

      return this;
   }

   @Override
   public Builder<E, R> addAll(Iterator<E> iterator) {
      while (iterator.hasNext()) {
         this.add(iterator.next());
      }

      return this;
   }

   @Override
   public Builder<E, R> addAll(E e1, E e2, E... es) {
      this.add((E)e1);
      this.add((E)e2);

      for (Object object : es) {
         this.add((E)object);
      }

      return this;
   }

   @Override
   public final R build() {
      if (this.built) {
         throw new IllegalStateException("Builders do not support multiple calls to build()");
      }

      this.built = true;
      return this.doBuild();
   }

   public abstract R doBuild();
}
