package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
   private final IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();
   private static final long K = 52586829071031L;
   private static final long gb = 4728592064697074665L;

   @Override
   public IBehaviorDispenseItem getBehavior(ItemStack var1) {
      return this.dropBehavior;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityDropper();
   }

   @Override
   public void dispense(World var1, BlockPos var2) {
      BlockSourceImpl var6 = new BlockSourceImpl(var1, var2);
      TileEntityDispenser var7 = var6.getBlockTileEntity();
      if (var7 != null) {
         int var8 = var7.getDispenseSlot();
         if (var8 < 0) {
            var1.playAuxSFX((int)gb, var2, 0);
         } else {
            ItemStack var9 = var7.getStackInSlot(var8);
            if (var9 != null) {
               EnumFacing var10 = var1.getBlockState(var2).getValue(FACING);
               BlockPos var11 = var2.offset(var10);
               IInventory var12 = TileEntityHopper.getInventoryAtPosition(var1, var11.getX(), var11.getY(), var11.getZ());
               ItemStack var13;
               if (var12 == null) {
                  var13 = this.dropBehavior.dispense(var6, var9);
                  if (var13 != null && var13.stackSize <= 0) {
                     var13 = null;
                  }
               } else {
                  var13 = TileEntityHopper.putStackInInventoryAllSlots(var12, var9.copy().splitStack(1), var10.getOpposite());
                  if (var13 == null) {
                     var13 = var9.copy();
                     if (--var13.stackSize <= 0) {
                        var13 = null;
                     }
                  } else {
                     var13 = var9.copy();
                  }
               }

               var7.setInventorySlotContents(var8, var13);
            }
         }
      }
   }
}
