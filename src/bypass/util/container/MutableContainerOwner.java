package bypass.util.container;

public abstract class MutableContainerOwner<T> extends ContainerOwner<T> {
   public CollectionMutator<T> T = new CollectionMutator<>(this.c);

   public CollectionContainer<T> L() {
      return this.c;
   }
}
