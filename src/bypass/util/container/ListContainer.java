package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class ListContainer<E> extends CollectionContainer<E> {
   private static final long d = 26694049125338L;

   public ListContainer() {
      super(new ArrayList<>());
      this.I(this.getValue());
   }

   public ListContainer(List<E> list) {
      super(list);
      this.I(list);
   }

   @Override
   public void j() throws ContainerException {
      this.b();
   }

   @Override
   public void m() throws ContainerError {
      this.r();
   }

   public void b() {
      this.a(!this.x() ? this.getValue() : new ArrayList<>());
   }

   public void r() {
      this.a(null);
   }

   public List<E> getValue() {
      return (List<E>)super.getValue();
   }

   public List<E> I(List<E> list) {
      return (List<E>)super.setValue(list);
   }

   public List<E> Z() {
      return Collections.unmodifiableList(this.getValue());
   }

   public void b(int i, E object) {
      this.getValue().add(i, (E)object);
   }

   public E o(int i) {
      return this.getValue().remove(i);
   }

   public E k(int i, E object) {
      return this.getValue().set(i, (E)object);
   }

   public E J(int i) {
      return this.getValue().get(i);
   }

   public Optional<E> G(int i) {
      return Optional.ofNullable(this.getValue().get(i));
   }

   public <T extends E> Optional<T> z(Class<T> class2) {
      for (Object object : this.getValue()) {
         if (object.getClass() == class2) {
            return Optional.of((T)object);
         }
      }

      return Optional.empty();
   }

   public int u(E object) {
      return this.getValue().indexOf(object);
   }

   public int d(E object) {
      return this.getValue().lastIndexOf(object);
   }

   public boolean Z(int i, Collection<? extends E> collection) {
      return this.getValue().addAll(i, collection);
   }

   public ListIterator<E> h() {
      return this.getValue().listIterator();
   }

   public ListIterator<E> X(int i) {
      return this.getValue().listIterator(i);
   }
}
