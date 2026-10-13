package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   private static final long e = 42513022916739L;

   public BlockCactus() {
      super(Material.cactus);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      BlockPos var8 = var2.up();
      if (var1.isAirBlock(var8)) {
         int var9 = 1;

         while (var1.getBlockState(var2.down(var9)).getBlock() == this) {
            var9++;
         }

         if (var9 < 3) {
            int var10 = var3.getValue(AGE);
            if (var10 == 15) {
               var1.setBlockState(var8, this.getDefaultState());
               IBlockState var11 = var3.withProperty(AGE, 0);
               var1.setBlockState(var2, var11, 4);
               this.onNeighborBlockChange(var1, var8, var11, this);
            } else {
               var1.setBlockState(var2, var3.withProperty(AGE, var10 + 1), 4);
            }
         }
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      float var4 = 0.0625F;
      return new AxisAlignedBB(var2.getX() + var4, var2.getY(), var2.getZ() + var4, var2.getX() + 1 - var4, var2.getY() + 1 - var4, var2.getZ() + 1 - var4);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      float var3 = 0.0625F;
      return new AxisAlignedBB(var2.getX() + var3, var2.getY(), var2.getZ() + var3, var2.getX() + 1 - var3, var2.getY() + 1, var2.getZ() + 1 - var3);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return super.canPlaceBlockAt(var1, var2) ? this.canBlockStay(var1, var2) : false;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.canBlockStay(var1, var2)) {
         var1.destroyBlock(var2, true);
      }
   }

   public boolean canBlockStay(World var1, BlockPos var2) {
      for (Object var7 : EnumFacing.Plane.HORIZONTAL) {
         if (var1.getBlockState(var2.offset((EnumFacing)var7)).getBlock().getMaterial().isSolid()) {
            return false;
         }
      }

      Block var8 = var1.getBlockState(var2.down()).getBlock();
      return var8 == Blocks.cactus || var8 == Blocks.sand;
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      var4.attackEntityFrom(DamageSource.cactus, 1.0F);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(AGE, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE);
   }
}
