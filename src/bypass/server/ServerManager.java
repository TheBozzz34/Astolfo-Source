package bypass.server;

import bypass.Toasts;
import bypass.manager.Manager;
import bypass.via.ViaFabric;

public class ServerManager extends Manager<Server> {
   public static ServerType currentServerType;
   private boolean protocolWarningShown;

   public ServerManager() {
      this.add(new HypixelServer(), new CubecraftServer(), new CubecraftGamesServer());
   }

   public void updateCurrentServerType() {
      for (Server server : this.items) {
         if (server.isCurrentServer()) {
            if (server.getType() == ServerType.HYPIXEL && !this.protocolWarningShown && !ViaFabric.INSTANCE.isNative()) {
               Toasts.TOASTS.warningTimed("Important", "Using protocols that arent 1.8.x is currently not recommended for blatant cheating in Hypixel", 15000);
               this.protocolWarningShown = true;
            }

            currentServerType = server.getType();
            return;
         }
      }

      currentServerType = ServerType.UNKNOWN;
   }

   public static void setCurrentServerType(ServerType serverType) {
      currentServerType = serverType;
   }
}
