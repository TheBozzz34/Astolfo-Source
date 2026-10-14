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
      Block block, EnumFacing enumFacing, BlockPos blockPos, IBlockAccess iBlockAccess, double d, double d2, double d3, double d4, double d5, double d6
   ) {
      this.block = block;
      this.facing = enumFacing;
      this.pos = blockPos;
      this.blockAccess = iBlockAccess;
      this.U = d5;
      this.e = d6;
      this.A = d;
      this.n = d2;
      this.q = d3;
      this.O = d4;
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
