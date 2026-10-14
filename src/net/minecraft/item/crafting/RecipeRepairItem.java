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
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack itemStack = inv.getStackInSlot(i);
         if (itemStack != null) {
            arrayList.add(itemStack);
            if (arrayList.size() > 1) {
               ItemStack itemStack2 = (ItemStack)arrayList.get(0);
               if (itemStack.getItem() != itemStack2.getItem() || itemStack2.stackSize != 1 || itemStack.stackSize != 1 || !itemStack2.getItem().isDamageable()) {
                  return false;
               }
            }
         }
      }

      return arrayList.size() == 2;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack itemStack = inv.getStackInSlot(i);
         if (itemStack != null) {
            arrayList.add(itemStack);
            if (arrayList.size() > 1) {
               ItemStack itemStack2 = (ItemStack)arrayList.get(0);
               if (itemStack.getItem() != itemStack2.getItem() || itemStack2.stackSize != 1 || itemStack.stackSize != 1 || !itemStack2.getItem().isDamageable()) {
                  return null;
               }
            }
         }
      }

      if (arrayList.size() == 2) {
         ItemStack itemStack3 = (ItemStack)arrayList.get(0);
         ItemStack itemStack4 = (ItemStack)arrayList.get(1);
         if (itemStack3.getItem() == itemStack4.getItem() && itemStack3.stackSize == 1 && itemStack4.stackSize == 1 && itemStack3.getItem().isDamageable()) {
            Item item = itemStack3.getItem();
            int j = item.getMaxDamage() - itemStack3.getItemDamage();
            int k = item.getMaxDamage() - itemStack4.getItemDamage();
            int l = j + k + item.getMaxDamage() * 5 / (int)b;
            int m = item.getMaxDamage() - l;
            if (m < 0) {
               m = 0;
            }

            return new ItemStack(itemStack3.getItem(), 1, m);
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
}
