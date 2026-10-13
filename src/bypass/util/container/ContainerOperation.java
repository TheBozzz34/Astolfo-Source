package bypass.util.container;

public abstract class ContainerOperation {
   public abstract void W() throws ContainerException;

   public abstract void R() throws ContainerError;
}
