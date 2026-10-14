package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   private final ItemStack recipeOutput;
   private final List<ItemStack> recipeItems;

   public ShapelessRecipes(ItemStack output, List<ItemStack> inputList) {
      this.recipeOutput = output;
      this.recipeItems = inputList;
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
      ArrayList arrayList = Lists.newArrayList(this.recipeItems);

      for (int i = 0; i < inv.getHeight(); i++) {
         for (int j = 0; j < inv.getWidth(); j++) {
            ItemStack itemStack = inv.getStackInRowAndColumn(j, i);
            if (itemStack != null) {
               boolean bl = false;

               for (Object object : arrayList) {
                  ItemStack itemStack2 = (ItemStack)object;
                  if (itemStack.getItem() == itemStack2.getItem() && (itemStack2.getMetadata() == 32767 || itemStack.getMetadata() == itemStack2.getMetadata())) {
                     bl = true;
                     arrayList.remove(itemStack2);
                     break;
                  }
               }

               if (!bl) {
                  return false;
               }
            }
         }
      }

      return arrayList.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.recipeOutput.copy();
   }

   @Override
   public int getRecipeSize() {
      return this.recipeItems.size();
   }
}
