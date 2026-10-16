package bypass.auth;

import bypass.util.container.ListContainerOwner;
import java.util.Arrays;

public class AuthServerAddress {
   private final AddressParts parts = new AddressParts(
      new ListContainerOwner<>(Arrays.asList(new AddressPart1(), new AddressPart2(), new AddressPart3(), new AddressPart4()))
   );

   public String getHost() {
      StringBuilder stringBuilder = new StringBuilder();

      for (AddressPart addressPart : this.parts.getParts().getOwnedContainer().getValue()) {
         stringBuilder.append(addressPart.getPart());
      }

      return stringBuilder.toString();
   }

   public AddressParts getParts() {
      return this.parts;
   }
}
