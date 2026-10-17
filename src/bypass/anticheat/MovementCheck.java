package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public abstract class MovementCheck extends AbstractCheck {
   public MovementCheck(String string) {
      super(string, CheckType.MOVEMENT);
   }

   public abstract int handleMovement(double d, double e, double d2, float f, float g, boolean bl, EntityPlayer entityPlayer);
}
