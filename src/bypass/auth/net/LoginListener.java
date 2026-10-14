package bypass.auth.net;

import bypass.ClientAccess;
import bypass.auth.packet.LoginResponse;

public interface LoginListener {
   default void o(Object object) {
      if (object instanceof LoginResponse) {
         LoginResponse loginResponse = (LoginResponse)object;
         if (loginResponse.success) {
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getSidebarGui());
         }
      }
   }
}
