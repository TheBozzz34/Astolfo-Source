package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public abstract class MovementCheck extends AbstractCheck {
   public MovementCheck(String string) {
      super(string, CheckType.MOVEMENT);
   }

   public abstract int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer);
}
