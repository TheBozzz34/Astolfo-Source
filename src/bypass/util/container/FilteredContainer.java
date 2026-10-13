package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public final class FilteredContainer<E> extends ListContainer<E> implements ContainerSupplier<ListContainer<E>> {
   private final Predicate<? super E> s;
   private final ListContainer<E> m;
   private List<E> C;
   private List<E> i;
   private static final long b = 103208129523326L;

   public FilteredContainer(ListContainer<E> var1, Predicate<? super E> var2, boolean var3) {
      super(var1.getValue());
      this.m = var1;
      this.s = var2;
      if (var3) {
         this.C = new ArrayList<>();
         this.i = new ArrayList<>();
      }
   }

   @Override
   public boolean addAll(E... var1) {
      boolean var5 = false;

      for (int var6 = 0; var6 < var1.length; var6++) {
         var5 = this.A((E)var1[var6]);
         if (var5) {
            super.add((E)var1[var6]);
         }

         if (var6 == var1.length) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean add(E var1) {
      return this.A((E)var1) ? super.add((E)var1) : false;
   }

   @Override
   public void b(int var1, E var2) {
      if (this.A((E)var2)) {
         super.b(var1, (E)var2);
      }
   }

   @Override
   public boolean k(Collection<? extends E> var1) {
      for (Object var6 : var1) {
         if (!this.A((E)var6)) {
            return false;
         }
      }

      return super.k(var1);
   }

   @Override
   public boolean Z(int var1, Collection<? extends E> var2) {
      for (Object var7 : var2) {
         if (!this.A((E)var7)) {
            return false;
         }
      }

      return super.Z(var1, var2);
   }

   private boolean A(E var1) {
      return this.s != null ? (this.s.test((E)var1) ? (this.C != null ? this.C.add((E)var1) : true) : (this.i != null ? !this.i.add((E)var1) : false)) : true;
   }

   public List<E> w() {
      return this.i;
   }

   public List<E> g() {
      return this.C;
   }

   public Predicate<? super E> l() {
      return this.s;
   }

   public ListContainer<E> W() {
      return this.m;
   }
}
