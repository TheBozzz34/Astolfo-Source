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
   private static long L;
   private static long H;
   private float p;
   private float G;
   private float f;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGHEST)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         this.f = this.G;
         this.p = motionEvent.getYaw();
         this.G = motionEvent.getPitch();
      }
   };
   @EventHandler(RotationYawEvent.class)
   private Listener<RotationYawEvent> onRotationYaw = rotationYawEvent -> rotationYawEvent.setYaw(this.p);
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (renderLivingEvent.getEntity() == mc.thePlayer) {
         renderLivingEvent.setPrevPitch(this.f);
         renderLivingEvent.setPitch(this.G);
         renderLivingEvent.cancel();
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S03PacketTimeUpdate) {
            H = System.currentTimeMillis();
         }

         L = System.currentTimeMillis();
      }
   };
   private static final long c = 68231009307398L;

   public ServerRotationTracker() {
      client.getEventBus().subscribe(this);
   }

   public static long U() {
      return mc.getCurrentServerData() == null ? 0L : System.currentTimeMillis() - L;
   }

   public static long g() {
      return mc.getCurrentServerData() == null ? 0L : System.currentTimeMillis() - H;
   }
}
