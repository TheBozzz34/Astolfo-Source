package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemPiston extends ItemBlock {
   private static final long a = 108471679335004L;
   private static final long d = 4519873142490923015L;

   public ItemPiston(Block block) {
      super(block);
   }

   @Override
   public int getMetadata(int damage) {
      return (int)d;
   }
}
