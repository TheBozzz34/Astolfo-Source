package bypass.server;

import bypass.Toasts;
import bypass.manager.Manager;
import bypass.via.ViaFabric;

public class ServerManager extends Manager<Server> {
   public static ServerType X;
   private boolean k;
   private static final long a = 120084956906074L;
   private static final long e = -1313696488571782504L;

   public ServerManager() {
      this.add(new HypixelServer(), new CubecraftServer(), new CubecraftGamesServer());
   }

   public void x() {
      for (Server server : this.C) {
         if (server.g()) {
            if (server.y() == ServerType.HYPIXEL && !this.k && !ViaFabric.INSTANCE.isNative()) {
               Toasts.TOASTS.A("Important", "Using protocols that arent 1.8.x is currently not recommended for blatant cheating in Hypixel", (int)e);
               this.k = true;
            }

            X = server.y();
            return;
         }
      }

      X = ServerType.UNKNOWN;
   }

   public static void c(ServerType serverType) {
      X = serverType;
   }
}
