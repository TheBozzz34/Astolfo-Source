package bypass.auth;

import bypass.util.container.ListContainerOwner;
import java.util.Arrays;

public class AuthServerAddress {
   private final AddressParts parts = new AddressParts(
      new ListContainerOwner<>(Arrays.asList(new AddressPart1(), new AddressPart2(), new AddressPart3(), new AddressPart4()))
   );
   private static final long a = 46431573615470L;

   public String getHost() {
      StringBuilder var4 = new StringBuilder();

      for (AddressPart var6 : this.parts.d().L().getValue()) {
         var4.append(var6.getPart());
      }

      return var4.toString();
   }

   public AddressParts getParts() {
      return this.parts;
   }
}
