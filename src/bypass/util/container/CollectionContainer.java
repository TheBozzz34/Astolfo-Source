package bypass.util.container;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

public abstract class CollectionContainer<E> extends Container<Collection<E>> {
   public CollectionContainer(Collection<E> collection) {
      super(collection);
   }

   @Override
   public void j() throws ContainerException {
   }

   @Override
   public void m() throws ContainerError {
   }

   public Collection<E> view() {
      return Collections.unmodifiableCollection(this.getValue());
   }

   public Collection<E> a(Collection<E> collection) {
      return this.setValue(collection);
   }

   public boolean addAll(E... objects) {
      return Collections.addAll(this.getValue(), (E[])objects);
   }

   public boolean add(E object) {
      return this.getValue().add((E)object);
   }

   public boolean k(E object) {
      return this.getValue().remove(object);
   }

   public int j_int() {
      return this.getValue().size();
   }

   public boolean N() {
      return this.getValue().isEmpty();
   }

   public boolean x() {
      return this.getValue() == null;
   }

   public void v_void() {
      this.getValue().clear();
   }

   public Optional<E> y(Class<? extends E> class2) {
      for (Object object : this.getValue()) {
         if (object.getClass() == class2) {
            return Optional.of((E)object);
         }
      }

      return Optional.empty();
   }

   public boolean k(Collection<? extends E> collection) {
      return this.getValue().addAll(collection);
   }

   public boolean R(Collection<?> collection) {
      return this.getValue().containsAll(collection);
   }

   public Iterator<E> m_Iterator() {
      return this.getValue().iterator();
   }

   public boolean M(Collection<?> collection) {
      return this.getValue().removeAll(collection);
   }

   public boolean a_boolean(Collection<?> collection) {
      return this.getValue().retainAll(collection);
   }

   public Object[] O() {
      return this.getValue().toArray();
   }

   public <E> E[] o(E[] objects) {
      return (E[])this.getValue().toArray(objects);
   }
}
