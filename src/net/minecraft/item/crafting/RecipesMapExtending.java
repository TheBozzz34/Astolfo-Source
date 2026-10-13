package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending extends ShapedRecipes {
   private static final long a = 12279004397162L;

   public RecipesMapExtending() {
      super(
         3,
         3,
         new ItemStack[]{
            new ItemStack(Items.paper),
            new ItemStack(Items.paper),
            new ItemStack(Items.paper),
            new ItemStack(Items.paper),
            new ItemStack(Items.filled_map, 0, 32767),
            new ItemStack(Items.paper),
            new ItemStack(Items.paper),
            new ItemStack(Items.paper),
            new ItemStack(Items.paper)
         },
         new ItemStack(Items.map, 0, 0)
      );
   }

   @Override
   public boolean matches(InventoryCrafting var1, World var2) {
      if (!super.matches(var1, var2)) {
         return false;
      }

      ItemStack var6 = null;

      for (int var7 = 0; var7 < var1.getSizeInventory() && var6 == null; var7++) {
         ItemStack var8 = var1.getStackInSlot(var7);
         if (var8 != null && var8.getItem() == Items.filled_map) {
            var6 = var8;
         }
      }

      if (var6 == null) {
         return false;
      }

      MapData var9 = Items.filled_map.getMapData(var6, var2);
      return var9 == null ? false : var9.scale < 4;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      ItemStack var5 = null;

      for (int var6 = 0; var6 < var1.getSizeInventory() && var5 == null; var6++) {
         ItemStack var7 = var1.getStackInSlot(var6);
         if (var7 != null && var7.getItem() == Items.filled_map) {
            var5 = var7;
         }
      }

      var5 = var5.copy();
      var5.stackSize = 1;
      if (var5.getTagCompound() == null) {
         var5.setTagCompound(new NBTTagCompound());
      }

      var5.getTagCompound().setBoolean("map_is_scaling", true);
      return var5;
   }
}
