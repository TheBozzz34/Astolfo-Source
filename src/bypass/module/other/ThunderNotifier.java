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
   private final Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.RECEIVED && var0.getPacket() instanceof S2CPacketSpawnGlobalEntity) {
         S2CPacketSpawnGlobalEntity var4 = (S2CPacketSpawnGlobalEntity)var0.getPacket();
         if (var4.func_149053_g() == 1) {
            double var5 = var4.func_149051_d() / 32.0;
            double var7 = var4.func_149050_e() / 32.0;
            double var9 = var4.func_149049_f() / 32.0;
            double var11 = mc.thePlayer.getDistance(var5, var7, var9);
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
                  .i((int)var11 + "米")
                  .M(EnumChatFormatting.RED)
                  .i(" 检测到雷击! ")
                  .M(EnumChatFormatting.AQUA)
                  .i("坐标")
                  .M(EnumChatFormatting.GREEN)
                  .i("X: ")
                  .M(EnumChatFormatting.AQUA)
                  .i(String.valueOf((int)var5))
                  .M(EnumChatFormatting.WHITE)
                  .i(" Y: ")
                  .M(EnumChatFormatting.AQUA)
                  .i(String.valueOf((int)var7))
                  .M(EnumChatFormatting.WHITE)
                  .i(" Z: ")
                  .M(EnumChatFormatting.AQUA)
                  .i(String.valueOf((int)var9))
                  .M(EnumChatFormatting.WHITE)
                  .i(" [")
                  .M(EnumChatFormatting.BLUE)
                  .i("ClickHereTP")
                  .M()
                  .M(EnumChatFormatting.YELLOW)
                  .V(".tp " + var5 + " 128 " + var9)
                  .y(EnumChatFormatting.GREEN + "Click to TP~")
                  .i("]")
                  .M(EnumChatFormatting.BLUE)
                  .N()
                  .b();
            } else {
               chat.D(false, false)
                  .i("> ")
                  .M(EnumChatFormatting.GRAY)
                  .i(String.format("Detected thunder at %s %s %s (%s blocks away)", (int)var5, (int)var7, (int)var9, (int)var11))
                  .M(EnumChatFormatting.WHITE)
                  .V(".tp " + var5 + " 128 " + var9)
                  .y("Click for TP")
                  .N()
                  .b();
            }
         }
      }
   };
   private static final long c = 67288966726825L;

   public ThunderNotifier(Category var1) {
      super(var1);
      this.setHidden(false);
   }
}
