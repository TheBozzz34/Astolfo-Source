package bypass.util.container;

import java.util.Arrays;

public class CollectionMutator<T> extends Mutator<T> {
   private CollectionContainer<T> F;
   private static final long a = 69735078805085L;

   public CollectionMutator(CollectionContainer<T> var1) {
      this.F = var1;
   }

   @Override
   public void q(T... var1) {
      this.F.k(Arrays.asList((T[])var1));
   }

   @Override
   public boolean k(T var1) {
      return this.F.k((T)var1);
   }
}
