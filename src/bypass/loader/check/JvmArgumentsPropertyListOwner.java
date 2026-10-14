package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class JvmArgumentsPropertyListOwner {
   private final ListContainerOwner<String> Z;

   public JvmArgumentsPropertyListOwner() {
      JvmArgumentPropertyPatterns jvmArgumentPropertyPatterns = new JvmArgumentPropertyPatterns();
      this.Z = new ListContainerOwner<>(jvmArgumentPropertyPatterns.Z());
   }

   public ListContainerOwner<String> k() {
      return this.Z;
   }
}
