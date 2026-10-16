package bypass.util.container;

public abstract class MutableContainerOwner<T> extends ContainerOwner<T> {
   public CollectionMutator<T> mutator = new CollectionMutator<>(this.container);

   public CollectionContainer<T> getOwnedContainer() {
      return this.container;
   }
}
