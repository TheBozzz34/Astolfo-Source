package bypass.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Manager<T> {
   public List<T> C = new Manager.au1();

   public List<T> getList() {
      return this.C;
   }

   @SafeVarargs
   public final void add(T... var1) {
      this.C.addAll(Arrays.asList((T[])var1));
   }

   @SafeVarargs
   public final void remove(T... var1) {
      this.C.removeAll(Arrays.asList(var1));
   }

   public boolean canAdd(T var1) {
      return true;
   }

   public boolean canRemove(Object var1) {
      return true;
   }

   public class au1 extends ArrayList<T> {
      private static final long a = 39446278696327L;

      @Override
      public boolean addAll(Collection<? extends T> var1) {
         for (Object var6 : var1) {
            Manager.this.canAdd((T)var6);
         }

         return super.addAll(var1);
      }

      @Override
      public boolean add(T var1) {
         return Manager.this.canAdd((T)var1) && super.add(var1);
      }

      @Override
      public void add(int var1, T var2) {
         if (Manager.this.canAdd((T)var2)) {
            super.add(var1, var2);
         }
      }

      @Override
      public T remove(int var1) {
         Object var5 = this.get(var1);
         return (T)(Manager.this.canRemove(var5) ? super.remove(var1) : var5);
      }

      @Override
      public boolean remove(Object var1) {
         return Manager.this.canRemove(var1) && super.remove(var1);
      }
   }
}
