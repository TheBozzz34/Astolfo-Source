package bypass.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.realms.Realms;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ServerPingHandler implements INetHandlerStatusClient {
   private boolean z;
   public final ServerPingData U;
   public final NetworkManager x;
   public final String u;
   public final ServerPinger Q;
   private static final long a = 54730631041341L;

   public ServerPingHandler(ServerPinger serverPinger, ServerPingData serverPingData, NetworkManager networkManager, String string) {
      this.Q = serverPinger;
      this.U = serverPingData;
      this.x = networkManager;
      this.u = string;
      this.z = false;
   }

   @Override
   public void handleServerInfo(S00PacketServerInfo packetIn) {
      ServerStatusResponse serverStatusResponse = packetIn.getResponse();
      if (serverStatusResponse.getPlayerCountData() != null) {
         this.U.N = String.valueOf(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount());
         if (ArrayUtils.isNotEmpty(serverStatusResponse.getPlayerCountData().getPlayers())) {
            StringBuilder stringBuilder = new StringBuilder();

            for (GameProfile gameProfile : serverStatusResponse.getPlayerCountData().getPlayers()) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append("\n");
               }

               stringBuilder.append(gameProfile.getName());
            }

            if (serverStatusResponse.getPlayerCountData().getPlayers().length < serverStatusResponse.getPlayerCountData().getOnlinePlayerCount()) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append("\n");
               }

               stringBuilder.append("... and ")
                  .append(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount() - serverStatusResponse.getPlayerCountData().getPlayers().length)
                  .append(" more ...");
            }

            this.U.u = stringBuilder.toString();
         }
      } else {
         this.U.u = "";
      }

      this.x.sendPacket(new C01PacketPing(Realms.currentTimeMillis()));
      this.z = true;
   }

   @Override
   public void handlePong(S01PacketPong packetIn) {
      this.x.closeChannel(new ChatComponentText("Finished"));
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      if (!this.z) {
         ServerPinger.P().error("Can't ping " + this.u + ": " + reason.getUnformattedText());
      }
   }
}
