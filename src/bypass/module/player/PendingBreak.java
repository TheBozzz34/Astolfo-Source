package bypass.module.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PendingBreak {
   public int B;
   public BlockPos f;
   public EnumFacing e;
   public boolean K;

   public PendingBreak(int i, BlockPos blockPos, EnumFacing enumFacing) {
      this.B = i;
      this.f = blockPos;
      this.e = enumFacing;
   }
}
