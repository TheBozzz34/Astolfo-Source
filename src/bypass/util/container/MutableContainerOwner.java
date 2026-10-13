package bypass.util.container;

public abstract class MutableContainerOwner<T> extends ContainerOwner<T> {
   public CollectionMutator<T> T = new CollectionMutator<>(this.c);
   private static final long b = 114858932665907L;

   public CollectionContainer<T> L() {
      return this.c;
   }
}
