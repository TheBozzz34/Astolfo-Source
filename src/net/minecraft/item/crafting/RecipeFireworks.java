package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe {
   private ItemStack field_92102_a;
   private static final long a = 83266674986397L;

   @Override
   public boolean matches(InventoryCrafting var1, World var2) {
      this.field_92102_a = null;
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;

      for (int var13 = 0; var13 < var1.getSizeInventory(); var13++) {
         ItemStack var14 = var1.getStackInSlot(var13);
         if (var14 != null) {
            if (var14.getItem() == Items.gunpowder) {
               var8++;
            } else if (var14.getItem() == Items.firework_charge) {
               var10++;
            } else if (var14.getItem() == Items.dye) {
               var9++;
            } else if (var14.getItem() == Items.paper) {
               var7++;
            } else if (var14.getItem() == Items.glowstone_dust) {
               var11++;
            } else if (var14.getItem() == Items.diamond) {
               var11++;
            } else if (var14.getItem() == Items.fire_charge) {
               var12++;
            } else if (var14.getItem() == Items.feather) {
               var12++;
            } else if (var14.getItem() == Items.gold_nugget) {
               var12++;
            } else {
               if (var14.getItem() != Items.skull) {
                  return false;
               }

               var12++;
            }
         }
      }

      var11 = var11 + var9 + var12;
      if (var8 > 3 || var7 > 1) {
         return false;
      }

      if (var8 >= 1 && var7 == 1 && var11 == 0) {
         this.field_92102_a = new ItemStack(Items.fireworks);
         if (var10 > 0) {
            NBTTagCompound var22 = new NBTTagCompound();
            NBTTagCompound var26 = new NBTTagCompound();
            NBTTagList var30 = new NBTTagList();

            for (int var31 = 0; var31 < var1.getSizeInventory(); var31++) {
               ItemStack var33 = var1.getStackInSlot(var31);
               if (var33 != null && var33.getItem() == Items.firework_charge && var33.hasTagCompound() && var33.getTagCompound().hasKey("Explosion", 10)) {
                  var30.appendTag(var33.getTagCompound().getCompoundTag("Explosion"));
               }
            }

            var26.setTag("Explosions", var30);
            var26.setByte("Flight", (byte)var8);
            var22.setTag("Fireworks", var26);
            this.field_92102_a.setTagCompound(var22);
         }

         return true;
      } else if (var8 == 1 && var7 == 0 && var10 == 0 && var9 > 0 && var12 <= 1) {
         this.field_92102_a = new ItemStack(Items.firework_charge);
         NBTTagCompound var21 = new NBTTagCompound();
         NBTTagCompound var25 = new NBTTagCompound();
         byte var29 = 0;
         ArrayList var16 = Lists.newArrayList();

         for (int var17 = 0; var17 < var1.getSizeInventory(); var17++) {
            ItemStack var18 = var1.getStackInSlot(var17);
            if (var18 != null) {
               if (var18.getItem() == Items.dye) {
                  var16.add(ItemDye.dyeColors[var18.getMetadata() & 15]);
               } else if (var18.getItem() == Items.glowstone_dust) {
                  var25.setBoolean("Flicker", true);
               } else if (var18.getItem() == Items.diamond) {
                  var25.setBoolean("Trail", true);
               } else if (var18.getItem() == Items.fire_charge) {
                  var29 = 1;
               } else if (var18.getItem() == Items.feather) {
                  var29 = 4;
               } else if (var18.getItem() == Items.gold_nugget) {
                  var29 = 2;
               } else if (var18.getItem() == Items.skull) {
                  var29 = 3;
               }
            }
         }

         int[] var32 = new int[var16.size()];

         for (int var34 = 0; var34 < var32.length; var34++) {
            var32[var34] = (Integer)var16.get(var34);
         }

         var25.setIntArray("Colors", var32);
         var25.setByte("Type", var29);
         var21.setTag("Explosion", var25);
         this.field_92102_a.setTagCompound(var21);
         return true;
      } else if (var8 == 0 && var7 == 0 && var10 == 1 && var9 > 0 && var9 == var11) {
         ArrayList var20 = Lists.newArrayList();

         for (int var23 = 0; var23 < var1.getSizeInventory(); var23++) {
            ItemStack var15 = var1.getStackInSlot(var23);
            if (var15 != null) {
               if (var15.getItem() == Items.dye) {
                  var20.add(ItemDye.dyeColors[var15.getMetadata() & 15]);
               } else if (var15.getItem() == Items.firework_charge) {
                  this.field_92102_a = var15.copy();
                  this.field_92102_a.stackSize = 1;
               }
            }
         }

         int[] var24 = new int[var20.size()];

         for (int var27 = 0; var27 < var24.length; var27++) {
            var24[var27] = (Integer)var20.get(var27);
         }

         if (this.field_92102_a != null && this.field_92102_a.hasTagCompound()) {
            NBTTagCompound var28 = this.field_92102_a.getTagCompound().getCompoundTag("Explosion");
            if (var28 == null) {
               return false;
            }

            var28.setIntArray("FadeColors", var24);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting var1) {
      return this.field_92102_a.copy();
   }

   @Override
   public int getRecipeSize() {
      return 10;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.field_92102_a;
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
