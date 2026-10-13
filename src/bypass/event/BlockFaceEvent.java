package bypass.event;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockFaceEvent extends CancellableEvent {
   private Block block;
   private EnumFacing facing;
   private double U;
   private double e;
   private double A;
   private double n;
   private double q;
   private double O;
   private BlockPos pos;
   private IBlockAccess blockAccess;
   private static final long a = 35915437786310L;

   public BlockFaceEvent(
      Block var1, EnumFacing var2, BlockPos var3, IBlockAccess var4, double var5, double var7, double var9, double var11, double var13, double var15
   ) {
      this.block = var1;
      this.facing = var2;
      this.pos = var3;
      this.blockAccess = var4;
      this.U = var13;
      this.e = var15;
      this.A = var5;
      this.n = var7;
      this.q = var9;
      this.O = var11;
   }

   public Block getBlock() {
      return this.block;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public EnumFacing getFacing() {
      return this.facing;
   }

   public double f() {
      return this.n;
   }

   public double H() {
      return this.A;
   }

   public double k() {
      return this.O;
   }

   public double E() {
      return this.q;
   }

   public double h() {
      return this.e;
   }

   public IBlockAccess getBlockAccess() {
      return this.blockAccess;
   }

   public double r() {
      return this.U;
   }
}
