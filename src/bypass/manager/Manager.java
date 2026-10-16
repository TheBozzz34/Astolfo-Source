package bypass.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Manager<T> {
   public List<T> items = new Manager.au1();

   public List<T> getList() {
      return this.items;
   }

   @SafeVarargs
   public final void add(T... objects) {
      this.items.addAll(Arrays.asList((T[])objects));
   }

   @SafeVarargs
   public final void remove(T... objects) {
      this.items.removeAll(Arrays.asList(objects));
   }

   public boolean canAdd(T object) {
      return true;
   }

   public boolean canRemove(Object object) {
      return true;
   }

   public class au1 extends ArrayList<T> {
      @Override
      public boolean addAll(Collection<? extends T> collection) {
         for (Object object : collection) {
            Manager.this.canAdd((T)object);
         }

         return super.addAll(collection);
      }

      @Override
      public boolean add(T object) {
         return Manager.this.canAdd((T)object) && super.add(object);
      }

      @Override
      public void add(int i, T object) {
         if (Manager.this.canAdd((T)object)) {
            super.add(i, object);
         }
      }

      @Override
      public T remove(int i) {
         Object object = this.get(i);
         return (T)(Manager.this.canRemove(object) ? super.remove(i) : object);
      }

      @Override
      public boolean remove(Object object) {
         return Manager.this.canRemove(object) && super.remove(object);
      }
   }
}
