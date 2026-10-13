package bypass.module.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PendingBreak {
   public int B;
   public BlockPos f;
   public EnumFacing e;
   public boolean K;

   public PendingBreak(int var1, BlockPos var2, EnumFacing var3) {
      this.B = var1;
      this.f = var2;
      this.e = var3;
   }
}
