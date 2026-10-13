package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class StringListHolder {
   private final ListContainerOwner<String> f;

   public StringListHolder(ListContainerOwner<String> var1) {
      this.f = var1;
   }

   public ListContainerOwner<String> A() {
      return this.f;
   }
}
