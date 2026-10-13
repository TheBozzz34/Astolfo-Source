package bypass.event.world;

import bypass.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class LiquidCollisionEvent extends CancellableEvent {
   private Block block;
   private AxisAlignedBB boundingBox;
   private BlockPos pos;

   public LiquidCollisionEvent(Block var1, AxisAlignedBB var2, BlockPos var3) {
      this.block = var1;
      this.boundingBox = var2;
      this.pos = var3;
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

   public void setBoundingBox(AxisAlignedBB var1) {
      this.boundingBox = var1;
   }
}
