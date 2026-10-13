package net.minecraft.item;

public class ItemBook extends Item {
   private static final long a = 6165114334048L;

   @Override
   public boolean isItemTool(ItemStack var1) {
      return var1.stackSize == 1;
   }

   @Override
   public int getItemEnchantability() {
      return 1;
   }
}
