package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool EYE = PropertyBool.create("eye");
   private static final long e = 45909674762323L;
   private static final long i = -6494092449651097585L;

   public BlockEndPortalFrame() {
      super(Material.rock, MapColor.greenColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EYE, false));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      if (var1.getBlockState(var2).getValue(EYE)) {
         this.setBlockBounds(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }

      this.setBlockBoundsForItemRender();
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return null;
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing().getOpposite()).withProperty(EYE, false);
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getValue(EYE) ? (int)i : 0;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(EYE, (var1 & 4) != 0).withProperty(FACING, EnumFacing.getHorizontal(var1 & 3));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getHorizontalIndex();
      if (var1.getValue(EYE)) {
         var5 |= 4;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, EYE);
   }
}
