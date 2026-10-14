package bypass.event.world;

import bypass.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class LiquidCollisionEvent extends CancellableEvent {
   private Block block;
   private AxisAlignedBB boundingBox;
   private BlockPos pos;

   public LiquidCollisionEvent(Block block, AxisAlignedBB axisAlignedBB, BlockPos blockPos) {
      this.block = block;
      this.boundingBox = axisAlignedBB;
      this.pos = blockPos;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlock() {
      return this.block;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB axisAlignedBB) {
      this.boundingBox = axisAlignedBB;
   }
}
