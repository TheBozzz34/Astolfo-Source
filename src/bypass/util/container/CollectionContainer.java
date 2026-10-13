package bypass.util.container;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

public abstract class CollectionContainer<E> extends Container<Collection<E>> {
   private static final long c = 127847277921579L;

   public CollectionContainer(Collection<E> var1) {
      super(var1);
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

   public Collection<E> a(Collection<E> var1) {
      return this.setValue(var1);
   }

   public boolean addAll(E... var1) {
      return Collections.addAll(this.getValue(), (E[])var1);
   }

   public boolean add(E var1) {
      return this.getValue().add((E)var1);
   }

   public boolean k(E var1) {
      return this.getValue().remove(var1);
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

   public Optional<E> y(Class<? extends E> var1) {
      for (Object var6 : this.getValue()) {
         if (var6.getClass() == var1) {
            return Optional.of((E)var6);
         }
      }

      return Optional.empty();
   }

   public boolean k(Collection<? extends E> var1) {
      return this.getValue().addAll(var1);
   }

   public boolean R(Collection<?> var1) {
      return this.getValue().containsAll(var1);
   }

   public Iterator<E> m_Iterator() {
      return this.getValue().iterator();
   }

   public boolean M(Collection<?> var1) {
      return this.getValue().removeAll(var1);
   }

   public boolean a_boolean(Collection<?> var1) {
      return this.getValue().retainAll(var1);
   }

   public Object[] O() {
      return this.getValue().toArray();
   }

   public <E> E[] o(E[] var1) {
      return (E[])this.getValue().toArray(var1);
   }
}
