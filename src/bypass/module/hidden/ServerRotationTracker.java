package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.RotationYawEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.module.HiddenModule;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public final class ServerRotationTracker extends HiddenModule {
   private static long lastPacketTime;
   private static long lastTimeUpdateTime;
   private float serverYaw;
   private float serverPitch;
   private float prevServerPitch;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGHEST)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         this.prevServerPitch = this.serverPitch;
         this.serverYaw = motionEvent.getYaw();
         this.serverPitch = motionEvent.getPitch();
      }
   };
   @EventHandler(RotationYawEvent.class)
   private Listener<RotationYawEvent> onRotationYaw = rotationYawEvent -> rotationYawEvent.setYaw(this.serverYaw);
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (renderLivingEvent.getEntity() == mc.thePlayer) {
         renderLivingEvent.setPrevPitch(this.prevServerPitch);
         renderLivingEvent.setPitch(this.serverPitch);
         renderLivingEvent.cancel();
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S03PacketTimeUpdate) {
            lastTimeUpdateTime = System.currentTimeMillis();
         }

         lastPacketTime = System.currentTimeMillis();
      }
   };

   public ServerRotationTracker() {
      client.getEventBus().subscribe(this);
   }

   public static long getTimeSinceLastPacket() {
      return mc.getCurrentServerData() == null ? 0L : System.currentTimeMillis() - lastPacketTime;
   }

   public static long getTimeSinceTimeUpdate() {
      return mc.getCurrentServerData() == null ? 0L : System.currentTimeMillis() - lastTimeUpdateTime;
   }
}
