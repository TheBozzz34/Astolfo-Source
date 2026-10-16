package bypass.util.container;

public abstract class ContainerOperation {
   public abstract void initialize() throws ContainerException;

   public abstract void destroy() throws ContainerError;
}
