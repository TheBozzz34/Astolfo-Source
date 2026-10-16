package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.visuals.HUD;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.EnumChatFormatting;

public final class ThunderNotifier extends Module {
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED && packetEvent.getPacket() instanceof S2CPacketSpawnGlobalEntity) {
         S2CPacketSpawnGlobalEntity s2CPacketSpawnGlobalEntity = (S2CPacketSpawnGlobalEntity)packetEvent.getPacket();
         if (s2CPacketSpawnGlobalEntity.func_149053_g() == 1) {
            double d = s2CPacketSpawnGlobalEntity.func_149051_d() / 32.0;
            double e = s2CPacketSpawnGlobalEntity.func_149050_e() / 32.0;
            double d2 = s2CPacketSpawnGlobalEntity.func_149049_f() / 32.0;
            double d3 = mc.thePlayer.getDistance(d, e, d2);
            if (HUD.isPowerBranding()) {
               chat.createBuilder(false, false)
                  .append("[")
                  .color(EnumChatFormatting.DARK_GRAY)
                  .append("Power")
                  .color(EnumChatFormatting.RED)
                  .append("]")
                  .color(EnumChatFormatting.DARK_GRAY)
                  .append(" 距离 ")
                  .color(EnumChatFormatting.GREEN)
                  .append((int)d3 + "米")
                  .color(EnumChatFormatting.RED)
                  .append(" 检测到雷击! ")
                  .color(EnumChatFormatting.AQUA)
                  .append("坐标")
                  .color(EnumChatFormatting.GREEN)
                  .append("X: ")
                  .color(EnumChatFormatting.AQUA)
                  .append(String.valueOf((int)d))
                  .color(EnumChatFormatting.WHITE)
                  .append(" Y: ")
                  .color(EnumChatFormatting.AQUA)
                  .append(String.valueOf((int)e))
                  .color(EnumChatFormatting.WHITE)
                  .append(" Z: ")
                  .color(EnumChatFormatting.AQUA)
                  .append(String.valueOf((int)d2))
                  .color(EnumChatFormatting.WHITE)
                  .append(" [")
                  .color(EnumChatFormatting.BLUE)
                  .append("ClickHereTP")
                  .bold()
                  .color(EnumChatFormatting.YELLOW)
                  .runCommandOnClick(".tp " + d + " 128 " + d2)
                  .showTextOnHover(EnumChatFormatting.GREEN + "Click to TP~")
                  .append("]")
                  .color(EnumChatFormatting.BLUE)
                  .build()
                  .send();
            } else {
               chat.createBuilder(false, false)
                  .append("> ")
                  .color(EnumChatFormatting.GRAY)
                  .append(String.format("Detected thunder at %s %s %s (%s blocks away)", (int)d, (int)e, (int)d2, (int)d3))
                  .color(EnumChatFormatting.WHITE)
                  .runCommandOnClick(".tp " + d + " 128 " + d2)
                  .showTextOnHover("Click for TP")
                  .build()
                  .send();
            }
         }
      }
   };

   public ThunderNotifier(Category category) {
      super(category);
      this.setHidden(false);
   }
}
