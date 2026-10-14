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

   public ShapedRecipes(int width, int height, ItemStack[] itemStacks, ItemStack output) {
      this.recipeWidth = width;
      this.recipeHeight = height;
      this.recipeItems = itemStacks;
      this.recipeOutput = output;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] itemStacks = new ItemStack[inv.getSizeInventory()];

      for (int i = 0; i < itemStacks.length; i++) {
         ItemStack itemStack = inv.getStackInSlot(i);
         if (itemStack != null && itemStack.getItem().hasContainerItem()) {
            itemStacks[i] = new ItemStack(itemStack.getItem().getContainerItem());
         }
      }

      return itemStacks;
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      for (int i = 0; i <= 3 - this.recipeWidth; i++) {
         for (int j = 0; j <= 3 - this.recipeHeight; j++) {
            if (this.checkMatch(inv, i, j, true)) {
               return true;
            }

            if (this.checkMatch(inv, i, j, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean checkMatch(InventoryCrafting inventoryCrafting, int i, int j, boolean bl) {
      for (int k = 0; k < 3; k++) {
         for (int l = 0; l < 3; l++) {
            int m = k - i;
            int n = l - j;
            ItemStack itemStack = null;
            if (m >= 0 && n >= 0 && m < this.recipeWidth && n < this.recipeHeight) {
               if (bl) {
                  itemStack = this.recipeItems[this.recipeWidth - m - 1 + n * this.recipeWidth];
               } else {
                  itemStack = this.recipeItems[m + n * this.recipeWidth];
               }
            }

            ItemStack itemStack2 = inventoryCrafting.getStackInRowAndColumn(k, l);
            if (itemStack2 != null || itemStack != null) {
               if (itemStack2 == null && itemStack != null || itemStack2 != null && itemStack == null) {
                  return false;
               }

               if (itemStack.getItem() != itemStack2.getItem()) {
                  return false;
               }

               if (itemStack.getMetadata() != 32767 && itemStack.getMetadata() != itemStack2.getMetadata()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack itemStack = this.getRecipeOutput().copy();
      if (this.copyIngredientNBT) {
         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack2 = inv.getStackInSlot(i);
            if (itemStack2 != null && itemStack2.hasTagCompound()) {
               itemStack.setTagCompound((NBTTagCompound)itemStack2.getTagCompound().copy());
            }
         }
      }

      return itemStack;
   }

   @Override
   public int getRecipeSize() {
      return this.recipeWidth * this.recipeHeight;
   }
}
