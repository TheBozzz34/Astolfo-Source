package bypass.anticheat;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class SpoofGroundACheck extends MovementCheck {
   public SpoofGroundACheck() {
      super("Spoof Ground A");
   }

   @Override
   public int handleMovement(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      boolean bl2 = d2 % 0.0625 == 0.0 || d2 % 0.25 == 0.0 || d2 % 0.5 == 0.0;
      if (bl
         && !bl2
         && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(d, Math.floor(d2), d3)).getBlock() instanceof BlockAir
         && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(d, d2 - 1.0, d3)).getBlock() instanceof BlockAir) {
         this.flag(entityPlayer.getName());
      }

      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 15;
   }
}
