package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class JvmArgumentsListOwner {
   private final ListContainerOwner<String> F;

   public JvmArgumentsListOwner() {
      JvmArgumentPatterns jvmArgumentPatterns = new JvmArgumentPatterns();
      this.F = new ListContainerOwner<>(jvmArgumentPatterns.N());
   }

   public ListContainerOwner<String> d() {
      return this.F;
   }
}
