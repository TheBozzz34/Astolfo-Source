package bypass.event.render;

import bypass.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class RenderBlockEvent implements Event {
   private final BlockPos pos;
   private final Block block;

   public RenderBlockEvent(Block block, BlockPos blockPos) {
      this.pos = blockPos;
      this.block = block;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlock() {
      return this.block;
   }
}
