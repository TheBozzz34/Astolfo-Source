package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   private final boolean canDrop;
   private static final long e = 11753762983719L;

   public BlockPane(Material var1, boolean var2) {
      super(var1);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false)
      );
      this.canDrop = var2;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(NORTH, this.canPaneConnectToBlock(var2.getBlockState(var3.north()).getBlock()))
         .withProperty(SOUTH, this.canPaneConnectToBlock(var2.getBlockState(var3.south()).getBlock()))
         .withProperty(WEST, this.canPaneConnectToBlock(var2.getBlockState(var3.west()).getBlock()))
         .withProperty(EAST, this.canPaneConnectToBlock(var2.getBlockState(var3.east()).getBlock()));
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return !this.canDrop ? null : super.getItemDropped(var1, var2, var3);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var1.getBlockState(var2).getBlock() == this ? false : super.shouldSideBeRendered(var1, var2, var3);
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      boolean var10 = this.canPaneConnectToBlock(var1.getBlockState(var2.north()).getBlock());
      boolean var11 = this.canPaneConnectToBlock(var1.getBlockState(var2.south()).getBlock());
      boolean var12 = this.canPaneConnectToBlock(var1.getBlockState(var2.west()).getBlock());
      boolean var13 = this.canPaneConnectToBlock(var1.getBlockState(var2.east()).getBlock());
      if ((!var12 || !var13) && (var12 || var13 || var10 || var11)) {
         if (var12) {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
         } else if (var13) {
            this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
         }
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }

      if ((!var10 || !var11) && (var12 || var13 || var10 || var11)) {
         if (var10) {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
         } else if (var11) {
            this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
         }
      } else {
         this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      float var6 = 0.4375F;
      float var7 = 0.5625F;
      float var8 = 0.4375F;
      float var9 = 0.5625F;
      boolean var10 = this.canPaneConnectToBlock(var1.getBlockState(var2.north()).getBlock());
      boolean var11 = this.canPaneConnectToBlock(var1.getBlockState(var2.south()).getBlock());
      boolean var12 = this.canPaneConnectToBlock(var1.getBlockState(var2.west()).getBlock());
      boolean var13 = this.canPaneConnectToBlock(var1.getBlockState(var2.east()).getBlock());
      if ((!var12 || !var13) && (var12 || var13 || var10 || var11)) {
         if (var12) {
            var6 = 0.0F;
         } else if (var13) {
            var7 = 1.0F;
         }
      } else {
         var6 = 0.0F;
         var7 = 1.0F;
      }

      if ((!var10 || !var11) && (var12 || var13 || var10 || var11)) {
         if (var10) {
            var8 = 0.0F;
         } else if (var11) {
            var9 = 1.0F;
         }
      } else {
         var8 = 0.0F;
         var9 = 1.0F;
      }

      this.setBlockBounds(var6, 0.0F, var8, var7, 1.0F, var9);
   }

   public final boolean canPaneConnectToBlock(Block var1) {
      return var1.isFullBlock()
         || var1 == this
         || var1 == Blocks.glass
         || var1 == Blocks.stained_glass
         || var1 == Blocks.stained_glass_pane
         || var1 instanceof BlockPane;
   }

   @Override
   public boolean canSilkHarvest() {
      return true;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, WEST, SOUTH);
   }
}
