package net.minecraft.util;

public abstract class LazyLoadBase<T> {
   private T value;
   private boolean isLoaded = false;
   private static final long b = 84000559485965L;

   public T getValue() {
      if (!this.isLoaded) {
         this.isLoaded = true;
         this.value = this.load();
      }

      return this.value;
   }

   public abstract T load();
}
