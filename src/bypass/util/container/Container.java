package bypass.util.container;

import bypass.util.SelfTyped;

public abstract class Container<S> implements SelfTyped<Container<S>> {
   private S value;
   private ContainerSupplier supplier;

   public Container(S object) {
      this.setValue((S)object);
   }

   public <T extends S> S getValue() {
      return this.value;
   }

   public S setValue(S object) {
      this.value = (S)object;
      return this.value;
   }

   public ContainerSupplier getSupplier() {
      return this.supplier;
   }

   public ContainerSupplier setSupplier(ContainerSupplier containerSupplier) {
      this.supplier = containerSupplier;
      return this.supplier;
   }

   public abstract void initialize() throws ContainerException;

   public abstract void destroy() throws ContainerError;
}
