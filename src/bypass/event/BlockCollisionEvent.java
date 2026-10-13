package bypass.event;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockCollisionEvent implements Event {
   private Block block;
   private AxisAlignedBB boundingBox;
   private BlockPos pos;

   public BlockCollisionEvent(Block var1, AxisAlignedBB var2, BlockPos var3) {
      this.block = var1;
      this.boundingBox = var2;
      this.pos = var3;
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

   public void setBoundingBox(AxisAlignedBB var1) {
      this.boundingBox = var1;
   }

   public int I() {
      return this.pos.getX();
   }

   public int W() {
      return this.pos.getY();
   }

   public int B() {
      return this.pos.getZ();
   }
}
