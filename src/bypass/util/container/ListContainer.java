package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class ListContainer<E> extends CollectionContainer<E> {
   public ListContainer() {
      super(new ArrayList<>());
      this.setList(this.getValue());
   }

   public ListContainer(List<E> list) {
      super(list);
      this.setList(list);
   }

   @Override
   public void initialize() throws ContainerException {
      this.ensureList();
   }

   @Override
   public void destroy() throws ContainerError {
      this.releaseList();
   }

   public void ensureList() {
      this.setCollection(!this.isNull() ? this.getValue() : new ArrayList<>());
   }

   public void releaseList() {
      this.setCollection(null);
   }

   public List<E> getValue() {
      return (List<E>)super.getValue();
   }

   public List<E> setList(List<E> list) {
      return (List<E>)super.setValue(list);
   }

   public List<E> viewList() {
      return Collections.unmodifiableList(this.getValue());
   }

   public void insertAt(int i, E object) {
      this.getValue().add(i, (E)object);
   }

   public E removeAt(int i) {
      return this.getValue().remove(i);
   }

   public E setAt(int i, E object) {
      return this.getValue().set(i, (E)object);
   }

   public E getAt(int i) {
      return this.getValue().get(i);
   }

   public Optional<E> getOptional(int i) {
      return Optional.ofNullable(this.getValue().get(i));
   }

   public <T extends E> Optional<T> findFirstOfClass(Class<T> class2) {
      for (Object object : this.getValue()) {
         if (object.getClass() == class2) {
            return Optional.of((T)object);
         }
      }

      return Optional.empty();
   }

   public int indexOf(E object) {
      return this.getValue().indexOf(object);
   }

   public int lastIndexOf(E object) {
      return this.getValue().lastIndexOf(object);
   }

   public boolean addAllAt(int i, Collection<? extends E> collection) {
      return this.getValue().addAll(i, collection);
   }

   public ListIterator<E> listIterator() {
      return this.getValue().listIterator();
   }

   public ListIterator<E> listIteratorAt(int i) {
      return this.getValue().listIterator(i);
   }
}
