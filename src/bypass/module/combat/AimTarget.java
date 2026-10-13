package bypass.module.combat;

import bypass.util.Rotation;
import net.minecraft.entity.player.EntityPlayer;

public class AimTarget {
   private final EntityPlayer y;
   private final Rotation U;
   private final float i;

   public AimTarget(EntityPlayer var1, Rotation var2, float var3) {
      this.y = var1;
      this.U = var2;
      this.i = var3;
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

   public static float R(AimTarget var0) {
      return var0.i;
   }

   public static Rotation h(AimTarget var0) {
      return var0.U;
   }
}
