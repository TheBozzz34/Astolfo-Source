package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class WeightedRandomChestContent extends WeightedRandom.Item {
   private ItemStack theItemId;
   private int minStackSize;
   private int maxStackSize;
   private static final long a = 10702279433997L;

   public WeightedRandomChestContent(Item var1, int var2, int var3, int var4, int var5) {
      super(var5);
      this.theItemId = new ItemStack(var1, 1, var2);
      this.minStackSize = var3;
      this.maxStackSize = var4;
   }

   public WeightedRandomChestContent(ItemStack var1, int var2, int var3, int var4) {
      super(var4);
      this.theItemId = var1;
      this.minStackSize = var2;
      this.maxStackSize = var3;
   }

   public static void generateChestContents(Random var0, List<WeightedRandomChestContent> var1, IInventory var2, int var3) {
      for (int var7 = 0; var7 < var3; var7++) {
         WeightedRandomChestContent var8 = WeightedRandom.getRandomItem(var0, var1);
         int var9 = var8.minStackSize + var0.nextInt(var8.maxStackSize - var8.minStackSize + 1);
         if (var8.theItemId.getMaxStackSize() >= var9) {
            ItemStack var12 = var8.theItemId.copy();
            var12.stackSize = var9;
            var2.setInventorySlotContents(var0.nextInt(var2.getSizeInventory()), var12);
         } else {
            for (int var10 = 0; var10 < var9; var10++) {
               ItemStack var11 = var8.theItemId.copy();
               var11.stackSize = 1;
               var2.setInventorySlotContents(var0.nextInt(var2.getSizeInventory()), var11);
            }
         }
      }
   }

   public static void generateDispenserContents(Random var0, List<WeightedRandomChestContent> var1, TileEntityDispenser var2, int var3) {
      for (int var7 = 0; var7 < var3; var7++) {
         WeightedRandomChestContent var8 = WeightedRandom.getRandomItem(var0, var1);
         int var9 = var8.minStackSize + var0.nextInt(var8.maxStackSize - var8.minStackSize + 1);
         if (var8.theItemId.getMaxStackSize() >= var9) {
            ItemStack var12 = var8.theItemId.copy();
            var12.stackSize = var9;
            var2.setInventorySlotContents(var0.nextInt(var2.getSizeInventory()), var12);
         } else {
            for (int var10 = 0; var10 < var9; var10++) {
               ItemStack var11 = var8.theItemId.copy();
               var11.stackSize = 1;
               var2.setInventorySlotContents(var0.nextInt(var2.getSizeInventory()), var11);
            }
         }
      }
   }

   public static List<WeightedRandomChestContent> func_177629_a(List<WeightedRandomChestContent> var0, WeightedRandomChestContent... var1) {
      ArrayList var2 = Lists.newArrayList(var0);
      Collections.addAll(var2, var1);
      return var2;
   }
}
