package bypass.util.container;

public abstract class ContainerOwner<T> {
   public CollectionContainer<T> c = Containers.g();
}
