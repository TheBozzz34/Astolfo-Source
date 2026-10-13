package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
   public static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockSlab.EnumBlockHalf.class);
   private static final long e = 108853380053266L;

   public BlockSlab(Material var1) {
      super(var1);
      if (this.isDouble()) {
         this.fullBlock = true;
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

      this.setLightOpacity(255);
   }

   @Override
   public boolean canSilkHarvest() {
      return false;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      if (this.isDouble()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         IBlockState var6 = var1.getBlockState(var2);
         if (var6.getBlock() == this) {
            if (var6.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
               this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
         }
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      if (this.isDouble()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBlockBoundsBasedOnState(var1, var2);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean isOpaqueCube() {
      return this.isDouble();
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var12 = super.onBlockPlaced(var1, var2, var3, var4, var5, var6, var7, var8).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      return this.isDouble()
         ? var12
         : (var3 == EnumFacing.DOWN || var3 != EnumFacing.UP && !(var5 <= 0.5) ? var12.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP) : var12);
   }

   @Override
   public int quantityDropped(Random var1) {
      return this.isDouble() ? 2 : 1;
   }

   @Override
   public boolean isFullCube() {
      return this.isDouble();
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      if (this.isDouble()) {
         return super.shouldSideBeRendered(var1, var2, var3);
      }

      if (var3 != EnumFacing.UP && var3 != EnumFacing.DOWN && !super.shouldSideBeRendered(var1, var2, var3)) {
         return false;
      }

      BlockPos var7 = var2.offset(var3.getOpposite());
      IBlockState var8 = var1.getBlockState(var2);
      IBlockState var9 = var1.getBlockState(var7);
      boolean var10 = isSlab(var8.getBlock()) && var8.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
      boolean var11 = isSlab(var9.getBlock()) && var9.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
      return var11
         ? (
            var3 == EnumFacing.DOWN
               ? true
               : (var3 == EnumFacing.UP && super.shouldSideBeRendered(var1, var2, var3) ? true : !isSlab(var8.getBlock()) || !var10)
         )
         : (var3 == EnumFacing.UP ? true : (var3 == EnumFacing.DOWN && super.shouldSideBeRendered(var1, var2, var3) ? true : !isSlab(var8.getBlock()) || var10));
   }

   public static boolean isSlab(Block var0) {
      return var0 == Blocks.stone_slab || var0 == Blocks.wooden_slab || var0 == Blocks.stone_slab2;
   }

   public abstract String getUnlocalizedName(int var1);

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      return super.getDamageValue(var1, var2) & 7;
   }

   public abstract boolean isDouble();

   public abstract IProperty<?> getVariantProperty();

   public abstract Object getVariant(ItemStack var1);

   public enum EnumBlockHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      EnumBlockHalf(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
