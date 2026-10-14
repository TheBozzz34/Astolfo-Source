package bypass.event.world;

import bypass.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockBrokenEvent implements Event {
   private Block block;
   private EnumFacing facing;
   private BlockPos pos;

   public BlockBrokenEvent(Block block, EnumFacing enumFacing, BlockPos blockPos) {
      this.block = block;
      this.facing = enumFacing;
      this.pos = blockPos;
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
}
