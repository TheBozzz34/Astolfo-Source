package bypass.module.combat;

import bypass.util.Rotation;
import net.minecraft.entity.player.EntityPlayer;

public class AimTarget {
   private final EntityPlayer player;
   private final Rotation rotation;
   private final float angleDistance;

   public AimTarget(EntityPlayer entityPlayer, Rotation rotation, float f) {
      this.player = entityPlayer;
      this.rotation = rotation;
      this.angleDistance = f;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public float getAngleDistance() {
      return this.angleDistance;
   }

   public static float angleDistanceOf(AimTarget aimTarget) {
      return aimTarget.angleDistance;
   }

   public static Rotation rotationOf(AimTarget aimTarget) {
      return aimTarget.rotation;
   }
}
