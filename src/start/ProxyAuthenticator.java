package start;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class ProxyAuthenticator extends Authenticator {
   public final String l;
   public final String F;
   private static final long a = 18758532729662L;

   public ProxyAuthenticator(String string, String string2) {
      this.l = string;
      this.F = string2;
   }

   @Override
   public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.l, this.F.toCharArray());
   }
}
