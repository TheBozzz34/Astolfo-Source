package bypass.event.player;

import bypass.ClientAccess;
import bypass.event.CancellableEvent;
import bypass.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class TeleportEvent extends CancellableEvent implements ClientAccess {
   private final S08PacketPlayerPosLook s08PacketPlayerPosLook;

   public TeleportEvent(S08PacketPlayerPosLook s08PacketPlayerPosLook) {
      this.s08PacketPlayerPosLook = s08PacketPlayerPosLook;
   }

   public boolean y() {
      return mc.thePlayer == null
         ? false
         : !this.V()
            && mc.thePlayer.getDistance(this.s08PacketPlayerPosLook.getX(), this.s08PacketPlayerPosLook.getY(), this.s08PacketPlayerPosLook.getZ()) < 50.0
            && this.s08PacketPlayerPosLook.getPitch() != 0.0F
            && this.s08PacketPlayerPosLook.getYaw() % 90.0F != 0.0F;
   }

   public boolean V() {
      return !mc.getNetHandler().doneLoadingTerrain;
   }

   public void i(Module module) {
      module.setEnabled(false);
      toasts.X("Server spawn", module.getName() + " was disabled");
   }

   public void b(Entity entity) {
      entity.setPositionAndRotation(
         this.s08PacketPlayerPosLook.getX(),
         this.s08PacketPlayerPosLook.getY(),
         this.s08PacketPlayerPosLook.getZ(),
         this.s08PacketPlayerPosLook.getYaw(),
         this.s08PacketPlayerPosLook.getPitch()
      );
   }
}
