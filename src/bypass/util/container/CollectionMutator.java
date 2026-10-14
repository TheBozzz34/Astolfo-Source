package bypass.util.container;

import java.util.Arrays;

public class CollectionMutator<T> extends Mutator<T> {
   private CollectionContainer<T> F;

   public CollectionMutator(CollectionContainer<T> collectionContainer) {
      this.F = collectionContainer;
   }

   @Override
   public void q(T... objects) {
      this.F.k(Arrays.asList((T[])objects));
   }

   @Override
   public boolean k(T object) {
      return this.F.k((T)object);
   }
}
