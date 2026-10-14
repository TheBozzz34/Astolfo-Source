package bypass.auth.handler;

import bypass.auth.DisconnectHandler;
import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Timer;
import org.apache.commons.lang3.StringUtils;

public class PingHandler extends PacketHandler<Integer> {
   public static Integer S;
   public static Integer k;
   private static final long b = 25317801065252L;

   public PingHandler() {
      super(PacketId.PING);
   }

   public void handle(Integer integer, PacketOutput packetOutput) {
      if (integer != -1) {
         k = S;
         S = integer;
         packetOutput.getSender().send((short)237, integer);
      } else {
         if (k != null) {
            packetOutput.getSender().send((short)237, k);
         }

         if (S != null) {
            packetOutput.getSender().send((short)237, S);
         }
      }

      if (DisconnectHandler.disconnected) {
         DisconnectHandler.disconnected = false;
         if (mc.thePlayer != null && mc.session != null && !StringUtils.isEmpty(ServerData.I)) {
            Minecraft.N(false);
         }
      }

      Timer.q.reset();
   }
}
