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

   @Override
   public IBehaviorDispenseItem getBehavior(ItemStack stack) {
      return this.dropBehavior;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityDropper();
   }

   @Override
   public void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blockSourceImpl = new BlockSourceImpl(worldIn, pos);
      TileEntityDispenser tileEntityDispenser = blockSourceImpl.getBlockTileEntity();
      if (tileEntityDispenser != null) {
         int i = tileEntityDispenser.getDispenseSlot();
         if (i < 0) {
            worldIn.playAuxSFX(1001, pos, 0);
         } else {
            ItemStack itemStack = tileEntityDispenser.getStackInSlot(i);
            if (itemStack != null) {
               EnumFacing enumFacing = worldIn.getBlockState(pos).getValue(FACING);
               BlockPos blockPos = pos.offset(enumFacing);
               IInventory iInventory = TileEntityHopper.getInventoryAtPosition(worldIn, blockPos.getX(), blockPos.getY(), blockPos.getZ());
               ItemStack itemStack2;
               if (iInventory == null) {
                  itemStack2 = this.dropBehavior.dispense(blockSourceImpl, itemStack);
                  if (itemStack2 != null && itemStack2.stackSize <= 0) {
                     itemStack2 = null;
                  }
               } else {
                  itemStack2 = TileEntityHopper.putStackInInventoryAllSlots(iInventory, itemStack.copy().splitStack(1), enumFacing.getOpposite());
                  if (itemStack2 == null) {
                     itemStack2 = itemStack.copy();
                     if (--itemStack2.stackSize <= 0) {
                        itemStack2 = null;
                     }
                  } else {
                     itemStack2 = itemStack.copy();
                  }
               }

               tileEntityDispenser.setInventorySlotContents(i, itemStack2);
            }
         }
      }
   }
}
