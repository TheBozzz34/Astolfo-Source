package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class ProcessListOwner {
   private final ListContainerOwner<String> O;

   public ProcessListOwner() {
      ProcessNames processNames = new ProcessNames();
      this.O = new ListContainerOwner<>(processNames.A());
   }

   public ListContainerOwner<String> R() {
      return this.O;
   }
}
