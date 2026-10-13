package bypass.module.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class ScaffoldTarget {
   public BlockPos pos;
   public EnumFacing facing;
   public Vec3 hitVec;
   public int unused;

   public ScaffoldTarget(BlockPos var1, EnumFacing var2) {
      this.pos = var1;
      this.facing = var2;
   }
}
