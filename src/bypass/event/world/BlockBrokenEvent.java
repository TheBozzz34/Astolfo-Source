package bypass.event.world;

import bypass.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockBrokenEvent implements Event {
   private Block block;
   private EnumFacing facing;
   private BlockPos pos;

   public BlockBrokenEvent(Block var1, EnumFacing var2, BlockPos var3) {
      this.block = var1;
      this.facing = var2;
      this.pos = var3;
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
