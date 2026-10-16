package bypass.anticheat;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class SpoofGroundBCheck extends MovementCheck {
   private double lastGroundY;

   public SpoofGroundBCheck() {
      super("Spoof Ground B");
   }

   @Override
   public int handleMovement(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      boolean bl2 = d2 % 0.0625 == 0.0 || d2 % 0.25 == 0.0 || d2 % 0.5 == 0.0;
      if (bl && bl2) {
         this.D_void();
         this.lastGroundY = d2;
      } else if (this.lastGroundY != 0.0) {
         double e = d2 - this.lastGroundY;
         if (e == 0.0
            && !(
               Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY - 0.05, entityPlayer.posZ)) instanceof BlockAir
            )) {
            this.flag(entityPlayer.getName());
         }
      }

      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 25;
   }
}
