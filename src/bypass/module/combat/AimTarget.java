package bypass.module.combat;

import bypass.util.Rotation;
import net.minecraft.entity.player.EntityPlayer;

public class AimTarget {
   private final EntityPlayer y;
   private final Rotation U;
   private final float i;

   public AimTarget(EntityPlayer entityPlayer, Rotation rotation, float f) {
      this.y = entityPlayer;
      this.U = rotation;
      this.i = f;
   }

   public Rotation d() {
      return this.U;
   }

   public EntityPlayer S() {
      return this.y;
   }

   public float Z() {
      return this.i;
   }

   public static float R(AimTarget aimTarget) {
      return aimTarget.i;
   }

   public static Rotation h(AimTarget aimTarget) {
      return aimTarget.U;
   }
}
