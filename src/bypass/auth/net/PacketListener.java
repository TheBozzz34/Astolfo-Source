package bypass.auth.net;

import bypass.ClientAccess;
import bypass.auth.packet.LoginResponse;

public interface PacketListener<T> {
   long a = 13007465144963L;

   void onPacket(T object);

   default void E(T object) {
      long l = a ^ 114177592814000L;
      long m = l ^ 138837328836305L;
      long n = l ^ 100584918349440L;
      long l2 = l ^ 3171863292505L;
      long l3 = l ^ 60182057851445L;
      long l4 = l ^ 30960231397488L;
      if (object instanceof LoginResponse) {
         LoginResponse loginResponse = (LoginResponse)object;
         if (loginResponse.success) {
            ClientAccess.client.register(l2, ClientAccess.client.getModuleManager());
            ClientAccess.client.register(l2, ClientAccess.client.getCommandManager());
            ClientAccess.client.register(l2, ClientAccess.client.getTransparentPanelManager());
            ClientAccess.client.register(l2, ClientAccess.client.getScriptManager());
         }
      }
   }
}
