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

   public WeightedRandomChestContent(Item item, int i, int minimumChance, int maximumChance, int itemWeightIn) {
      super(itemWeightIn);
      this.theItemId = new ItemStack(item, 1, i);
      this.minStackSize = minimumChance;
      this.maxStackSize = maximumChance;
   }

   public WeightedRandomChestContent(ItemStack stack, int minimumChance, int maximumChance, int itemWeightIn) {
      super(itemWeightIn);
      this.theItemId = stack;
      this.minStackSize = minimumChance;
      this.maxStackSize = maximumChance;
   }

   public static void generateChestContents(Random randomIn, List<WeightedRandomChestContent> listIn, IInventory inv, int maxIn) {
      for (int i = 0; i < maxIn; i++) {
         WeightedRandomChestContent weightedRandomChestContent = WeightedRandom.getRandomItem(randomIn, listIn);
         int j = weightedRandomChestContent.minStackSize + randomIn.nextInt(weightedRandomChestContent.maxStackSize - weightedRandomChestContent.minStackSize + 1);
         if (weightedRandomChestContent.theItemId.getMaxStackSize() >= j) {
            ItemStack itemStack = weightedRandomChestContent.theItemId.copy();
            itemStack.stackSize = j;
            inv.setInventorySlotContents(randomIn.nextInt(inv.getSizeInventory()), itemStack);
         } else {
            for (int k = 0; k < j; k++) {
               ItemStack itemStack2 = weightedRandomChestContent.theItemId.copy();
               itemStack2.stackSize = 1;
               inv.setInventorySlotContents(randomIn.nextInt(inv.getSizeInventory()), itemStack2);
            }
         }
      }
   }

   public static void generateDispenserContents(Random randomIn, List<WeightedRandomChestContent> listIn, TileEntityDispenser dispenser, int maxIn) {
      for (int i = 0; i < maxIn; i++) {
         WeightedRandomChestContent weightedRandomChestContent = WeightedRandom.getRandomItem(randomIn, listIn);
         int j = weightedRandomChestContent.minStackSize + randomIn.nextInt(weightedRandomChestContent.maxStackSize - weightedRandomChestContent.minStackSize + 1);
         if (weightedRandomChestContent.theItemId.getMaxStackSize() >= j) {
            ItemStack itemStack = weightedRandomChestContent.theItemId.copy();
            itemStack.stackSize = j;
            dispenser.setInventorySlotContents(randomIn.nextInt(dispenser.getSizeInventory()), itemStack);
         } else {
            for (int k = 0; k < j; k++) {
               ItemStack itemStack2 = weightedRandomChestContent.theItemId.copy();
               itemStack2.stackSize = 1;
               dispenser.setInventorySlotContents(randomIn.nextInt(dispenser.getSizeInventory()), itemStack2);
            }
         }
      }
   }

   public static List<WeightedRandomChestContent> func_177629_a(
      List<WeightedRandomChestContent> list, WeightedRandomChestContent... weightedRandomChestContents
   ) {
      ArrayList arrayList = Lists.newArrayList(list);
      Collections.addAll(arrayList, weightedRandomChestContents);
      return arrayList;
   }
}
