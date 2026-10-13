package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public abstract class MovementCheck extends AbstractCheck {
   public MovementCheck(String var1) {
      super(var1, CheckType.MOVEMENT);
   }

   public abstract int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10);
}
