package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public final class WorldTime extends Module {
   private final EnumSetting<WorldTimeMode> mode = new EnumSetting<>("Mode", "Set time", WorldTimeMode.SUNRISE);
   private long serverWorldTime = -1L;
   private long loopTime;
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         mc.theWorld.setWorldTime(this.mode.getValue() == WorldTimeMode.LOOP ? this.loopTime : WorldTimeMode.getTime(this.mode.getValue()));
         if (this.loopTime++ >= 23999L) {
            this.loopTime = 0L;
         }
      }
   };
   @EventHandler(value = PacketEvent.class, priority = EventPriority.LOWEST)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED && packetEvent.getPacket() instanceof S03PacketTimeUpdate) {
         this.serverWorldTime = ((S03PacketTimeUpdate)packetEvent.getPacket()).getWorldTime();
         packetEvent.cancel();
      }
   };

   public WorldTime(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.mode);
   }

   @Override
   public void onDisable() {
      if (mc.theWorld != null && this.serverWorldTime != -1L) {
         mc.theWorld.setWorldTime(this.serverWorldTime);
      }

      this.loopTime = 0L;
      super.onDisable();
   }
}
