package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class StringListHolder {
   private final ListContainerOwner<String> stringContainer;

   public StringListHolder(ListContainerOwner<String> listContainerOwner) {
      this.stringContainer = listContainerOwner;
   }

   public ListContainerOwner<String> getStringContainer() {
      return this.stringContainer;
   }
}
