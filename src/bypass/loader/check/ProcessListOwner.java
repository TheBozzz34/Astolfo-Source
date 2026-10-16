package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class ProcessListOwner {
   private final ListContainerOwner<String> processContainer;

   public ProcessListOwner() {
      ProcessNames processNames = new ProcessNames();
      this.processContainer = new ListContainerOwner<>(processNames.getProcessNames());
   }

   public ListContainerOwner<String> getProcessContainer() {
      return this.processContainer;
   }
}
