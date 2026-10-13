package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemAnvilBlock extends ItemMultiTexture {
   private static final long a = 62934928561592L;

   public ItemAnvilBlock(Block var1) {
      super(var1, var1, new String[]{"intact", "slightlyDamaged", "veryDamaged"});
   }

   @Override
   public int getMetadata(int var1) {
      return var1 << 2;
   }
}
