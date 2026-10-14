package bypass.util;

import net.minecraft.util.Vec3;

public class AimPoint {
   private Vec3 y;
   private AimRotation f;

   public AimPoint(Vec3 vec3, AimRotation aimRotation) {
      this.y = vec3;
      this.f = aimRotation;
   }

   public AimRotation r() {
      return this.f;
   }

   public Vec3 W() {
      return this.y;
   }
}
