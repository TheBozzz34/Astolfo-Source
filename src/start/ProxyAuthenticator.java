package start;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class ProxyAuthenticator extends Authenticator {
   public final String l;
   public final String F;
   private static final long a = 18758532729662L;

   public ProxyAuthenticator(String var1, String var2) {
      this.l = var1;
      this.F = var2;
   }

   @Override
   public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.l, this.F.toCharArray());
   }
}
