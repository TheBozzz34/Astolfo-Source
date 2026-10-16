package bypass.auth;

import bypass.util.container.ListContainerOwner;

public class AddressParts {
   private final ListContainerOwner<AddressPart> parts;

   public AddressParts(ListContainerOwner<AddressPart> listContainerOwner) {
      this.parts = listContainerOwner;
   }

   public ListContainerOwner<AddressPart> getParts() {
      return this.parts;
   }
}
