package bypass.auth.handler;

import bypass.auth.DisconnectHandler;
import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Timer;
import org.apache.commons.lang3.StringUtils;

public class PingHandler extends PacketHandler<Integer> {
   public static Integer lastPingId;
   public static Integer previousPingId;

   public PingHandler() {
      super(PacketId.PING);
   }

   public void handle(Integer integer, PacketOutput packetOutput) {
      if (integer != -1) {
         previousPingId = lastPingId;
         lastPingId = integer;
         packetOutput.getSender().send((short)237, integer);
      } else {
         if (previousPingId != null) {
            packetOutput.getSender().send((short)237, previousPingId);
         }

         if (lastPingId != null) {
            packetOutput.getSender().send((short)237, lastPingId);
         }
      }

      if (DisconnectHandler.disconnected) {
         DisconnectHandler.disconnected = false;
         if (mc.thePlayer != null && mc.session != null && !StringUtils.isEmpty(ServerData.currentServerIp)) {
            Minecraft.reportServerChange(false);
         }
      }

      Timer.authPingTimer.reset();
   }
}
