package bypass.anticheat;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class SpoofGroundBCheck extends MovementCheck {
   private double o;
   private static final long b = 128025344506953L;
   private static final long e = -7154551926061793255L;

   public SpoofGroundBCheck() {
      super("Spoof Ground B");
   }

   @Override
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      boolean var14 = var3 % 0.0625 == 0.0 || var3 % 0.25 == 0.0 || var3 % 0.5 == 0.0;
      if (var9 && var14) {
         this.D_void();
         this.o = var3;
      } else if (this.o != 0.0) {
         double var15 = var3 - this.o;
         if (var15 == 0.0 && !(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(var10.posX, var10.posY - 0.05, var10.posZ)) instanceof BlockAir)) {
            this.x(var10.getName());
         }
      }

      return this.T;
   }

   @Override
   public int p() {
      return (int)e;
   }
}
