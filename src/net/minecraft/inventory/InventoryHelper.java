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

   public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
      dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
   }

   public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
      dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
   }

   private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack itemStack = inventory.getStackInSlot(i);
         if (itemStack != null) {
            spawnItemStack(worldIn, x, y, z, itemStack);
         }
      }
   }

   private static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
      float f = RANDOM.nextFloat() * 0.8F + 0.1F;
      float g = RANDOM.nextFloat() * 0.8F + 0.1F;
      float h = RANDOM.nextFloat() * 0.8F + 0.1F;

      while (stack.stackSize > 0) {
         int i = RANDOM.nextInt(21) + 10;
         if (i > stack.stackSize) {
            i = stack.stackSize;
         }

         stack.stackSize -= i;
         EntityItem entityItem = new EntityItem(worldIn, x + f, y + g, z + h, new ItemStack(stack.getItem(), i, stack.getMetadata()));
         if (stack.hasTagCompound()) {
            entityItem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
         }

         float f2 = 0.05F;
         entityItem.motionX = RANDOM.nextGaussian() * f2;
         entityItem.motionY = RANDOM.nextGaussian() * f2 + 0.2F;
         entityItem.motionZ = RANDOM.nextGaussian() * f2;
         worldIn.spawnEntityInWorld(entityItem);
      }
   }
}
