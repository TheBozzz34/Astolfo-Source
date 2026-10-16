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
   private Stopwatch messageTimer = new Stopwatch();
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S2DPacketOpenWindow
            && ((S2DPacketOpenWindow)packetEvent.getPacket()).getWindowTitle().getUnformattedText().contains("You are frozen")) {
            packetEvent.cancel();
         }

         if (this.spawn.getValue() && packetEvent.getPacket() instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("is currently frozen") && this.messageTimer.hasElapsed(1500.0F)) {
               mc.thePlayer.setPosition(mc.thePlayer.posX, -999.0, mc.thePlayer.posY);
               this.messageTimer.reset();
            }
         }
      }
   };

   public AntiFrozen(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.spawn);
   }
}
