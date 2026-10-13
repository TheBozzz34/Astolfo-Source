package bypass.event.player;

import bypass.event.CancellableEvent;
import net.minecraft.util.Vec3;

public class LookEvent extends CancellableEvent {
   private Vec3 vec;

   public LookEvent(Vec3 var1) {
      this.vec = var1;
   }

   public Vec3 getVec() {
      return this.vec;
   }

   public void setVec(Vec3 var1) {
      this.vec = var1;
   }
}
