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
   private final String[] C = new String[]{"hey", "brb", "waddup", "hello", "hi"};
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C14PacketTabComplete) {
         C14PacketTabComplete var5 = (C14PacketTabComplete)var1x.getPacket();
         if (var5.getMessage().startsWith(".")) {
            try {
               var5.x(var5.getMessage().substring(var5.getMessage().lastIndexOf(" ") + 1));
            } catch (Exception var7) {
               var5.x(this.C[RandomUtils.RANDOM.nextInt(this.C.length)] + " ");
            }
         }
      }
   };
   private static final long c = 90130258760057L;

   public AntiTabComplete(Category var1) {
      super(var1);
      this.setHidden(false);
   }
}
