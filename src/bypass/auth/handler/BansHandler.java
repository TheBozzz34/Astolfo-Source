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

   public void handle(BansPacket var1, PacketOutput var2) {
      if (var1.type == BansPacketType.B) {
         try {
            int var6 = Integer.parseInt(var1.bans);
            chat.D(false, false)
               .S()
               .i("[BANS] ")
               .M()
               .M(EnumChatFormatting.RED)
               .i(
                  Z(var1.mode)
                     + " has "
                     + var6
                     + (var6 <= 1 && var6 != 0 ? " ban" : " bans")
                     + " in the last "
                     + (BansCommand.p == 1 ? "hour" : BansCommand.p + " hours")
               )
               .N()
               .b();
         } catch (NumberFormatException var7) {
         }
      }
   }

   public static String Z(int var0) {
      switch (var0) {
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
