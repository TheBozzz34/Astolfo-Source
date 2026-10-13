package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class JvmArgumentsPropertyListOwner {
   private final ListContainerOwner<String> Z;

   public JvmArgumentsPropertyListOwner() {
      JvmArgumentPropertyPatterns var1 = new JvmArgumentPropertyPatterns();
      this.Z = new ListContainerOwner<>(var1.Z());
   }

   public ListContainerOwner<String> k() {
      return this.Z;
   }
}
