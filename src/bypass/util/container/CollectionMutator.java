package bypass.util.container;

import java.util.Arrays;

public class CollectionMutator<T> extends Mutator<T> {
   private CollectionContainer<T> container;

   public CollectionMutator(CollectionContainer<T> collectionContainer) {
      this.container = collectionContainer;
   }

   @Override
   public void addAll(T... objects) {
      this.container.addAllFrom(Arrays.asList((T[])objects));
   }

   @Override
   public boolean remove(T object) {
      return this.container.remove((T)object);
   }
}
