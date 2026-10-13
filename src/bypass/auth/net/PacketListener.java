package bypass.auth.net;

import bypass.ClientAccess;
import bypass.auth.packet.LoginResponse;

public interface PacketListener<T> {
   long a = 13007465144963L;

   void onPacket(T var1);

   default void E(T var1) {
      long var2 = a ^ 114177592814000L;
      long var4 = var2 ^ 138837328836305L;
      long var6 = var2 ^ 100584918349440L;
      long var8 = var2 ^ 3171863292505L;
      long var10 = var2 ^ 60182057851445L;
      long var12 = var2 ^ 30960231397488L;
      if (var1 instanceof LoginResponse) {
         LoginResponse var15 = (LoginResponse)var1;
         if (var15.success) {
            ClientAccess.client.register(var8, ClientAccess.client.getModuleManager());
            ClientAccess.client.register(var8, ClientAccess.client.getCommandManager());
            ClientAccess.client.register(var8, ClientAccess.client.getTransparentPanelManager());
            ClientAccess.client.register(var8, ClientAccess.client.getScriptManager());
         }
      }
   }
}
