package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random RANDOM = new Random();
   private static final long a = 11133481835387L;

   public static void dropInventoryItems(World var0, BlockPos var1, IInventory var2) {
      dropInventoryItems(var0, var1.getX(), var1.getY(), var1.getZ(), var2);
   }

   public static void dropInventoryItems(World var0, Entity var1, IInventory var2) {
      dropInventoryItems(var0, var1.posX, var1.posY, var1.posZ, var2);
   }

   private static void dropInventoryItems(World var0, double var1, double var3, double var5, IInventory var7) {
      for (int var11 = 0; var11 < var7.getSizeInventory(); var11++) {
         ItemStack var12 = var7.getStackInSlot(var11);
         if (var12 != null) {
            spawnItemStack(var0, var1, var3, var5, var12);
         }
      }
   }

   private static void spawnItemStack(World var0, double var1, double var3, double var5, ItemStack var7) {
      float var11 = RANDOM.nextFloat() * 0.8F + 0.1F;
      float var12 = RANDOM.nextFloat() * 0.8F + 0.1F;
      float var13 = RANDOM.nextFloat() * 0.8F + 0.1F;

      while (var7.stackSize > 0) {
         int var14 = RANDOM.nextInt(21) + 10;
         if (var14 > var7.stackSize) {
            var14 = var7.stackSize;
         }

         var7.stackSize -= var14;
         EntityItem var15 = new EntityItem(var0, var1 + var11, var3 + var12, var5 + var13, new ItemStack(var7.getItem(), var14, var7.getMetadata()));
         if (var7.hasTagCompound()) {
            var15.getEntityItem().setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
         }

         float var16 = 0.05F;
         var15.motionX = RANDOM.nextGaussian() * var16;
         var15.motionY = RANDOM.nextGaussian() * var16 + 0.2F;
         var15.motionZ = RANDOM.nextGaussian() * var16;
         var0.spawnEntityInWorld(var15);
      }
   }
}
