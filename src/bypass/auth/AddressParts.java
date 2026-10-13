package bypass.auth;

import bypass.util.container.ListContainerOwner;

public class AddressParts {
   private final ListContainerOwner<AddressPart> A;

   public AddressParts(ListContainerOwner<AddressPart> var1) {
      this.A = var1;
   }

   public ListContainerOwner<AddressPart> d() {
      return this.A;
   }
}
