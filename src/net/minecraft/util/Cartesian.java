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
   private static final long a = 118693516936280L;

   public static <T> Iterable<T[]> cartesianProduct(Class<T> var0, Iterable<? extends Iterable<? extends T>> var1) {
      return new Cartesian.Product<>(var0, toArray(Iterable.class, var1));
   }

   public static <T> Iterable<List<T>> cartesianProduct(Iterable<? extends Iterable<? extends T>> var0) {
      return arraysAsLists(cartesianProduct(Object.class, var0));
   }

   private static <T> Iterable<List<T>> arraysAsLists(Iterable<Object[]> var0) {
      return Iterables.transform(var0, new Cartesian.GetList<>());
   }

   private static <T> T[] toArray(Class<? super T> var0, Iterable<? extends T> var1) {
      ArrayList var5 = Lists.newArrayList();

      for (Object var7 : var1) {
         var5.add(var7);
      }

      return (T[])var5.toArray(createArray(var0, var5.size()));
   }

   private static <T> T[] createArray(Class<? super T> var0, int var1) {
      return (T[])((Object[])Array.newInstance(var0, var1));
   }

   public static class GetList<T> implements Function<Object[], List<T>> {
      public GetList() {
      }

      public List<T> apply(Object[] var1) {
         return Arrays.asList((T[])var1);
      }
   }

   public static class Product<T> implements Iterable<T[]> {
      private final Class<T> clazz;
      private final Iterable<? extends T>[] iterables;
      private static final long a = 100083686606487L;

      public Product(Class<T> var1, Iterable<? extends T>[] var2) {
         this.clazz = var1;
         this.iterables = var2;
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
         private static final long a = 67822640136776L;

         public ProductIterator(Class<T> var1, Iterable<? extends T>[] var2) {
            this.iterables = var2;
            this.iterators = Cartesian.createArray(Iterator.class, this.iterables.length);

            for (int var6 = 0; var6 < this.iterables.length; var6++) {
               this.iterators[var6] = var2[var6].iterator();
            }

            this.results = (T[])Cartesian.createArray(var1, this.iterators.length);
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

               for (Iterator var7 : this.iterators) {
                  if (!var7.hasNext()) {
                     this.endOfData();
                     break;
                  }
               }

               return true;
            } else {
               if (this.index >= this.iterators.length) {
                  for (this.index = this.iterators.length - 1; this.index >= 0; this.index--) {
                     Iterator var4 = this.iterators[this.index];
                     if (var4.hasNext()) {
                        break;
                     }

                     if (this.index == 0) {
                        this.endOfData();
                        break;
                     }

                     var4 = this.iterables[this.index].iterator();
                     this.iterators[this.index] = var4;
                     if (!var4.hasNext()) {
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
