package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Builder;
import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.lang.reflect.Array;

public abstract class AbstractTraversable<E> implements Traversable<E> {
   private static final long e = 30229383558062L;
   private static final long l = 6162024368806297700L;

   @Override
   public int size() {
      final int[] var1 = new int[]{0};
      this.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E var1x) {
            return var1[0]++;
         }
      });
      return var1[0];
   }

   @Override
   public String makeString(String var1) {
      return this.makeString(var1, "", "", -1, "");
   }

   @Override
   public String makeString(final String var1, String var2, String var3, final int var4, String var5) {
      final StringBuilder var9 = new StringBuilder(var2);
      final int[] var10 = new int[]{0};

      try {
         this.forEach(new Function<E, Object>() {
            private static final long a = 23852266951153L;

            @Override
            public Object invoke(E var1x) {
               int var6 = var10[0] + 1;
               var10[0] = var6;
               if (var6 > 1) {
                  var9.append(var1);
               }

               if (var4 >= 0 && var6 > var4) {
                  throw new RuntimeException();
               }

               var9.append(var1x == null ? "null" : var1x.toString());
               return null;
            }
         });
      } catch (Exception var12) {
      }

      if (var4 >= 0 && var10[0] > var4) {
         var9.append(var5);
      }

      var9.append(var3);
      return var9.toString();
   }

   @Override
   public String toString() {
      return this.makeString(", ", this.getClass().getSimpleName() + "(", ")", (int)l, "...");
   }

   @Override
   public <R extends Traversable<E>> R to(Builder<E, R> var1) {
      return var1.addAll(this).build();
   }

   @Override
   public boolean isEmpty() {
      try {
         this.forEach(new Function<E, Object>() {
            @Override
            public Object invoke(E var1) {
               throw new RuntimeException();
            }
         });
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   @Override
   public Object[] toArray() {
      final Object[] var1 = new Object[this.size()];
      final int[] var2 = new int[]{0};
      this.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E var1x) {
            var1[var2[0]] = var1x;
            var2[0]++;
            return null;
         }
      });
      return var1;
   }

   @Override
   public E[] toArray(E[] var1) {
      int var5 = this.size();
      final Object[] var6 = var1.length >= var5 ? var1 : (Object[])Array.newInstance(var1.getClass().getComponentType(), var5);
      final int[] var7 = new int[]{0};
      this.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E var1) {
            var6[var7[0]] = var1;
            var7[0]++;
            return null;
         }
      });
      return (E[])var6;
   }
}
