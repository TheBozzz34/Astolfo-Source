package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe {
   private static final long a = 130197091373589L;
   private static final long b = -6277061709165232119L;

   @Override
   public boolean matches(InventoryCrafting var1, World var2) {
      int var6 = 0;
      ItemStack var7 = null;

      for (int var8 = 0; var8 < var1.getSizeInventory(); var8++) {
         ItemStack var9 = var1.getStackInSlot(var8);
         if (var9 != null) {
            if (var9.getItem() == Items.filled_map) {
               if (var7 != null) {
                  return false;
               }

               var7 = var9;
            } else {
               if (var9.getItem() != Items.map) {
                  return false;
               }

               var6++;
            }
         }
      }

      return var7 != null && var6 > 0;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      int var5 = 0;
      ItemStack var6 = null;

      for (int var7 = 0; var7 < var1.getSizeInventory(); var7++) {
         ItemStack var8 = var1.getStackInSlot(var7);
         if (var8 != null) {
            if (var8.getItem() == Items.filled_map) {
               if (var6 != null) {
                  return null;
               }

               var6 = var8;
            } else {
               if (var8.getItem() != Items.map) {
                  return null;
               }

               var5++;
            }
         }
      }

      if (var6 != null && var5 >= 1) {
         ItemStack var9 = new ItemStack(Items.filled_map, var5 + 1, var6.getMetadata());
         if (var6.hasDisplayName()) {
            var9.setStackDisplayName(var6.getDisplayName());
         }

         return var9;
      } else {
         return null;
      }
   }

   @Override
   public int getRecipeSize() {
      return (int)b;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting var1) {
      ItemStack[] var5 = new ItemStack[var1.getSizeInventory()];

      for (int var6 = 0; var6 < var5.length; var6++) {
         ItemStack var7 = var1.getStackInSlot(var6);
         if (var7 != null && var7.getItem().hasContainerItem()) {
            var5[var6] = new ItemStack(var7.getItem().getContainerItem());
         }
      }

      return var5;
   }
}
