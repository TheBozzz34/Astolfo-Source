package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ItemStack itemStack = null;
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack itemStack2 = inv.getStackInSlot(i);
         if (itemStack2 != null) {
            if (itemStack2.getItem() instanceof ItemArmor) {
               ItemArmor itemArmor = (ItemArmor)itemStack2.getItem();
               if (itemArmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || itemStack != null) {
                  return false;
               }

               itemStack = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.dye) {
                  return false;
               }

               arrayList.add(itemStack2);
            }
         }
      }

      return itemStack != null && !arrayList.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack itemStack = null;
      int[] ints = new int[3];
      int i = 0;
      int j = 0;
      ItemArmor itemArmor = null;

      for (int k = 0; k < inv.getSizeInventory(); k++) {
         ItemStack itemStack2 = inv.getStackInSlot(k);
         if (itemStack2 != null) {
            if (itemStack2.getItem() instanceof ItemArmor) {
               itemArmor = (ItemArmor)itemStack2.getItem();
               if (itemArmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || itemStack != null) {
                  return null;
               }

               itemStack = itemStack2.copy();
               itemStack.stackSize = 1;
               if (itemArmor.hasColor(itemStack2)) {
                  int l = itemArmor.getColor(itemStack);
                  float f = (l >> 16 & 0xFF) / 255.0F;
                  float g = (l >> 8 & 0xFF) / 255.0F;
                  float h = (l & 0xFF) / 255.0F;
                  i = (int)(i + Math.max(f, Math.max(g, h)) * 255.0F);
                  ints[0] = (int)(ints[0] + f * 255.0F);
                  ints[1] = (int)(ints[1] + g * 255.0F);
                  ints[2] = (int)(ints[2] + h * 255.0F);
                  j++;
               }
            } else {
               if (itemStack2.getItem() != Items.dye) {
                  return null;
               }

               float[] floats = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(itemStack2.getMetadata()));
               int m = (int)(floats[0] * 255.0F);
               int n = (int)(floats[1] * 255.0F);
               int i2 = (int)(floats[2] * 255.0F);
               i += Math.max(m, Math.max(n, i2));
               ints[0] += m;
               ints[1] += n;
               ints[2] += i2;
               j++;
            }
         }
      }

      if (itemArmor == null) {
         return null;
      }

      int i3 = ints[0] / j;
      int i4 = ints[1] / j;
      int i5 = ints[2] / j;
      float f2 = (float)i / j;
      float f3 = Math.max(i3, Math.max(i4, i5));
      i3 = (int)(i3 * f2 / f3);
      i4 = (int)(i4 * f2 / f3);
      i5 = (int)(i5 * f2 / f3);
      int i6 = (i3 << 8) + i4;
      i6 = (i6 << 8) + i5;
      itemArmor.setColor(itemStack, i6);
      return itemStack;
   }

   @Override
   public int getRecipeSize() {
      return 10;
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
