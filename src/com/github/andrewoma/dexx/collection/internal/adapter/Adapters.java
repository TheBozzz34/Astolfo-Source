package com.github.andrewoma.dexx.collection.internal.adapter;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.lang.reflect.Array;
import java.util.Collection;

public class Adapters {
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
            @Override
            public Object invoke(E e) {
               if (e.equals(o)) {
                  throw new RuntimeException();
               } else {
                  return null;
               }
            }
         });
         return false;
      } catch (Exception e) {
         return true;
      }
   }

   public static <T, E> T[] toArray(Traversable<E> traversable, T[] a) {
      int i = traversable.size();
      final Object[] objects = a.length >= i ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), i);
      final int[] ints = new int[]{0};
      traversable.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E e) {
            objects[ints[0]] = e;
            ints[0]++;
            return null;
         }
      });
      return (T[])objects;
   }
}
