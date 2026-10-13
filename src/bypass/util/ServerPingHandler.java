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

   public ServerPingHandler(ServerPinger var1, ServerPingData var2, NetworkManager var3, String var4) {
      this.Q = var1;
      this.U = var2;
      this.x = var3;
      this.u = var4;
      this.z = false;
   }

   @Override
   public void handleServerInfo(S00PacketServerInfo var1) {
      ServerStatusResponse var5 = var1.getResponse();
      if (var5.getPlayerCountData() != null) {
         this.U.N = String.valueOf(var5.getPlayerCountData().getOnlinePlayerCount());
         if (ArrayUtils.isNotEmpty(var5.getPlayerCountData().getPlayers())) {
            StringBuilder var6 = new StringBuilder();

            for (GameProfile var10 : var5.getPlayerCountData().getPlayers()) {
               if (var6.length() > 0) {
                  var6.append("\n");
               }

               var6.append(var10.getName());
            }

            if (var5.getPlayerCountData().getPlayers().length < var5.getPlayerCountData().getOnlinePlayerCount()) {
               if (var6.length() > 0) {
                  var6.append("\n");
               }

               var6.append("... and ")
                  .append(var5.getPlayerCountData().getOnlinePlayerCount() - var5.getPlayerCountData().getPlayers().length)
                  .append(" more ...");
            }

            this.U.u = var6.toString();
         }
      } else {
         this.U.u = "";
      }

      this.x.sendPacket(new C01PacketPing(Realms.currentTimeMillis()));
      this.z = true;
   }

   @Override
   public void handlePong(S01PacketPong var1) {
      this.x.closeChannel(new ChatComponentText("Finished"));
   }

   @Override
   public void onDisconnect(IChatComponent var1) {
      if (!this.z) {
         ServerPinger.P().error("Can't ping " + this.u + ": " + var1.getUnformattedText());
      }
   }
}
