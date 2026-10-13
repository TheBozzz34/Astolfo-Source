package com.github.andrewoma.dexx.collection.internal.builder;

public abstract class AbstractSelfBuilder<E, R> extends AbstractBuilder<E, R> {
   public R result;

   public AbstractSelfBuilder(R var1) {
      this.result = (R)var1;
   }

   @Override
   public R doBuild() {
      return this.result;
   }
}
