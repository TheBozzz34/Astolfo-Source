package bypass.module.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PendingBreak {
   public int ticksLeft;
   public BlockPos pos;
   public EnumFacing facing;
   public boolean unusedFlag;

   public PendingBreak(int i, BlockPos blockPos, EnumFacing enumFacing) {
      this.ticksLeft = i;
      this.pos = blockPos;
      this.facing = enumFacing;
   }
}
