package com.github.andrewoma.dexx.collection.internal.adapter;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.lang.reflect.Array;
import java.util.Collection;

public class Adapters {
   private static final long a = 137998105838854L;

   public static <E> boolean containsAll(Traversable<E> var0, Collection<?> var1) {
      for (Object var6 : var1) {
         if (!contains(var0, var6)) {
            return false;
         }
      }

      return true;
   }

   public static <E> boolean contains(Traversable<E> var0, final Object var1) {
      try {
         var0.forEach(new Function<E, Object>() {
            private static final long a = 82638705275494L;

            @Override
            public Object invoke(E var1x) {
               if (var1x.equals(var1)) {
                  throw new RuntimeException();
               } else {
                  return null;
               }
            }
         });
         return false;
      } catch (Exception var3) {
         return true;
      }
   }

   public static <T, E> T[] toArray(Traversable<E> var0, T[] var1) {
      int var6 = var0.size();
      final Object[] var7 = var1.length >= var6 ? var1 : (Object[])Array.newInstance(var1.getClass().getComponentType(), var6);
      final int[] var8 = new int[]{0};
      var0.forEach(new Function<E, Object>() {
         private static final long a = 127875383888408L;

         @Override
         public Object invoke(E var1) {
            var7[var8[0]] = var1;
            var8[0]++;
            return null;
         }
      });
      return (T[])var7;
   }
}
