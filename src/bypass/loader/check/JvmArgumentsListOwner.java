package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class JvmArgumentsListOwner {
   private final ListContainerOwner<String> F;

   public JvmArgumentsListOwner() {
      JvmArgumentPatterns var1 = new JvmArgumentPatterns();
      this.F = new ListContainerOwner<>(var1.N());
   }

   public ListContainerOwner<String> d() {
      return this.F;
   }
}
