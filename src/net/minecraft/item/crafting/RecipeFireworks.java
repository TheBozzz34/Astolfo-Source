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

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      this.field_92102_a = null;
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      int m = 0;
      int n = 0;

      for (int i2 = 0; i2 < inv.getSizeInventory(); i2++) {
         ItemStack itemStack = inv.getStackInSlot(i2);
         if (itemStack != null) {
            if (itemStack.getItem() == Items.gunpowder) {
               j++;
            } else if (itemStack.getItem() == Items.firework_charge) {
               l++;
            } else if (itemStack.getItem() == Items.dye) {
               k++;
            } else if (itemStack.getItem() == Items.paper) {
               i++;
            } else if (itemStack.getItem() == Items.glowstone_dust) {
               m++;
            } else if (itemStack.getItem() == Items.diamond) {
               m++;
            } else if (itemStack.getItem() == Items.fire_charge) {
               n++;
            } else if (itemStack.getItem() == Items.feather) {
               n++;
            } else if (itemStack.getItem() == Items.gold_nugget) {
               n++;
            } else {
               if (itemStack.getItem() != Items.skull) {
                  return false;
               }

               n++;
            }
         }
      }

      m = m + k + n;
      if (j > 3 || i > 1) {
         return false;
      }

      if (j >= 1 && i == 1 && m == 0) {
         this.field_92102_a = new ItemStack(Items.fireworks);
         if (l > 0) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            NBTTagList nbtTagList = new NBTTagList();

            for (int i3 = 0; i3 < inv.getSizeInventory(); i3++) {
               ItemStack itemStack2 = inv.getStackInSlot(i3);
               if (itemStack2 != null && itemStack2.getItem() == Items.firework_charge && itemStack2.hasTagCompound() && itemStack2.getTagCompound().hasKey("Explosion", 10)) {
                  nbtTagList.appendTag(itemStack2.getTagCompound().getCompoundTag("Explosion"));
               }
            }

            nbtTagCompound2.setTag("Explosions", nbtTagList);
            nbtTagCompound2.setByte("Flight", (byte)j);
            nbtTagCompound.setTag("Fireworks", nbtTagCompound2);
            this.field_92102_a.setTagCompound(nbtTagCompound);
         }

         return true;
      } else if (j == 1 && i == 0 && l == 0 && k > 0 && n <= 1) {
         this.field_92102_a = new ItemStack(Items.firework_charge);
         NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
         NBTTagCompound nbtTagCompound4 = new NBTTagCompound();
         byte b = 0;
         ArrayList arrayList = Lists.newArrayList();

         for (int i4 = 0; i4 < inv.getSizeInventory(); i4++) {
            ItemStack itemStack3 = inv.getStackInSlot(i4);
            if (itemStack3 != null) {
               if (itemStack3.getItem() == Items.dye) {
                  arrayList.add(ItemDye.dyeColors[itemStack3.getMetadata() & 15]);
               } else if (itemStack3.getItem() == Items.glowstone_dust) {
                  nbtTagCompound4.setBoolean("Flicker", true);
               } else if (itemStack3.getItem() == Items.diamond) {
                  nbtTagCompound4.setBoolean("Trail", true);
               } else if (itemStack3.getItem() == Items.fire_charge) {
                  b = 1;
               } else if (itemStack3.getItem() == Items.feather) {
                  b = 4;
               } else if (itemStack3.getItem() == Items.gold_nugget) {
                  b = 2;
               } else if (itemStack3.getItem() == Items.skull) {
                  b = 3;
               }
            }
         }

         int[] ints = new int[arrayList.size()];

         for (int i5 = 0; i5 < ints.length; i5++) {
            ints[i5] = (Integer)arrayList.get(i5);
         }

         nbtTagCompound4.setIntArray("Colors", ints);
         nbtTagCompound4.setByte("Type", b);
         nbtTagCompound3.setTag("Explosion", nbtTagCompound4);
         this.field_92102_a.setTagCompound(nbtTagCompound3);
         return true;
      } else if (j == 0 && i == 0 && l == 1 && k > 0 && k == m) {
         ArrayList arrayList2 = Lists.newArrayList();

         for (int i6 = 0; i6 < inv.getSizeInventory(); i6++) {
            ItemStack itemStack4 = inv.getStackInSlot(i6);
            if (itemStack4 != null) {
               if (itemStack4.getItem() == Items.dye) {
                  arrayList2.add(ItemDye.dyeColors[itemStack4.getMetadata() & 15]);
               } else if (itemStack4.getItem() == Items.firework_charge) {
                  this.field_92102_a = itemStack4.copy();
                  this.field_92102_a.stackSize = 1;
               }
            }
         }

         int[] ints2 = new int[arrayList2.size()];

         for (int i7 = 0; i7 < ints2.length; i7++) {
            ints2[i7] = (Integer)arrayList2.get(i7);
         }

         if (this.field_92102_a != null && this.field_92102_a.hasTagCompound()) {
            NBTTagCompound nbtTagCompound5 = this.field_92102_a.getTagCompound().getCompoundTag("Explosion");
            if (nbtTagCompound5 == null) {
               return false;
            }

            nbtTagCompound5.setIntArray("FadeColors", ints2);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
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
