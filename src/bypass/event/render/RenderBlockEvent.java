package bypass.event.render;

import bypass.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class RenderBlockEvent implements Event {
   private final BlockPos pos;
   private final Block block;

   public RenderBlockEvent(Block var1, BlockPos var2) {
      this.pos = var2;
      this.block = var1;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlock() {
      return this.block;
   }
}
