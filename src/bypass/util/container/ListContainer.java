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

   public ListContainer(List<E> var1) {
      super(var1);
      this.I(var1);
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

   public List<E> I(List<E> var1) {
      return (List<E>)super.setValue(var1);
   }

   public List<E> Z() {
      return Collections.unmodifiableList(this.getValue());
   }

   public void b(int var1, E var2) {
      this.getValue().add(var1, (E)var2);
   }

   public E o(int var1) {
      return this.getValue().remove(var1);
   }

   public E k(int var1, E var2) {
      return this.getValue().set(var1, (E)var2);
   }

   public E J(int var1) {
      return this.getValue().get(var1);
   }

   public Optional<E> G(int var1) {
      return Optional.ofNullable(this.getValue().get(var1));
   }

   public <T extends E> Optional<T> z(Class<T> var1) {
      for (Object var6 : this.getValue()) {
         if (var6.getClass() == var1) {
            return Optional.of((T)var6);
         }
      }

      return Optional.empty();
   }

   public int u(E var1) {
      return this.getValue().indexOf(var1);
   }

   public int d(E var1) {
      return this.getValue().lastIndexOf(var1);
   }

   public boolean Z(int var1, Collection<? extends E> var2) {
      return this.getValue().addAll(var1, var2);
   }

   public ListIterator<E> h() {
      return this.getValue().listIterator();
   }

   public ListIterator<E> X(int var1) {
      return this.getValue().listIterator(var1);
   }
}
