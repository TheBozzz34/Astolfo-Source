package com.github.andrewoma.dexx.collection.internal.adapter;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.lang.reflect.Array;
import java.util.Collection;

public class Adapters {
   private static final long a = 137998105838854L;

   public static <E> boolean containsAll(Traversable<E> traversable, Collection<?> c) {
      for (Object object : c) {
         if (!contains(traversable, object)) {
            return false;
         }
      }

      return true;
   }

   public static <E> boolean contains(Traversable<E> traversable, final Object o) {
      try {
         traversable.forEach(new Function<E, Object>() {
            private static final long a = 82638705275494L;

            @Override
            public Object invoke(E eIn) {
               if (eIn.equals(o)) {
                  throw new RuntimeException();
               } else {
                  return null;
               }
            }
         });
         return false;
      } catch (Exception eIn) {
         return true;
      }
   }

   public static <T, E> T[] toArray(Traversable<E> traversable, T[] aIn) {
      int i = traversable.size();
      final Object[] objects = aIn.length >= i ? aIn : (Object[])Array.newInstance(aIn.getClass().getComponentType(), i);
      final int[] ints = new int[]{0};
      traversable.forEach(new Function<E, Object>() {
         private static final long a = 127875383888408L;

         @Override
         public Object invoke(E eIn) {
            objects[ints[0]] = eIn;
            ints[0]++;
            return null;
         }
      });
      return (T[])objects;
   }
}
