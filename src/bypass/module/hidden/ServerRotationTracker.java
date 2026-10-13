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
   private Listener<MotionEvent> onMotion = var1 -> {
      if (var1.getState() == EventState.PRE) {
         this.f = this.G;
         this.p = var1.getYaw();
         this.G = var1.getPitch();
      }
   };
   @EventHandler(RotationYawEvent.class)
   private Listener<RotationYawEvent> onRotationYaw = var1 -> var1.setYaw(this.p);
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var1 -> {
      if (var1.getEntity() == mc.thePlayer) {
         var1.setPrevPitch(this.f);
         var1.setPitch(this.G);
         var1.cancel();
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.RECEIVED) {
         if (var0.getPacket() instanceof S03PacketTimeUpdate) {
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
