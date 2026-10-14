package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ConvertingListContainer<A, B> extends ListContainer<B> implements ContainerSupplier<ListContainer<B>> {
   private ListContainer<B> N;
   private Function<A, B> Z;
   private Function<B, A> U;

   public ConvertingListContainer(ListContainer<B> listContainer, Function<A, B> function, Function<B, A> function2) {
      super(listContainer.getValue());
      this.N = listContainer;
      this.Z = function;
      this.U = function2;
   }

   public void Y(A... objects) {
      for (Object object : objects) {
         super.add(this.T((A)object));
      }
   }

   public boolean b(A object) {
      return this.add(this.T((A)object));
   }

   public void Y(int i, A object) {
      super.b(i, this.T((A)object));
   }

   public boolean convertAndAddAll(Collection<? extends A> collection) {
      ArrayList arrayList = new ArrayList();
      collection.stream().forEach(value -> arrayList.add(this.T((A)value)));
      return super.k(arrayList);
   }

   public boolean convertAndAddAll(int i, Collection<? extends A> collection) {
      ArrayList arrayList = new ArrayList();
      collection.stream().forEach(value -> arrayList.add(this.T((A)value)));
      return super.Z(i, arrayList);
   }

   private B T(A object) {
      if (this.Z == null) {
         throw new NullPointerException("The function that converts given element into another can't be null.");
      } else {
         return this.Z.apply((A)object);
      }
   }

   private A v(B object) {
      if (this.U == null) {
         throw new NullPointerException("The function that converts given element into another can't be null.");
      } else {
         return this.U.apply((B)object);
      }
   }

   public List<A> createUnconvertedList() {
      ArrayList arrayList = new ArrayList();
      this.getValue().stream().forEach(value -> arrayList.add(this.v((B)value)));
      return arrayList;
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
