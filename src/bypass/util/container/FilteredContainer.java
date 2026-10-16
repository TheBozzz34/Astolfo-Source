package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public final class FilteredContainer<E> extends ListContainer<E> implements ContainerSupplier<ListContainer<E>> {
   private final Predicate<? super E> filter;
   private final ListContainer<E> source;
   private List<E> accepted;
   private List<E> rejected;

   public FilteredContainer(ListContainer<E> listContainer, Predicate<? super E> predicate, boolean bl) {
      super(listContainer.getValue());
      this.source = listContainer;
      this.filter = predicate;
      if (bl) {
         this.accepted = new ArrayList<>();
         this.rejected = new ArrayList<>();
      }
   }

   @Override
   public boolean addAll(E... objects) {
      boolean bl = false;

      for (int i = 0; i < objects.length; i++) {
         bl = this.testAndRecord((E)objects[i]);
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
      return this.testAndRecord((E)object) ? super.add((E)object) : false;
   }

   @Override
   public void insertAt(int i, E object) {
      if (this.testAndRecord((E)object)) {
         super.insertAt(i, (E)object);
      }
   }

   @Override
   public boolean addAllFrom(Collection<? extends E> collection) {
      for (Object object : collection) {
         if (!this.testAndRecord((E)object)) {
            return false;
         }
      }

      return super.addAllFrom(collection);
   }

   @Override
   public boolean addAllAt(int i, Collection<? extends E> collection) {
      for (Object object : collection) {
         if (!this.testAndRecord((E)object)) {
            return false;
         }
      }

      return super.addAllAt(i, collection);
   }

   private boolean testAndRecord(E object) {
      return this.filter != null
         ? (
            this.filter.test((E)object)
               ? (this.accepted != null ? this.accepted.add((E)object) : true)
               : (this.rejected != null ? !this.rejected.add((E)object) : false)
         )
         : true;
   }

   public List<E> getRejected() {
      return this.rejected;
   }

   public List<E> getAccepted() {
      return this.accepted;
   }

   public Predicate<? super E> getFilter() {
      return this.filter;
   }

   public ListContainer<E> getContainer() {
      return this.source;
   }
}
