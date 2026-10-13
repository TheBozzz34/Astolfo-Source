package bypass.util;

import net.minecraft.util.Vec3;

public class AimPoint {
   private Vec3 y;
   private AimRotation f;

   public AimPoint(Vec3 var1, AimRotation var2) {
      this.y = var1;
      this.f = var2;
   }

   public AimRotation r() {
      return this.f;
   }

   public Vec3 W() {
      return this.y;
   }
}
