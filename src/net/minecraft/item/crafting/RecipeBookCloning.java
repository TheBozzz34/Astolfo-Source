package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      int i = 0;
      ItemStack itemStack = null;

      for (int j = 0; j < inv.getSizeInventory(); j++) {
         ItemStack itemStack2 = inv.getStackInSlot(j);
         if (itemStack2 != null) {
            if (itemStack2.getItem() == Items.written_book) {
               if (itemStack != null) {
                  return false;
               }

               itemStack = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.writable_book) {
                  return false;
               }

               i++;
            }
         }
      }

      return itemStack != null && i > 0;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      int i = 0;
      ItemStack itemStack = null;

      for (int j = 0; j < inv.getSizeInventory(); j++) {
         ItemStack itemStack2 = inv.getStackInSlot(j);
         if (itemStack2 != null) {
            if (itemStack2.getItem() == Items.written_book) {
               if (itemStack != null) {
                  return null;
               }

               itemStack = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.writable_book) {
                  return null;
               }

               i++;
            }
         }
      }

      if (itemStack != null && i >= 1 && ItemEditableBook.getGeneration(itemStack) < 2) {
         ItemStack itemStack3 = new ItemStack(Items.written_book, i);
         itemStack3.setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
         itemStack3.getTagCompound().setInteger("generation", ItemEditableBook.getGeneration(itemStack) + 1);
         if (itemStack.hasDisplayName()) {
            itemStack3.setStackDisplayName(itemStack.getDisplayName());
         }

         return itemStack3;
      } else {
         return null;
      }
   }

   @Override
   public int getRecipeSize() {
      return 9;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] itemStacks = new ItemStack[inv.getSizeInventory()];

      for (int i = 0; i < itemStacks.length; i++) {
         ItemStack itemStack = inv.getStackInSlot(i);
         if (itemStack != null && itemStack.getItem() instanceof ItemEditableBook) {
            itemStacks[i] = itemStack;
            break;
         }
      }

      return itemStacks;
   }
}
