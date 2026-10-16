package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class JvmArgumentsPropertyListOwner {
   private final ListContainerOwner<String> patternContainer;

   public JvmArgumentsPropertyListOwner() {
      JvmArgumentPropertyPatterns jvmArgumentPropertyPatterns = new JvmArgumentPropertyPatterns();
      this.patternContainer = new ListContainerOwner<>(jvmArgumentPropertyPatterns.getPatterns());
   }

   public ListContainerOwner<String> getPatternContainer() {
      return this.patternContainer;
   }
}
