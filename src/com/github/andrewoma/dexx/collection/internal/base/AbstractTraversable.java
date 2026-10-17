package com.github.andrewoma.dexx.collection.internal.base;

import com.github.andrewoma.dexx.collection.Builder;
import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Traversable;
import java.lang.reflect.Array;

public abstract class AbstractTraversable<E> implements Traversable<E> {
   @Override
   public int size() {
      final int[] result = new int[]{0};
      this.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E parameter) {
            return result[0]++;
         }
      });
      return result[0];
   }

   @Override
   public String makeString(String separator) {
      return this.makeString(separator, "", "", -1, "");
   }

   @Override
   public String makeString(final String separator, String prefix, String postfix, final int limit, String truncated) {
      final StringBuilder stringBuilder = new StringBuilder(prefix);
      final int[] ints = new int[]{0};

      try {
         this.forEach(new Function<E, Object>() {
            @Override
            public Object invoke(E element) {
               int i = ints[0] + 1;
               ints[0] = i;
               if (i > 1) {
                  stringBuilder.append(separator);
               }

               if (limit >= 0 && i > limit) {
                  throw new RuntimeException();
               }

               stringBuilder.append(element == null ? "null" : element.toString());
               return null;
            }
         });
      } catch (Exception exception) {
      }

      if (limit >= 0 && ints[0] > limit) {
         stringBuilder.append(truncated);
      }

      stringBuilder.append(postfix);
      return stringBuilder.toString();
   }

   @Override
   public String toString() {
      return this.makeString(", ", this.getClass().getSimpleName() + "(", ")", 100, "...");
   }

   @Override
   public <R extends Traversable<E>> R to(Builder<E, R> builder) {
      return builder.addAll(this).build();
   }

   @Override
   public boolean isEmpty() {
      try {
         this.forEach(new Function<E, Object>() {
            @Override
            public Object invoke(E parameter) {
               throw new RuntimeException();
            }
         });
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   @Override
   public Object[] toArray() {
      final Object[] result = new Object[this.size()];
      final int[] count = new int[]{0};
      this.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E parameter) {
            result[count[0]] = parameter;
            count[0]++;
            return null;
         }
      });
      return result;
   }

   @Override
   public E[] toArray(E[] array) {
      int i = this.size();
      final Object[] objects = array.length >= i ? array : (Object[])Array.newInstance(array.getClass().getComponentType(), i);
      final int[] ints = new int[]{0};
      this.forEach(new Function<E, Object>() {
         @Override
         public Object invoke(E parameter) {
            objects[ints[0]] = parameter;
            ints[0]++;
            return null;
         }
      });
      return (E[])objects;
   }
}
