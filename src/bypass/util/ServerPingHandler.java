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
   private boolean receivedInfo;
   public final ServerPingData pingData;
   public final NetworkManager networkManager;
   public final String serverAddress;
   public final ServerPinger pinger;

   public ServerPingHandler(ServerPinger serverPinger, ServerPingData serverPingData, NetworkManager networkManager, String string) {
      this.pinger = serverPinger;
      this.pingData = serverPingData;
      this.networkManager = networkManager;
      this.serverAddress = string;
      this.receivedInfo = false;
   }

   @Override
   public void handleServerInfo(S00PacketServerInfo packetIn) {
      ServerStatusResponse serverStatusResponse = packetIn.getResponse();
      if (serverStatusResponse.getPlayerCountData() != null) {
         this.pingData.playerCount = String.valueOf(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount());
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

            this.pingData.playerList = stringBuilder.toString();
         }
      } else {
         this.pingData.playerList = "";
      }

      this.networkManager.sendPacket(new C01PacketPing(Realms.currentTimeMillis()));
      this.receivedInfo = true;
   }

   @Override
   public void handlePong(S01PacketPong packetIn) {
      this.networkManager.closeChannel(new ChatComponentText("Finished"));
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      if (!this.receivedInfo) {
         ServerPinger.getLogger().error("Can't ping " + this.serverAddress + ": " + reason.getUnformattedText());
      }
   }
}
