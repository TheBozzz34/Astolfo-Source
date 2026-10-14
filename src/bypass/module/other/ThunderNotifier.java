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
               chat.D(false, false)
                  .i("[")
                  .M(EnumChatFormatting.DARK_GRAY)
                  .i("Power")
                  .M(EnumChatFormatting.RED)
                  .i("]")
                  .M(EnumChatFormatting.DARK_GRAY)
                  .i(" 距离 ")
                  .M(EnumChatFormatting.GREEN)
                  .i((int)d3 + "米")
                  .M(EnumChatFormatting.RED)
                  .i(" 检测到雷击! ")
                  .M(EnumChatFormatting.AQUA)
                  .i("坐标")
                  .M(EnumChatFormatting.GREEN)
                  .i("X: ")
                  .M(EnumChatFormatting.AQUA)
                  .i(String.valueOf((int)d))
                  .M(EnumChatFormatting.WHITE)
                  .i(" Y: ")
                  .M(EnumChatFormatting.AQUA)
                  .i(String.valueOf((int)e))
                  .M(EnumChatFormatting.WHITE)
                  .i(" Z: ")
                  .M(EnumChatFormatting.AQUA)
                  .i(String.valueOf((int)d2))
                  .M(EnumChatFormatting.WHITE)
                  .i(" [")
                  .M(EnumChatFormatting.BLUE)
                  .i("ClickHereTP")
                  .M()
                  .M(EnumChatFormatting.YELLOW)
                  .V(".tp " + d + " 128 " + d2)
                  .y(EnumChatFormatting.GREEN + "Click to TP~")
                  .i("]")
                  .M(EnumChatFormatting.BLUE)
                  .N()
                  .b();
            } else {
               chat.D(false, false)
                  .i("> ")
                  .M(EnumChatFormatting.GRAY)
                  .i(String.format("Detected thunder at %s %s %s (%s blocks away)", (int)d, (int)e, (int)d2, (int)d3))
                  .M(EnumChatFormatting.WHITE)
                  .V(".tp " + d + " 128 " + d2)
                  .y("Click for TP")
                  .N()
                  .b();
            }
         }
      }
   };
   private static final long c = 67288966726825L;

   public ThunderNotifier(Category category) {
      super(category);
      this.setHidden(false);
   }
}
