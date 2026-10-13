package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe {
   private final int recipeWidth;
   private final int recipeHeight;
   private final ItemStack[] recipeItems;
   private final ItemStack recipeOutput;
   private boolean copyIngredientNBT;
   private static final long b = 96618596064655L;
   private static final long d = -7162599354744668161L;

   public ShapedRecipes(int var1, int var2, ItemStack[] var3, ItemStack var4) {
      this.recipeWidth = var1;
      this.recipeHeight = var2;
      this.recipeItems = var3;
      this.recipeOutput = var4;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
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

   @Override
   public boolean matches(InventoryCrafting var1, World var2) {
      for (int var6 = 0; var6 <= 3 - this.recipeWidth; var6++) {
         for (int var7 = 0; var7 <= 3 - this.recipeHeight; var7++) {
            if (this.checkMatch(var1, var6, var7, true)) {
               return true;
            }

            if (this.checkMatch(var1, var6, var7, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean checkMatch(InventoryCrafting var1, int var2, int var3, boolean var4) {
      for (int var8 = 0; var8 < 3; var8++) {
         for (int var9 = 0; var9 < 3; var9++) {
            int var10 = var8 - var2;
            int var11 = var9 - var3;
            ItemStack var12 = null;
            if (var10 >= 0 && var11 >= 0 && var10 < this.recipeWidth && var11 < this.recipeHeight) {
               if (var4) {
                  var12 = this.recipeItems[this.recipeWidth - var10 - 1 + var11 * this.recipeWidth];
               } else {
                  var12 = this.recipeItems[var10 + var11 * this.recipeWidth];
               }
            }

            ItemStack var13 = var1.getStackInRowAndColumn(var8, var9);
            if (var13 != null || var12 != null) {
               if (var13 == null && var12 != null || var13 != null && var12 == null) {
                  return false;
               }

               if (var12.getItem() != var13.getItem()) {
                  return false;
               }

               if (var12.getMetadata() != (int)d && var12.getMetadata() != var13.getMetadata()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      ItemStack var5 = this.getRecipeOutput().copy();
      if (this.copyIngredientNBT) {
         for (int var6 = 0; var6 < var1.getSizeInventory(); var6++) {
            ItemStack var7 = var1.getStackInSlot(var6);
            if (var7 != null && var7.hasTagCompound()) {
               var5.setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
            }
         }
      }

      return var5;
   }

   @Override
   public int getRecipeSize() {
      return this.recipeWidth * this.recipeHeight;
   }
}
