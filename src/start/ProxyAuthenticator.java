package start;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class ProxyAuthenticator extends Authenticator {
   public final String proxyUser;
   public final String proxyPassword;

   public ProxyAuthenticator(String string, String string2) {
      this.proxyUser = string;
      this.proxyPassword = string2;
   }

   @Override
   public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.proxyUser, this.proxyPassword.toCharArray());
   }
}
