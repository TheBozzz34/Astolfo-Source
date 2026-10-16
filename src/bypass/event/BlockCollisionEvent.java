package bypass.event;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockCollisionEvent implements Event {
   private Block block;
   private AxisAlignedBB boundingBox;
   private BlockPos pos;

   public BlockCollisionEvent(Block block, AxisAlignedBB axisAlignedBB, BlockPos blockPos) {
      this.block = block;
      this.boundingBox = axisAlignedBB;
      this.pos = blockPos;
   }

   public Block getBlock() {
      return this.block;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setBoundingBox(AxisAlignedBB axisAlignedBB) {
      this.boundingBox = axisAlignedBB;
   }

   public int getX() {
      return this.pos.getX();
   }

   public int getY() {
      return this.pos.getY();
   }

   public int getZ() {
      return this.pos.getZ();
   }
}
