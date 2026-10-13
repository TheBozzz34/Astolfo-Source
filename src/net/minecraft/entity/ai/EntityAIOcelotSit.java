package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIOcelotSit extends EntityAIMoveToBlock {
   private final EntityOcelot ocelot;
   private static final long a = 32406947746096L;
   private static final long d = 7792198756694229000L;

   public EntityAIOcelotSit(EntityOcelot var1, double var2) {
      super(var1, var2, (int)d);
      this.ocelot = var1;
   }

   @Override
   public boolean shouldExecute() {
      return this.ocelot.isTamed() && !this.ocelot.isSitting() && super.shouldExecute();
   }

   @Override
   public boolean continueExecuting() {
      return super.continueExecuting();
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
      this.ocelot.getAISit().setSitting(false);
   }

   @Override
   public void resetTask() {
      super.resetTask();
      this.ocelot.setSitting(false);
   }

   @Override
   public void updateTask() {
      super.updateTask();
      this.ocelot.getAISit().setSitting(false);
      if (!this.getIsAboveDestination()) {
         this.ocelot.setSitting(false);
      } else if (!this.ocelot.isSitting()) {
         this.ocelot.setSitting(true);
      }
   }

   @Override
   public boolean shouldMoveTo(World var1, BlockPos var2) {
      if (!var1.isAirBlock(var2.up())) {
         return false;
      }

      IBlockState var6 = var1.getBlockState(var2);
      Block var7 = var6.getBlock();
      if (var7 == Blocks.chest) {
         TileEntity var8 = var1.getTileEntity(var2);
         if (var8 instanceof TileEntityChest && ((TileEntityChest)var8).numPlayersUsing < 1) {
            return true;
         }
      } else {
         if (var7 == Blocks.lit_furnace) {
            return true;
         }

         if (var7 == Blocks.bed && var6.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD) {
            return true;
         }
      }

      return false;
   }
}
