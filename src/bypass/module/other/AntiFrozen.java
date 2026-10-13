package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.util.Stopwatch;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public final class AntiFrozen extends Module {
   private final BooleanSetting spawn = new BooleanSetting("Spawn", "TPs you to spawn when you hit a frozen player", true);
   private Stopwatch v = new Stopwatch();
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.RECEIVED) {
         if (var1x.getPacket() instanceof S2DPacketOpenWindow
            && ((S2DPacketOpenWindow)var1x.getPacket()).getWindowTitle().getUnformattedText().contains("You are frozen")) {
            var1x.cancel();
         }

         if (this.spawn.getValue() && var1x.getPacket() instanceof S02PacketChat) {
            S02PacketChat var5 = (S02PacketChat)var1x.getPacket();
            if (var5.getChatComponent().getUnformattedText().contains("is currently frozen") && this.v.hasElapsed(1500.0F)) {
               mc.thePlayer.setPosition(mc.thePlayer.posX, -999.0, mc.thePlayer.posY);
               this.v.reset();
            }
         }
      }
   };
   private static final long c = 2477974446304L;

   public AntiFrozen(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.spawn);
   }
}
