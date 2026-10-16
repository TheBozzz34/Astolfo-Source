package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.BansPacket;
import bypass.auth.packet.BansPacketType;
import bypass.auth.packet.PacketId;
import bypass.command.BansCommand;
import net.minecraft.util.EnumChatFormatting;

public class BansHandler extends PacketHandler<BansPacket> {
   public BansHandler() {
      super(PacketId.BANS);
   }

   public void handle(BansPacket bansPacket, PacketOutput packetOutput) {
      if (bansPacket.type == BansPacketType.B) {
         try {
            int i = Integer.parseInt(bansPacket.bans);
            chat.createBuilder(false, false)
               .enableLinkDetection()
               .append("[BANS] ")
               .bold()
               .color(EnumChatFormatting.RED)
               .append(
                  getModeName(bansPacket.mode)
                     + " has "
                     + i
                     + (i <= 1 && i != 0 ? " ban" : " bans")
                     + " in the last "
                     + (BansCommand.hours == 1 ? "hour" : BansCommand.hours + " hours")
               )
               .build()
               .send();
         } catch (NumberFormatException numberFormatException) {
         }
      }
   }

   public static String getModeName(int i) {
      switch (i) {
         case 1:
            return "Skywars";
         case 2:
            return "Bedwars";
         case 3:
            return "UHC";
         case 4:
            return "MegaWalls";
         case 5:
            return "Pit";
         default:
            return "Hypixel";
      }
   }
}
