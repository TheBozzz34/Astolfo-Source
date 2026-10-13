package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
   private final boolean isOn;
   private static final long e = 33624545015804L;

   public BlockRedstoneOre(boolean var1) {
      super(Material.rock);
      if (var1) {
         this.setTickRandomly(true);
      }

      this.isOn = var1;
   }

   @Override
   public int tickRate(World var1) {
      return 30;
   }

   @Override
   public void onBlockClicked(World var1, BlockPos var2, EntityPlayer var3) {
      this.activate(var1, var2);
      super.onBlockClicked(var1, var2, var3);
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, Entity var3) {
      this.activate(var1, var2);
      super.onEntityCollidedWithBlock(var1, var2, var3);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      this.activate(var1, var2);
      return super.onBlockActivated(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private void activate(World var1, BlockPos var2) {
      this.spawnParticles(var1, var2);
      if (this == Blocks.redstone_ore) {
         var1.setBlockState(var2, Blocks.lit_redstone_ore.getDefaultState());
      }
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this == Blocks.lit_redstone_ore) {
         var1.setBlockState(var2, Blocks.redstone_ore.getDefaultState());
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.redstone;
   }

   @Override
   public int quantityDroppedWithBonus(int var1, Random var2) {
      return this.quantityDropped(var2) + var2.nextInt(var1 + 1);
   }

   @Override
   public int quantityDropped(Random var1) {
      return 4 + var1.nextInt(2);
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5);
      if (this.getItemDropped(var3, var1.rand, var5) != Item.getItemFromBlock(this)) {
         int var8 = 1 + var1.rand.nextInt(5);
         this.dropXpOnBlockBreak(var1, var2, var8);
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.isOn) {
         this.spawnParticles(var1, var2);
      }
   }

   private void spawnParticles(World var1, BlockPos var2) {
      Random var6 = var1.rand;
      double var7 = 0.0625;

      for (int var9 = 0; var9 < 6; var9++) {
         double var10 = var2.getX() + var6.nextFloat();
         double var12 = var2.getY() + var6.nextFloat();
         double var14 = var2.getZ() + var6.nextFloat();
         if (var9 == 0 && !var1.getBlockState(var2.up()).getBlock().isOpaqueCube()) {
            var12 = var2.getY() + var7 + 1.0;
         }

         if (var9 == 1 && !var1.getBlockState(var2.down()).getBlock().isOpaqueCube()) {
            var12 = var2.getY() - var7;
         }

         if (var9 == 2 && !var1.getBlockState(var2.south()).getBlock().isOpaqueCube()) {
            var14 = var2.getZ() + var7 + 1.0;
         }

         if (var9 == 3 && !var1.getBlockState(var2.north()).getBlock().isOpaqueCube()) {
            var14 = var2.getZ() - var7;
         }

         if (var9 == 4 && !var1.getBlockState(var2.east()).getBlock().isOpaqueCube()) {
            var10 = var2.getX() + var7 + 1.0;
         }

         if (var9 == 5 && !var1.getBlockState(var2.west()).getBlock().isOpaqueCube()) {
            var10 = var2.getX() - var7;
         }

         if (var10 < var2.getX() || var10 > var2.getX() + 1 || var12 < 0.0 || var12 > var2.getY() + 1 || var14 < var2.getZ() || var14 > var2.getZ() + 1) {
            var1.spawnParticle(EnumParticleTypes.REDSTONE, var10, var12, var14, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public ItemStack createStackedBlock(IBlockState var1) {
      return new ItemStack(Blocks.redstone_ore);
   }
}
