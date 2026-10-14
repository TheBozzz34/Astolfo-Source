package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemAnvilBlock extends ItemMultiTexture {
   private static final long a = 62934928561592L;

   public ItemAnvilBlock(Block block) {
      super(block, block, new String[]{"intact", "slightlyDamaged", "veryDamaged"});
   }

   @Override
   public int getMetadata(int damage) {
      return damage << 2;
   }
}
