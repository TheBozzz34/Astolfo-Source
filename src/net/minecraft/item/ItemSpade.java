package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ItemSpade extends ItemTool {
   private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(
      Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel, Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand
   );
   private static final long a = 105412916980203L;

   public ItemSpade(Item.ToolMaterial var1) {
      super(1.0F, var1, EFFECTIVE_ON);
   }

   @Override
   public boolean canHarvestBlock(Block var1) {
      return var1 == Blocks.snow_layer ? true : var1 == Blocks.snow;
   }
}
