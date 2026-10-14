package bypass.auth;

import bypass.util.container.ListContainerOwner;

public class AddressParts {
   private final ListContainerOwner<AddressPart> A;

   public AddressParts(ListContainerOwner<AddressPart> listContainerOwner) {
      this.A = listContainerOwner;
   }

   public ListContainerOwner<AddressPart> d() {
      return this.A;
   }
}
