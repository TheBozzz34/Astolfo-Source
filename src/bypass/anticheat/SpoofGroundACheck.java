package bypass.anticheat;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class SpoofGroundACheck extends MovementCheck {
   private static final long b = 45380024613441L;
   private static final long e = 1185354816998604815L;

   public SpoofGroundACheck() {
      super("Spoof Ground A");
   }

   @Override
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      boolean var14 = var3 % 0.0625 == 0.0 || var3 % 0.25 == 0.0 || var3 % 0.5 == 0.0;
      if (var9
         && !var14
         && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(var1, Math.floor(var3), var5)).getBlock() instanceof BlockAir
         && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(var1, var3 - 1.0, var5)).getBlock() instanceof BlockAir) {
         this.x(var10.getName());
      }

      return this.T;
   }

   @Override
   public int p() {
      return (int)e;
   }
}
