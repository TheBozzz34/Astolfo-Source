package net.minecraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFurnaceFuel extends Slot {
   private static final long a = 61910983744228L;

   public SlotFurnaceFuel(IInventory var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public boolean isItemValid(ItemStack var1) {
      return TileEntityFurnace.isItemFuel(var1) || isBucket(var1);
   }

   @Override
   public int getItemStackLimit(ItemStack var1) {
      return isBucket(var1) ? 1 : super.getItemStackLimit(var1);
   }

   public static boolean isBucket(ItemStack var0) {
      return var0 != null && var0.getItem() != null && var0.getItem() == Items.bucket;
   }
}
