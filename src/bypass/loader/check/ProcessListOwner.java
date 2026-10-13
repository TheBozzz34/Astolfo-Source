package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class ProcessListOwner {
   private final ListContainerOwner<String> O;

   public ProcessListOwner() {
      ProcessNames var1 = new ProcessNames();
      this.O = new ListContainerOwner<>(var1.A());
   }

   public ListContainerOwner<String> R() {
      return this.O;
   }
}
