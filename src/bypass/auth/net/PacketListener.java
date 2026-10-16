package bypass.auth.net;

import bypass.ClientAccess;
import bypass.auth.packet.LoginResponse;

public interface PacketListener<T> {
   void onPacket(T object);

   default void registerManagersOnLogin(T object) {
      if (object instanceof LoginResponse) {
         LoginResponse loginResponse = (LoginResponse)object;
         if (loginResponse.success) {
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getModuleManager());
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getCommandManager());
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getTransparentPanelManager());
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getScriptManager());
         }
      }
   }
}
