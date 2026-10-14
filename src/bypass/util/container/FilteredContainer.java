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

   public FilteredContainer(ListContainer<E> listContainer, Predicate<? super E> predicate, boolean bl) {
      super(listContainer.getValue());
      this.m = listContainer;
      this.s = predicate;
      if (bl) {
         this.C = new ArrayList<>();
         this.i = new ArrayList<>();
      }
   }

   @Override
   public boolean addAll(E... objects) {
      boolean bl = false;

      for (int i = 0; i < objects.length; i++) {
         bl = this.A((E)objects[i]);
         if (bl) {
            super.add((E)objects[i]);
         }

         if (i == objects.length) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean add(E object) {
      return this.A((E)object) ? super.add((E)object) : false;
   }

   @Override
   public void b(int i, E object) {
      if (this.A((E)object)) {
         super.b(i, (E)object);
      }
   }

   @Override
   public boolean k(Collection<? extends E> collection) {
      for (Object object : collection) {
         if (!this.A((E)object)) {
            return false;
         }
      }

      return super.k(collection);
   }

   @Override
   public boolean Z(int i, Collection<? extends E> collection) {
      for (Object object : collection) {
         if (!this.A((E)object)) {
            return false;
         }
      }

      return super.Z(i, collection);
   }

   private boolean A(E object) {
      return this.s != null
         ? (this.s.test((E)object) ? (this.C != null ? this.C.add((E)object) : true) : (this.i != null ? !this.i.add((E)object) : false))
         : true;
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
