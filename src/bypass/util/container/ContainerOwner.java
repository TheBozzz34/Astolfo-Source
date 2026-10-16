package bypass.util.container;

public abstract class ContainerOwner<T> {
   public CollectionContainer<T> container = Containers.createList();
}
