package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   private Block isFull;
   private static final long a = 126655832521920L;
   private static final long d = 4115068489113272328L;

   public ItemBucket(Block var1) {
      this.maxStackSize = 1;
      this.isFull = var1;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      boolean var7 = this.isFull == Blocks.air;
      MovingObjectPosition var8 = this.getMovingObjectPositionFromPlayer(var2, var3, var7);
      if (var8 == null) {
         return var1;
      }

      if (var8.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos var9 = var8.getBlockPos();
         if (!var2.isBlockModifiable(var3, var9)) {
            return var1;
         }

         if (var7) {
            if (!var3.canPlayerEdit(var9.offset(var8.sideHit), var8.sideHit, var1)) {
               return var1;
            }

            IBlockState var10 = var2.getBlockState(var9);
            Material var11 = var10.getBlock().getMaterial();
            if (var11 == Material.water && var10.getValue(BlockLiquid.LEVEL) == 0) {
               var2.setBlockToAir(var9);
               var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return this.fillBucket(var1, var3, Items.water_bucket);
            }

            if (var11 == Material.lava && var10.getValue(BlockLiquid.LEVEL) == 0) {
               var2.setBlockToAir(var9);
               var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return this.fillBucket(var1, var3, Items.lava_bucket);
            }
         } else {
            if (this.isFull == Blocks.air) {
               return new ItemStack(Items.bucket);
            }

            BlockPos var12 = var9.offset(var8.sideHit);
            if (!var3.canPlayerEdit(var12, var8.sideHit, var1)) {
               return var1;
            }

            if (this.tryPlaceContainedLiquid(var2, var12) && !var3.capabilities.isCreativeMode) {
               var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return new ItemStack(Items.bucket);
            }
         }
      }

      return var1;
   }

   private ItemStack fillBucket(ItemStack var1, EntityPlayer var2, Item var3) {
      if (var2.capabilities.isCreativeMode) {
         return var1;
      }

      if (--var1.stackSize <= 0) {
         return new ItemStack(var3);
      }

      if (!var2.inventory.addItemStackToInventory(new ItemStack(var3))) {
         var2.dropPlayerItemWithRandomChoice(new ItemStack(var3, 1, 0), false);
      }

      return var1;
   }

   public boolean tryPlaceContainedLiquid(World var1, BlockPos var2) {
      if (this.isFull == Blocks.air) {
         return false;
      }

      Material var6 = var1.getBlockState(var2).getBlock().getMaterial();
      boolean var7 = !var6.isSolid();
      if (!var1.isAirBlock(var2) && !var7) {
         return false;
      }

      if (var1.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
         int var8 = var2.getX();
         int var9 = var2.getY();
         int var10 = var2.getZ();
         var1.playSoundEffect(var8 + 0.5F, var9 + 0.5F, var10 + 0.5F, "random.fizz", 0.5F, 2.6F + (var1.rand.nextFloat() - var1.rand.nextFloat()) * 0.8F);

         for (int var11 = 0; var11 < (int)d; var11++) {
            var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var8 + Math.random(), var9 + Math.random(), var10 + Math.random(), 0.0, 0.0, 0.0);
         }
      } else {
         if (!var1.isRemote && var7 && !var6.isLiquid()) {
            var1.destroyBlock(var2, true);
         }

         var1.setBlockState(var2, this.isFull.getDefaultState(), 3);
      }

      return true;
   }

   public Block N() {
      return this.isFull;
   }
}
