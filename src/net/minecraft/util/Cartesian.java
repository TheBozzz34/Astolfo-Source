package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cartesian {
   public static <T> Iterable<T[]> cartesianProduct(Class<T> clazz, Iterable<? extends Iterable<? extends T>> sets) {
      return new Cartesian.Product<>(clazz, toArray(Iterable.class, sets));
   }

   public static <T> Iterable<List<T>> cartesianProduct(Iterable<? extends Iterable<? extends T>> sets) {
      return arraysAsLists(cartesianProduct(Object.class, sets));
   }

   private static <T> Iterable<List<T>> arraysAsLists(Iterable<Object[]> arrays) {
      return Iterables.transform(arrays, new Cartesian.GetList<>());
   }

   private static <T> T[] toArray(Class<? super T> clazz, Iterable<? extends T> it) {
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : it) {
         arrayList.add(object);
      }

      return (T[])arrayList.toArray(createArray(clazz, arrayList.size()));
   }

   private static <T> T[] createArray(Class<? super T> class2, int i) {
      return (T[])((Object[])Array.newInstance(class2, i));
   }

   public static class GetList<T> implements Function<Object[], List<T>> {
      public GetList() {
      }

      public List<T> apply(Object[] objects) {
         return Arrays.asList((T[])objects);
      }
   }

   public static class Product<T> implements Iterable<T[]> {
      private final Class<T> clazz;
      private final Iterable<? extends T>[] iterables;

      public Product(Class<T> clazz, Iterable<? extends T>[] iterables) {
         this.clazz = clazz;
         this.iterables = iterables;
      }

      @Override
      public Iterator<T[]> iterator() {
         return this.iterables.length <= 0
            ? (java.util.Iterator)(Collections.singletonList((T)Cartesian.createArray(this.clazz, 0)).iterator())
            : new Cartesian.Product.ProductIterator<>(this.clazz, this.iterables);
      }

      public static class ProductIterator<T> extends UnmodifiableIterator<T[]> {
         private int index = -2;
         private final Iterable<? extends T>[] iterables;
         private final Iterator<? extends T>[] iterators;
         private final T[] results;

         public ProductIterator(Class<T> clazzIn, Iterable<? extends T>[] iterablesIn) {
            this.iterables = iterablesIn;
            this.iterators = Cartesian.createArray(Iterator.class, this.iterables.length);

            for (int i = 0; i < this.iterables.length; i++) {
               this.iterators[i] = iterablesIn[i].iterator();
            }

            this.results = (T[])Cartesian.createArray(clazzIn, this.iterators.length);
         }

         private void endOfData() {
            this.index = -1;
            Arrays.fill(this.iterators, null);
            Arrays.fill(this.results, null);
         }

         @Override
         public boolean hasNext() {
            if (this.index == -2) {
               this.index = 0;

               for (Iterator iterator2 : this.iterators) {
                  if (!iterator2.hasNext()) {
                     this.endOfData();
                     break;
                  }
               }

               return true;
            } else {
               if (this.index >= this.iterators.length) {
                  for (this.index = this.iterators.length - 1; this.index >= 0; this.index--) {
                     Iterator iterator3 = this.iterators[this.index];
                     if (iterator3.hasNext()) {
                        break;
                     }

                     if (this.index == 0) {
                        this.endOfData();
                        break;
                     }

                     iterator3 = this.iterables[this.index].iterator();
                     this.iterators[this.index] = iterator3;
                     if (!iterator3.hasNext()) {
                        this.endOfData();
                        break;
                     }
                  }
               }

               return this.index >= 0;
            }
         }

         public T[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            }

            while (this.index < this.iterators.length) {
               this.results[this.index] = (T)this.iterators[this.index].next();
               this.index++;
            }

            return (T[])((Object[])this.results.clone());
         }
      }
   }
}
