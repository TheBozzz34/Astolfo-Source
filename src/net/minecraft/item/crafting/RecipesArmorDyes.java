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
   private static final long a = 111105649771980L;

   @Override
   public boolean matches(InventoryCrafting var1, World var2) {
      ItemStack var6 = null;
      ArrayList var7 = Lists.newArrayList();

      for (int var8 = 0; var8 < var1.getSizeInventory(); var8++) {
         ItemStack var9 = var1.getStackInSlot(var8);
         if (var9 != null) {
            if (var9.getItem() instanceof ItemArmor) {
               ItemArmor var10 = (ItemArmor)var9.getItem();
               if (var10.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || var6 != null) {
                  return false;
               }

               var6 = var9;
            } else {
               if (var9.getItem() != Items.dye) {
                  return false;
               }

               var7.add(var9);
            }
         }
      }

      return var6 != null && !var7.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      ItemStack var5 = null;
      int[] var6 = new int[3];
      int var7 = 0;
      int var8 = 0;
      ItemArmor var9 = null;

      for (int var10 = 0; var10 < var1.getSizeInventory(); var10++) {
         ItemStack var11 = var1.getStackInSlot(var10);
         if (var11 != null) {
            if (var11.getItem() instanceof ItemArmor) {
               var9 = (ItemArmor)var11.getItem();
               if (var9.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || var5 != null) {
                  return null;
               }

               var5 = var11.copy();
               var5.stackSize = 1;
               if (var9.hasColor(var11)) {
                  int var12 = var9.getColor(var5);
                  float var13 = (var12 >> 16 & 0xFF) / 255.0F;
                  float var14 = (var12 >> 8 & 0xFF) / 255.0F;
                  float var15 = (var12 & 0xFF) / 255.0F;
                  var7 = (int)(var7 + Math.max(var13, Math.max(var14, var15)) * 255.0F);
                  var6[0] = (int)(var6[0] + var13 * 255.0F);
                  var6[1] = (int)(var6[1] + var14 * 255.0F);
                  var6[2] = (int)(var6[2] + var15 * 255.0F);
                  var8++;
               }
            } else {
               if (var11.getItem() != Items.dye) {
                  return null;
               }

               float[] var20 = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(var11.getMetadata()));
               int var23 = (int)(var20[0] * 255.0F);
               int var25 = (int)(var20[1] * 255.0F);
               int var27 = (int)(var20[2] * 255.0F);
               var7 += Math.max(var23, Math.max(var25, var27));
               var6[0] += var23;
               var6[1] += var25;
               var6[2] += var27;
               var8++;
            }
         }
      }

      if (var9 == null) {
         return null;
      }

      int var16 = var6[0] / var8;
      int var18 = var6[1] / var8;
      int var21 = var6[2] / var8;
      float var24 = (float)var7 / var8;
      float var26 = Math.max(var16, Math.max(var18, var21));
      var16 = (int)(var16 * var24 / var26);
      var18 = (int)(var18 * var24 / var26);
      var21 = (int)(var21 * var24 / var26);
      int var28 = (var16 << 8) + var18;
      var28 = (var28 << 8) + var21;
      var9.setColor(var5, var28);
      return var5;
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
