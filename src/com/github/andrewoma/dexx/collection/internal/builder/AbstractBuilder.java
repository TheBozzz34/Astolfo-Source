package com.github.andrewoma.dexx.collection.internal.builder;

import com.github.andrewoma.dexx.collection.Builder;
import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.util.Iterator;

public abstract class AbstractBuilder<E, R> implements Builder<E, R> {
   private boolean built = false;
   private static final long b = 74666353650036L;

   @Override
   public Builder<E, R> addAll(Traversable<E> var1) {
      var1.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E var1) {
            AbstractBuilder.this.add(var1);
            return null;
         }
      });
      return this;
   }

   @Override
   public Builder<E, R> addAll(Iterable<E> var1) {
      for (Object var6 : var1) {
         this.add((E)var6);
      }

      return this;
   }

   @Override
   public Builder<E, R> addAll(Iterator<E> var1) {
      while (var1.hasNext()) {
         this.add((E)var1.next());
      }

      return this;
   }

   @Override
   public Builder<E, R> addAll(E var1, E var2, E... var3) {
      this.add((E)var1);
      this.add((E)var2);

      for (Object var11 : var3) {
         this.add((E)var11);
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
