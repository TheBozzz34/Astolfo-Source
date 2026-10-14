package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.BansPacket;
import bypass.auth.packet.BansPacketType;
import bypass.auth.packet.PacketId;
import bypass.command.BansCommand;
import net.minecraft.util.EnumChatFormatting;

public class BansHandler extends PacketHandler<BansPacket> {
   private static final long b = 83492121256424L;

   public BansHandler() {
      super(PacketId.BANS);
   }

   public void handle(BansPacket bansPacket, PacketOutput packetOutput) {
      if (bansPacket.type == BansPacketType.B) {
         try {
            int j = Integer.parseInt(bansPacket.bans);
            chat.D(false, false)
               .S()
               .i("[BANS] ")
               .M()
               .M(EnumChatFormatting.RED)
               .i(
                  Z(bansPacket.mode)
                     + " has "
                     + j
                     + (j <= 1 && j != 0 ? " ban" : " bans")
                     + " in the last "
                     + (BansCommand.p == 1 ? "hour" : BansCommand.p + " hours")
               )
               .N()
               .b();
         } catch (NumberFormatException numberFormatException) {
         }
      }
   }

   public static String Z(int i) {
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
