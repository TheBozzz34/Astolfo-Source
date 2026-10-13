package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
   private final EntityVillager theVillager;
   private boolean hasFarmItem;
   private boolean field_179503_e;
   private int field_179501_f;
   private static final long a = 128994329980416L;

   public EntityAIHarvestFarmland(EntityVillager var1, double var2) {
      super(var1, var2, 16);
      this.theVillager = var1;
   }

   @Override
   public boolean shouldExecute() {
      if (this.runDelay <= 0) {
         if (!this.theVillager.worldObj.getGameRules().getBoolean("mobGriefing")) {
            return false;
         }

         this.field_179501_f = -1;
         this.hasFarmItem = this.theVillager.isFarmItemInInventory();
         this.field_179503_e = this.theVillager.func_175557_cr();
      }

      return super.shouldExecute();
   }

   @Override
   public boolean continueExecuting() {
      return this.field_179501_f >= 0 && super.continueExecuting();
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
   }

   @Override
   public void resetTask() {
      super.resetTask();
   }

   @Override
   public void updateTask() {
      super.updateTask();
      this.theVillager
         .getLookHelper()
         .setLookPosition(
            this.destinationBlock.getX() + 0.5,
            this.destinationBlock.getY() + 1,
            this.destinationBlock.getZ() + 0.5,
            10.0F,
            this.theVillager.getVerticalFaceSpeed()
         );
      if (this.getIsAboveDestination()) {
         World var4 = this.theVillager.worldObj;
         BlockPos var5 = this.destinationBlock.up();
         IBlockState var6 = var4.getBlockState(var5);
         Block var7 = var6.getBlock();
         if (this.field_179501_f == 0 && var7 instanceof BlockCrops && var6.getValue(BlockCrops.AGE) == 7) {
            var4.destroyBlock(var5, true);
         } else if (this.field_179501_f == 1 && var7 == Blocks.air) {
            InventoryBasic var8 = this.theVillager.getVillagerInventory();

            for (int var9 = 0; var9 < var8.getSizeInventory(); var9++) {
               ItemStack var10 = var8.getStackInSlot(var9);
               boolean var11 = false;
               if (var10 != null) {
                  if (var10.getItem() == Items.wheat_seeds) {
                     var4.setBlockState(var5, Blocks.wheat.getDefaultState(), 3);
                     var11 = true;
                  } else if (var10.getItem() == Items.potato) {
                     var4.setBlockState(var5, Blocks.potatoes.getDefaultState(), 3);
                     var11 = true;
                  } else if (var10.getItem() == Items.carrot) {
                     var4.setBlockState(var5, Blocks.carrots.getDefaultState(), 3);
                     var11 = true;
                  }
               }

               if (var11) {
                  var10.stackSize--;
                  if (var10.stackSize <= 0) {
                     var8.setInventorySlotContents(var9, (ItemStack)null);
                  }
                  break;
               }
            }
         }

         this.field_179501_f = -1;
         this.runDelay = 10;
      }
   }

   @Override
   public boolean shouldMoveTo(World var1, BlockPos var2) {
      Block var6 = var1.getBlockState(var2).getBlock();
      if (var6 == Blocks.farmland) {
         var2 = var2.up();
         IBlockState var7 = var1.getBlockState(var2);
         var6 = var7.getBlock();
         if (var6 instanceof BlockCrops && var7.getValue(BlockCrops.AGE) == 7 && this.field_179503_e && (this.field_179501_f == 0 || this.field_179501_f < 0)) {
            this.field_179501_f = 0;
            return true;
         }

         if (var6 == Blocks.air && this.hasFarmItem && (this.field_179501_f == 1 || this.field_179501_f < 0)) {
            this.field_179501_f = 1;
            return true;
         }
      }

      return false;
   }
}
