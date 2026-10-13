package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);
   private static final long e = 113884937358325L;
   private static final long i = 4167424058847133711L;

   public BlockCocoa() {
      super(Material.plants);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(AGE, 0));
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!this.canBlockStay(var1, var2, var3)) {
         this.dropBlock(var1, var2, var3);
      } else if (var1.rand.nextInt(5) == 0) {
         int var8 = var3.getValue(AGE);
         if (var8 < 2) {
            var1.setBlockState(var2, var3.withProperty(AGE, var8 + 1), 2);
         }
      }
   }

   public boolean canBlockStay(World var1, BlockPos var2, IBlockState var3) {
      var2 = var2.offset(var3.getValue(FACING));
      IBlockState var7 = var1.getBlockState(var2);
      return var7.getBlock() == Blocks.log && var7.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
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
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getSelectedBoundingBox(var1, var2);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      EnumFacing var7 = var6.getValue(FACING);
      int var8 = var6.getValue(AGE);
      int var9 = 4 + var8 * 2;
      int var10 = 5 + var8 * 2;
      float var11 = var9 / 2.0F;
      switch (var7) {
         case SOUTH:
            this.setBlockBounds((8.0F - var11) / 16.0F, (12.0F - var10) / 16.0F, (15.0F - var9) / 16.0F, (8.0F + var11) / 16.0F, 0.75F, 0.9375F);
            break;
         case NORTH:
            this.setBlockBounds((8.0F - var11) / 16.0F, (12.0F - var10) / 16.0F, 0.0625F, (8.0F + var11) / 16.0F, 0.75F, (1.0F + var9) / 16.0F);
            break;
         case WEST:
            this.setBlockBounds(0.0625F, (12.0F - var10) / 16.0F, (8.0F - var11) / 16.0F, (1.0F + var9) / 16.0F, 0.75F, (8.0F + var11) / 16.0F);
            break;
         case EAST:
            this.setBlockBounds((15.0F - var9) / 16.0F, (12.0F - var10) / 16.0F, (8.0F - var11) / 16.0F, 0.9375F, 0.75F, (8.0F + var11) / 16.0F);
      }
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      EnumFacing var6 = EnumFacing.fromAngle(var4.rotationYaw);
      var1.setBlockState(var2, var3.withProperty(FACING, var6), 2);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (!var3.getAxis().isHorizontal()) {
         var3 = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, var3.getOpposite()).withProperty(AGE, 0);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.canBlockStay(var1, var2, var3)) {
         this.dropBlock(var1, var2, var3);
      }
   }

   private void dropBlock(World var1, BlockPos var2, IBlockState var3) {
      var1.setBlockState(var2, Blocks.air.getDefaultState(), 3);
      this.dropBlockAsItem(var1, var2, var3, 0);
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      int var9 = var3.getValue(AGE);
      byte var10 = 1;
      if (var9 >= 2) {
         var10 = 3;
      }

      for (int var11 = 0; var11 < var10; var11++) {
         spawnAsEntity(var1, var2, new ItemStack(Items.dye, 1, EnumDyeColor.BROWN.getDyeDamage()));
      }
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.dye;
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      return EnumDyeColor.BROWN.getDyeDamage();
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var3.getValue(AGE) < 2;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      var1.setBlockState(var3, var4.withProperty(AGE, var4.getValue(AGE) + 1), 2);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(var1)).withProperty(AGE, (var1 & (int)i) >> 2);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 |= var1.getValue(FACING).getHorizontalIndex();
      return var2 | var1.getValue(AGE) << 2;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, AGE);
   }
}
