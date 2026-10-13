package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeRepairItem implements IRecipe {
   private static final long a = 6977921828562L;
   private static final long b = -7843211187075415964L;

   @Override
   public boolean matches(InventoryCrafting var1, World var2) {
      ArrayList var6 = Lists.newArrayList();

      for (int var7 = 0; var7 < var1.getSizeInventory(); var7++) {
         ItemStack var8 = var1.getStackInSlot(var7);
         if (var8 != null) {
            var6.add(var8);
            if (var6.size() > 1) {
               ItemStack var9 = (ItemStack)var6.get(0);
               if (var8.getItem() != var9.getItem() || var9.stackSize != 1 || var8.stackSize != 1 || !var9.getItem().isDamageable()) {
                  return false;
               }
            }
         }
      }

      return var6.size() == 2;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      ArrayList var5 = Lists.newArrayList();

      for (int var6 = 0; var6 < var1.getSizeInventory(); var6++) {
         ItemStack var7 = var1.getStackInSlot(var6);
         if (var7 != null) {
            var5.add(var7);
            if (var5.size() > 1) {
               ItemStack var8 = (ItemStack)var5.get(0);
               if (var7.getItem() != var8.getItem() || var8.stackSize != 1 || var7.stackSize != 1 || !var8.getItem().isDamageable()) {
                  return null;
               }
            }
         }
      }

      if (var5.size() == 2) {
         ItemStack var13 = (ItemStack)var5.get(0);
         ItemStack var14 = (ItemStack)var5.get(1);
         if (var13.getItem() == var14.getItem() && var13.stackSize == 1 && var14.stackSize == 1 && var13.getItem().isDamageable()) {
            Item var15 = var13.getItem();
            int var9 = var15.getMaxDamage() - var13.getItemDamage();
            int var10 = var15.getMaxDamage() - var14.getItemDamage();
            int var11 = var9 + var10 + var15.getMaxDamage() * 5 / (int)b;
            int var12 = var15.getMaxDamage() - var11;
            if (var12 < 0) {
               var12 = 0;
            }

            return new ItemStack(var13.getItem(), 1, var12);
         }
      }

      return null;
   }

   @Override
   public int getRecipeSize() {
      return 4;
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
