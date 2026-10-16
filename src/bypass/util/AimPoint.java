package bypass.util;

import net.minecraft.util.Vec3;

public class AimPoint {
   private Vec3 point;
   private AimRotation rotation;

   public AimPoint(Vec3 vec3, AimRotation aimRotation) {
      this.point = vec3;
      this.rotation = aimRotation;
   }

   public AimRotation getRotation() {
      return this.rotation;
   }

   public Vec3 getPoint() {
      return this.point;
   }
}
