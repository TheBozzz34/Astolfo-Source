package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class JvmArgumentsListOwner {
   private final ListContainerOwner<String> patternContainer;

   public JvmArgumentsListOwner() {
      JvmArgumentPatterns jvmArgumentPatterns = new JvmArgumentPatterns();
      this.patternContainer = new ListContainerOwner<>(jvmArgumentPatterns.getPatterns());
   }

   public ListContainerOwner<String> getPatternContainer() {
      return this.patternContainer;
   }
}
