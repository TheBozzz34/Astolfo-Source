package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.util.RandomUtils;
import net.minecraft.network.play.client.C14PacketTabComplete;

public final class AntiTabComplete extends Module {
   private final String[] fakeCompletions = new String[]{"hey", "brb", "waddup", "hello", "hi"};
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C14PacketTabComplete) {
         C14PacketTabComplete c14PacketTabComplete = (C14PacketTabComplete)packetEvent.getPacket();
         if (c14PacketTabComplete.getMessage().startsWith(".")) {
            try {
               c14PacketTabComplete.x(c14PacketTabComplete.getMessage().substring(c14PacketTabComplete.getMessage().lastIndexOf(" ") + 1));
            } catch (Exception exception) {
               c14PacketTabComplete.x(this.fakeCompletions[RandomUtils.RANDOM.nextInt(this.fakeCompletions.length)] + " ");
            }
         }
      }
   };

   public AntiTabComplete(Category category) {
      super(category);
      this.setHidden(false);
   }
}
