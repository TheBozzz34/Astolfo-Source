package bypass.event;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockFaceEvent extends CancellableEvent {
   private Block block;
   private EnumFacing facing;
   private double maxY;
   private double maxZ;
   private double minX;
   private double minY;
   private double minZ;
   private double maxX;
   private BlockPos pos;
   private IBlockAccess blockAccess;

   public BlockFaceEvent(
      Block block, EnumFacing enumFacing, BlockPos blockPos, IBlockAccess iBlockAccess, double d, double e, double d2, double d3, double d4, double d5
   ) {
      this.block = block;
      this.facing = enumFacing;
      this.pos = blockPos;
      this.blockAccess = iBlockAccess;
      this.maxY = d4;
      this.maxZ = d5;
      this.minX = d;
      this.minY = e;
      this.minZ = d2;
      this.maxX = d3;
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

   public double getMinY() {
      return this.minY;
   }

   public double getMinX() {
      return this.minX;
   }

   public double getMaxX() {
      return this.maxX;
   }

   public double getMinZ() {
      return this.minZ;
   }

   public double getMaxZ() {
      return this.maxZ;
   }

   public IBlockAccess getBlockAccess() {
      return this.blockAccess;
   }

   public double getMaxY() {
      return this.maxY;
   }
}
