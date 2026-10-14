package bypass.auth.net;

import bypass.ClientAccess;
import bypass.auth.packet.LoginResponse;

public interface LoginListener {
   long b = 18306945468379L;

   default void o(Object object) {
      long l = b ^ 67237695386132L;
      long m = l ^ 55690665576259L;
      long n = l ^ 74142247015589L;
      if (object instanceof LoginResponse) {
         LoginResponse loginResponse = (LoginResponse)object;
         if (loginResponse.success) {
            ClientAccess.client.register(n, ClientAccess.client.getSidebarGui());
         }
      }
   }
}
