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

   public ClassInheritanceMultiMap(Class<T> baseClassIn) {
      this.baseClass = baseClassIn;
      this.knownKeys.add(baseClassIn);
      this.map.put(baseClassIn, this.values);

      for (Class class2 : field_181158_a) {
         this.createLookup(class2);
      }
   }

   public void createLookup(Class<?> clazz) {
      field_181158_a.add(clazz);

      for (Object object : this.values) {
         if (clazz.isAssignableFrom(object.getClass())) {
            this.addForClass((T)object, clazz);
         }
      }

      this.knownKeys.add(clazz);
   }

   public Class<?> initializeClassLookup(Class<?> clazz) {
      if (this.baseClass.isAssignableFrom(clazz)) {
         if (!this.knownKeys.contains(clazz)) {
            this.createLookup(clazz);
         }

         return clazz;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + clazz);
      }
   }

   @Override
   public boolean add(T object) {
      for (Class class2 : this.knownKeys) {
         if (class2.isAssignableFrom(object.getClass())) {
            this.addForClass((T)object, class2);
         }
      }

      return true;
   }

   private void addForClass(T value, Class<?> parentClass) {
      List list = this.map.get(parentClass);
      if (list == null) {
         this.map.put(parentClass, Lists.newArrayList((T[])(new Object[]{value})));
      } else {
         list.add(value);
      }
   }

   @Override
   public boolean remove(Object object) {
      Object object2 = object;
      boolean bl = false;

      for (Class class2 : this.knownKeys) {
         if (class2.isAssignableFrom(object2.getClass())) {
            List list = this.map.get(class2);
            if (list != null && list.remove(object2)) {
               bl = true;
            }
         }
      }

      return bl;
   }

   @Override
   public boolean contains(Object object) {
      return Iterators.contains(this.getByClass(object.getClass()).iterator(), object);
   }

   public <S> Iterable<S> getByClass(final Class<S> clazz) {
      return new Iterable<S>() {
         @Override
         public Iterator<S> iterator() {
            List list = ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.initializeClassLookup(clazz));
            if (list == null) {
               return Iterators.emptyIterator();
            }

            Iterator iterator2 = list.iterator();
            return Iterators.filter(iterator2, clazz);
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
