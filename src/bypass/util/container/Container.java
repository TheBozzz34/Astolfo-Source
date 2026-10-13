package bypass.util.container;

import bypass.util.SelfTyped;

public abstract class Container<S> implements SelfTyped<Container<S>> {
   private S q;
   private ContainerSupplier R;
   private static final long a = 124214587787650L;

   public Container(S var1) {
      this.setValue((S)var1);
   }

   public <T extends S> S getValue() {
      return this.q;
   }

   public S setValue(S var1) {
      this.q = (S)var1;
      return this.q;
   }

   public ContainerSupplier i() {
      return this.R;
   }

   public ContainerSupplier G(ContainerSupplier var1) {
      this.R = var1;
      return this.R;
   }

   public abstract void j() throws ContainerException;

   public abstract void m() throws ContainerError;
}
