package com.github.andrewoma.dexx.collection.internal.builder;

public abstract class AbstractSelfBuilder<E, R> extends AbstractBuilder<E, R> {
   public R result;

   public AbstractSelfBuilder(R empty) {
      this.result = empty;
   }

   @Override
   public R doBuild() {
      return this.result;
   }
}
