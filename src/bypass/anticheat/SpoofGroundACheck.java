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
   public int handleMovement(double d, double e, double d2, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      boolean bl2 = e % 0.0625 == 0.0 || e % 0.25 == 0.0 || e % 0.5 == 0.0;
      if (bl
         && !bl2
         && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(d, Math.floor(e), d2)).getBlock() instanceof BlockAir
         && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(d, e - 1.0, d2)).getBlock() instanceof BlockAir) {
         this.flag(entityPlayer.getName());
      }

      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 15;
   }
}
