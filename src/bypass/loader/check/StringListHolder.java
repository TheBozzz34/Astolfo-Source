package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class StringListHolder {
   private final ListContainerOwner<String> f;

   public StringListHolder(ListContainerOwner<String> listContainerOwner) {
      this.f = listContainerOwner;
   }

   public ListContainerOwner<String> A() {
      return this.f;
   }
}
