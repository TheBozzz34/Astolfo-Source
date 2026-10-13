package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ConvertingListContainer<A, B> extends ListContainer<B> implements ContainerSupplier<ListContainer<B>> {
   private ListContainer<B> N;
   private Function<A, B> Z;
   private Function<B, A> U;
   private static final long b = 63959488470954L;

   public ConvertingListContainer(ListContainer<B> var1, Function<A, B> var2, Function<B, A> var3) {
      super(var1.getValue());
      this.N = var1;
      this.Z = var2;
      this.U = var3;
   }

   public void Y(A... var1) {
      for (Object var8 : var1) {
         super.add(this.T((A)var8));
      }
   }

   public boolean b(A var1) {
      return this.add(this.T((A)var1));
   }

   public void Y(int var1, A var2) {
      super.b(var1, this.T((A)var2));
   }

   public boolean convertAndAddAll(Collection<? extends A> var1) {
      ArrayList var2 = new ArrayList();
      var1.stream().forEach(var2x -> var2.add(this.T((A)var2x)));
      return super.k(var2);
   }

   public boolean convertAndAddAll(int var1, Collection<? extends A> var2) {
      ArrayList var6 = new ArrayList();
      var2.stream().forEach(var2x -> var6.add(this.T((A)var2x)));
      return super.Z(var1, var6);
   }

   private B T(A var1) {
      if (this.Z == null) {
         throw new NullPointerException("The function that converts given element into another can't be null.");
      } else {
         return this.Z.apply((A)var1);
      }
   }

   private A v(B var1) {
      if (this.U == null) {
         throw new NullPointerException("The function that converts given element into another can't be null.");
      } else {
         return this.U.apply((B)var1);
      }
   }

   public List<A> createUnconvertedList() {
      ArrayList var1 = new ArrayList();
      this.getValue().stream().forEach(var2 -> var1.add(this.v((B)var2)));
      return var1;
   }

   public Function<A, B> l() {
      return this.Z;
   }

   public Function<A, B> e() {
      return this.Z;
   }

   public ListContainer<B> W() {
      return this.N;
   }
}
