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
   private static final long a = 113690852796612L;
   private static final long b = -1911211930769391617L;

   public ShapelessRecipes(ItemStack var1, List<ItemStack> var2) {
      this.recipeOutput = var1;
      this.recipeItems = var2;
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
      ArrayList var6 = Lists.newArrayList(this.recipeItems);

      for (int var7 = 0; var7 < var1.getHeight(); var7++) {
         for (int var8 = 0; var8 < var1.getWidth(); var8++) {
            ItemStack var9 = var1.getStackInRowAndColumn(var8, var7);
            if (var9 != null) {
               boolean var10 = false;

               for (Object var12$raw : var6) {
                  ItemStack var12 = (ItemStack)var12$raw;
                  if (var9.getItem() == var12.getItem() && (var12.getMetadata() == (int)b || var9.getMetadata() == var12.getMetadata())) {
                     var10 = true;
                     var6.remove(var12);
                     break;
                  }
               }

               if (!var10) {
                  return false;
               }
            }
         }
      }

      return var6.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      return this.recipeOutput.copy();
   }

   @Override
   public int getRecipeSize() {
      return this.recipeItems.size();
   }
}
