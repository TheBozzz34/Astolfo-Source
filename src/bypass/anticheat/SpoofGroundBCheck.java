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
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      boolean bl2 = d2 % 0.0625 == 0.0 || d2 % 0.25 == 0.0 || d2 % 0.5 == 0.0;
      if (bl && bl2) {
         this.D_void();
         this.o = d2;
      } else if (this.o != 0.0) {
         double e = d2 - this.o;
         if (e == 0.0
            && !(
               Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY - 0.05, entityPlayer.posZ)) instanceof BlockAir
            )) {
            this.x(entityPlayer.getName());
         }
      }

      return this.T;
   }

   @Override
   public int p() {
      return (int)e;
   }
}
