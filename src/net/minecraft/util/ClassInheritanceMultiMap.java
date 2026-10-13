package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   private static final Set<Class<?>> field_181158_a = Sets.newHashSet();
   private final Map<Class<?>, List<T>> map = Maps.newHashMap();
   private final Set<Class<?>> knownKeys = Sets.newIdentityHashSet();
   private final Class<T> baseClass;
   private final List<T> values = Lists.newArrayList();
   private static final long a = 36911567704328L;

   public ClassInheritanceMultiMap(Class<T> var1) {
      this.baseClass = var1;
      this.knownKeys.add(var1);
      this.map.put(var1, this.values);

      for (Class var6 : field_181158_a) {
         this.createLookup(var6);
      }
   }

   public void createLookup(Class<?> var1) {
      field_181158_a.add(var1);

      for (Object var6 : this.values) {
         if (var1.isAssignableFrom(var6.getClass())) {
            this.addForClass((T)var6, var1);
         }
      }

      this.knownKeys.add(var1);
   }

   public Class<?> initializeClassLookup(Class<?> var1) {
      if (this.baseClass.isAssignableFrom(var1)) {
         if (!this.knownKeys.contains(var1)) {
            this.createLookup(var1);
         }

         return var1;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + var1);
      }
   }

   @Override
   public boolean add(T var1) {
      for (Class var6 : this.knownKeys) {
         if (var6.isAssignableFrom(var1.getClass())) {
            this.addForClass((T)var1, var6);
         }
      }

      return true;
   }

   private void addForClass(T var1, Class<?> var2) {
      List var6 = this.map.get(var2);
      if (var6 == null) {
         this.map.put(var2, Lists.newArrayList((T[])(new Object[]{var1})));
      } else {
         var6.add(var1);
      }
   }

   @Override
   public boolean remove(Object var1) {
      Object var5 = var1;
      boolean var6 = false;

      for (Class var8 : this.knownKeys) {
         if (var8.isAssignableFrom(var5.getClass())) {
            List var9 = this.map.get(var8);
            if (var9 != null && var9.remove(var5)) {
               var6 = true;
            }
         }
      }

      return var6;
   }

   @Override
   public boolean contains(Object var1) {
      return Iterators.contains(this.getByClass(var1.getClass()).iterator(), var1);
   }

   public <S> Iterable<S> getByClass(final Class<S> var1) {
      return new Iterable<S>() {
         private static final long a = 64355247651033L;

         @Override
         public Iterator<S> iterator() {
            List var4 = ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.initializeClassLookup(var1));
            if (var4 == null) {
               return Iterators.emptyIterator();
            }

            Iterator var5 = var4.iterator();
            return Iterators.filter(var5, var1);
         }
      };
   }

   @Override
   public Iterator<T> iterator() {
      return this.values.isEmpty() ? Iterators.emptyIterator() : Iterators.unmodifiableIterator(this.values.iterator());
   }

   @Override
   public int size() {
      return this.values.size();
   }
}
